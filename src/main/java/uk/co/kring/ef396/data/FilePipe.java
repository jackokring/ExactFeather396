package uk.co.kring.ef396.data;

import uk.co.kring.ef396.data.backend.Pipe;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.Consumer;

public enum FilePipe {

    GZIP("gz", Pipe.GZIP, null),
    RLE("rle", Pipe.RLE, null),
    RLE_GZIP("rle.gz", Pipe.RLE_GZIP, null),//almost useless except on very sparse data
    BWT_GZIP("bgz", Pipe.BWT_GZIP, null),
    LZW("lzw", Pipe.LZW, null),
    BWT_LZW_ZLE_GZIP("blwz",Pipe.BWT_LZW_ZLE_GZIP, null),
    //TODO
    PNG("png", Pipe.NULL, FilePipe::registerImageComponent),
    JPG("jpg", Pipe.NULL, FilePipe::registerImageComponent),
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
        return new TypedStream.Input(ip.getInputStream(), fp);
    }

    public static TypedStream.Output getOutputStream(Socket ip, FilePipe fp) throws IOException {
        return new TypedStream.Output(ip.getOutputStream(), fp);
    }

    //====================== COMPONENT AUTOMATICS =========================

    @FunctionalInterface
    public interface CheckedFunction<T, R> {
        R apply(T t) throws IOException;
    }

    @FunctionalInterface
    public interface CheckedBiConsumer<T, R> {
        void accept(T t, R r) throws IOException;
    }

    private static final HashMap<FilePipe,
            HashMap<Class, CheckedFunction<TypedStream.Input, Object>>> ins = new HashMap<>();

    private static final HashMap<FilePipe,
            HashMap<Class, CheckedBiConsumer<TypedStream.Output, Object>>> outs = new HashMap<>();

    public static void registerInputComponent(FilePipe fp,
                                              Class idx, CheckedFunction<TypedStream.Input, Object> transform) {
        var x = ins.computeIfAbsent(fp,
                (func) -> new HashMap<>());
        x.put(idx, transform);
    }

    public static void registerOutputComponent(FilePipe fp,
                                               Class idx, CheckedBiConsumer<TypedStream.Output, Object> transform) {
        var x = outs.computeIfAbsent(fp,
                (func) -> new HashMap<>());
        x.put(idx, transform);
    }

    public static Optional<Object> readComponent(TypedStream.Input in, Class clazz) throws IOException {
        var x = ins.get(in.getFilePipe());
        if(x == null) return Optional.empty();
        var y = x.get(clazz);
        if(y == null) return Optional.empty();
        return Optional.of(y.apply(in));
    }

    public static void writeComponent(TypedStream.Output out, Object thing) throws IOException {
        var x = outs.get(out.getFilePipe());
        if(x == null) throw new IOException("No component writer for "
                + out.getFilePipe().getClass().getCanonicalName());
        var y = x.get(thing.getClass());
        if(y == null) throw new IOException("No component writer for "
                + thing.getClass().getCanonicalName());
        y.accept(out, thing);
    }

    //======================= IMAGE COMPONENT =========================

    private static BufferedImage getImage(TypedStream.Input in) throws IOException {
        return ImageIO.read(in);
    }

    private static void putImage(TypedStream.Output out, Object image) throws IOException {
        ImageIO.write((BufferedImage)image, out.getFilePipe().extension, out);
    }

    private static void registerImageComponent(FilePipe fp) {
        registerInputComponent(fp, BufferedImage.class, FilePipe::getImage);
        registerOutputComponent(fp, BufferedImage.class, FilePipe::putImage);
    }
}
