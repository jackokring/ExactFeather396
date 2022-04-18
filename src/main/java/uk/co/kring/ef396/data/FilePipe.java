package uk.co.kring.ef396.data;

import uk.co.kring.ef396.data.backend.Pipe;
import uk.co.kring.ef396.data.streams.TypedStream;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public enum FilePipe {

    GZIP("gz", Pipe.GZIP, null),
    RLE("rle", Pipe.RLE, null),
    SPARSE("spa", Pipe.ZLE_GZIP, null),//almost useless except on very sparse data
    BGZ("bgz", Pipe.BWT_GZIP, null),//quite a good compromise
    LZW("w24", Pipe.LZW, null),//fast but not as effective, adapted for inverted symbols
    BLWZ("blwz",Pipe.BWT_LZW_GZIP, null),//slower but packs symbol repeats as zeros for GZ
    PNG("png", Pipe.MANGLER, FilePipe::registerImageComponent),
    JPG("jpg", Pipe.MANGLER, FilePipe::registerImageComponent),
    NULL("", Pipe.NULL, null);

    private final String extension;
    private final Pipe uses;

    FilePipe(String extension, Pipe uses, Consumer<FilePipe> transforms) {
        this.extension = extension;
        this.uses = uses;
        if(transforms != null) transforms.accept(this);
    }

    public boolean matches(String name) {
        return new Perl().anyOrNone(true)
                .optionOrRepeated().append(".").append(extension).matches(name);
    }

    public static FilePipe filePipeForName(String name) {
        for (FilePipe p: FilePipe.values()) {
            if(p.matches(name)) return p;
        }
        return FilePipe.NULL;
    }

    //====================== FILES AND STREAMS ======================

    public static TypedStream.Input getInputStream(File name) throws IOException {
        FilePipe p = filePipeForName(name.getName());
        return new TypedStream.Input(p.uses.getStream(new FileInputStream(name)), p);
    }

    public static TypedStream.Output getOutputStream(File name) throws IOException {
        FilePipe p = filePipeForName(name.getName());
        return new TypedStream.Output(p.uses.getStream(new FileOutputStream(name)), p);
    }

    public static TypedStream.Input getInputStream(Socket ip, FilePipe fp) throws IOException {
        return new TypedStream.Input(fp.uses.getStream(ip.getInputStream()), fp);
    }

    public static TypedStream.Output getOutputStream(Socket ip, FilePipe fp) throws IOException {
        return new TypedStream.Output(fp.uses.getStream(ip.getOutputStream()), fp);
    }

    public static TypedStream.Input getInputStream(InputStream in, FilePipe fp) throws IOException {
        return new TypedStream.Input(fp.uses.getStream(in), fp);
    }

    public static TypedStream.Output getOutputStream(OutputStream out, FilePipe fp) throws IOException {
        return new TypedStream.Output(fp.uses.getStream(out), fp);
    }

    public static void cloneStream(InputStream in, OutputStream out) throws IOException {
        int b = -1;
        while((b = in.read()) != -1)  {
            out.write(b);
        }
    }

    //====================== COMPONENT AUTOMATICS =========================

    @FunctionalInterface
    public interface CheckedFunction<T, R> {
        R apply(T t) throws IOException;
    }

    @FunctionalInterface
    public interface CheckedBiFunction<T, X, R> {
        R apply(T t, X x) throws IOException;
    }

    @FunctionalInterface
    public interface CheckedBiConsumer<T, R> {
        void accept(T t, R r) throws IOException;
    }

    //============= SINGULAR COMPONENT REGISTRATION ========================

    private static final HashMap<FilePipe, CheckedFunction<TypedStream.Input, Object>> ins = new HashMap<>();

    private static final HashMap<FilePipe, CheckedBiConsumer<TypedStream.Output, Object>> outs = new HashMap<>();

    public static void registerInputComponent(FilePipe fp,
                                              CheckedFunction<TypedStream.Input, Object> transform) {
        ins.put(fp, transform);
    }

    public static void registerOutputComponent(FilePipe fp,
                                               CheckedBiConsumer<TypedStream.Output, Object> transform) {
        outs.put(fp, transform);
    }

    private static final HashMap<FilePipe,
            CheckedBiFunction<Object, FilePipe, TypedStream.Input>> inMan = new HashMap<>();

    private static final HashMap<FilePipe,
            CheckedFunction<TypedStream.Input, Object>> outMan = new HashMap<>();

    public static void registerInputMangler(FilePipe fp,
                                            CheckedBiFunction<Object, FilePipe, TypedStream.Input> transform) {
        inMan.put(fp, transform);
    }

    public static void registerOutputMangler(FilePipe fp,
                                             CheckedFunction<TypedStream.Input, Object> transform) {
        outMan.put(fp, transform);
    }

    //======================== COMPONENT HANDLERS ================================

    public static Optional<Object> readComponent(TypedStream.Input in) throws IOException {
        var x = ins.get(in.getFilePipe());
        if(x == null) return Optional.empty();
        return Optional.of(x.apply(in));
    }

    public static TypedStream.Input readStream(TypedStream.Input in) throws IOException {
        if(in.getFilePipe().uses == Pipe.MANGLER) {
            AtomicReference<TypedStream.Input> ret = new AtomicReference<>();
            readComponent(in).ifPresent((comp) -> {
                var x = inMan.get(in.getFilePipe());
                try {
                    if (x == null) throw new IOException();
                    ret.set(x.apply(comp, in.getFilePipe()));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            return ret.get();
        } else {
            return in;//pass through
        }
    }

    public static void writeComponent(TypedStream.Output out, Object thing) throws IOException {
        var x = outs.get(out.getFilePipe());
        if(x == null) throw new IOException("No component writer for "
                + out.getFilePipe().getClass().getCanonicalName());
        x.accept(out, thing);
    }

    public static TypedStream.Output writeStream(TypedStream.Output out) throws IOException {
        if(out.getFilePipe().uses == Pipe.MANGLER) {
            var x = outMan.get(out.getFilePipe());
            if(x == null) throw new IOException("Component not available");
            PipedOutputStream pos = new PipedOutputStream();
            Object obj = x.apply(new TypedStream.Input(new PipedInputStream(pos), out.getFilePipe()));
            writeComponent(out, obj);
            return new TypedStream.Output(pos, out.getFilePipe());
        } else {
            return out;
        }
    }

    //======================= IMAGE COMPONENT =========================

    private static Object getImage(TypedStream.Input in) throws IOException {
        return ImageIO.read(in);
    }

    private static void putImage(TypedStream.Output out, Object image) throws IOException {
        ImageIO.write((BufferedImage)image, out.getFilePipe().extension, out);
    }

    //============================ RASTER FORMAT =======================

    private static TypedStream.Input getImage(Object in, FilePipe fp) throws IOException {
        BufferedImage bim = (BufferedImage) in;
        //raster basis
        PipedOutputStream pos = new PipedOutputStream();
        DataOutputStream dos = new DataOutputStream(pos);
        new Thread(() -> {
            try {
                dos.writeInt(bim.getWidth());
                dos.writeInt(bim.getHeight());
                for (int y = 0; y < bim.getHeight(); y++) {
                    for (int x = 0; x < bim.getWidth(); x++) {
                        dos.writeInt(bim.getRGB(x, y));
                    }
                }
            } catch(Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
        return new TypedStream.Input(new PipedInputStream(pos), fp);
    }

    private static Object putImage(TypedStream.Input in) throws IOException {
        DataInputStream dis = new DataInputStream(in);
        BufferedImage out = new BufferedImage(dis.readInt(), dis.readInt(), BufferedImage.TYPE_4BYTE_ABGR);
        //raster basis
        for(int y = 0; y < out.getHeight(); y++) {
            for(int x = 0; x < out.getWidth(); x++) {
                out.setRGB(x, y, dis.readInt());
            }
        }
        return dis;
    }

    //============ BUILT-IN IMAGE FORMATS ===============

    private static void registerImageComponent(FilePipe fp) {
        registerInputComponent(fp, FilePipe::getImage);
        registerInputMangler(fp, FilePipe::getImage);
        registerOutputComponent(fp, FilePipe::putImage);
        registerOutputMangler(fp, FilePipe::putImage);
    }
}
