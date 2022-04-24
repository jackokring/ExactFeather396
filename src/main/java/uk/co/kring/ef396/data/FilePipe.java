package uk.co.kring.ef396.data;

import net.sourceforge.jaad.spi.javasound.AACAudioFileReader;
import uk.co.kring.ef396.data.backend.Pipe;
import uk.co.kring.ef396.data.streams.LocalDataStream;
import uk.co.kring.ef396.data.streams.TypedStream;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.function.Consumer;

public enum FilePipe {

    GZIP("gz", Pipe.GZIP, true, null),
    RLE("rle", Pipe.RLE, false, null),
    SPARSE("spa", Pipe.ZLE_GZIP, false, null),//almost useless except on very sparse data
    BGZ("bgz", Pipe.BWT_GZIP, true, null),//quite a good compromise
    LZW("w24", Pipe.LZW, false, null),//fast but not as effective, adapted for inverted symbols
    BLWZ("blwz",Pipe.BWT_LZW_GZIP, true, null),//slower but packs symbol repeats as zeros for GZ
    PNG("png", Pipe.MANGLER, false, FilePipe::registerImageComponent),
    JPG("jpg", Pipe.MANGLER, false, FilePipe::registerImageComponent),
    WAVE(AudioFileFormat.Type.WAVE.getExtension(), Pipe.MANGLER, false,
            FilePipe::registerAudioInputStreamComponent),
    AIFF(AudioFileFormat.Type.AIFF.getExtension(), Pipe.MANGLER, false,
            FilePipe::registerAudioInputStreamComponent),
    AIFC(AudioFileFormat.Type.AIFC.getExtension(), Pipe.MANGLER, false,
            FilePipe::registerAudioInputStreamComponent),
    AU(AudioFileFormat.Type.AU.getExtension(), Pipe.MANGLER, false,
            FilePipe::registerAudioInputStreamComponent),
    SND(AudioFileFormat.Type.SND.getExtension(), Pipe.MANGLER, false,
            FilePipe::registerAudioInputStreamComponent),
    M4A("m4a", Pipe.MANGLER, false,
            FilePipe::registerAAudioInputStreamComponent),
    AAC("aac", Pipe.MANGLER, false,
            FilePipe::registerAAudioInputStreamComponent),
    NULL("", Pipe.NULL, false, null);

    private final String extension;
    private final Pipe uses;
    private final boolean tarable;

    FilePipe(String extension, Pipe uses, boolean tarable, Consumer<FilePipe> transforms) {
        this.extension = extension;
        this.uses = uses;
        this.tarable = tarable;
        if(transforms != null) transforms.accept(this);
    }

    public boolean isTarable() {
        return tarable;
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
        return new TypedStream.Input(p.uses.getStream(new FileInputStream(name)), p, null);
    }

    public static TypedStream.Output getOutputStream(File name) throws IOException {
        FilePipe p = filePipeForName(name.getName());
        return new TypedStream.Output(p.uses.getStream(new FileOutputStream(name)), p, null);
    }

    public static TypedStream.Input getInputStream(Socket ip) throws IOException {
        return getInputStream(ip.getInputStream());
    }

    public static TypedStream.Output getOutputStream(Socket ip, FilePipe fp) throws IOException {
        return getOutputStream(ip.getOutputStream(), fp);
    }

    public static TypedStream.Input getInputStream(InputStream in) throws IOException {
        LocalDataStream.Input dis = new LocalDataStream.Input(in);
        int length = 0;
        for (FilePipe s: values()) {
            if(s.extension.length() > length) length = s.extension.length();
        }
        String ext = dis.safeReadUTF(length);//baulk on bad type early
        FilePipe fp = filePipeForName("." + ext);
        return new TypedStream.Input(fp.uses.getStream(in), fp, null);
    }

    public static TypedStream.Output getOutputStream(OutputStream out, FilePipe fp) throws IOException {
        LocalDataStream.Output dos = new LocalDataStream.Output(out);
        dos.writeUTF(fp.extension);
        return new TypedStream.Output(fp.uses.getStream(dos), fp, null);
    }

    //========================= CLONE ==============================

    public static class Task extends Thread {

        IOException error = null;

        public synchronized void setError(IOException e) {
            error = e;
        }
        public synchronized IOException getError() {
            return error;
        }

        public Task() {
            super();
        }

        public void rejoin() throws IOException {//remove unnecessary interrupted condition
            try {
                join();
            } catch (Exception e) {
                if(error == null) {
                    error = new IOException("Interrupted");
                } else {
                    error = new IOException(error);
                }
            }
            if(error != null) throw error;
        }
    }

    public static Task cloneStream(InputStream in, OutputStream out,
                                   boolean closeOut) {
        Task thread = new Task() {
            @Override
            public void run() {
                int b = -1;
                try {
                    while ((b = in.read()) != -1) {
                        out.write(b);
                    }
                    if(closeOut) out.close();
                    in.close();
                } catch (IOException e) {
                    setError(e);
                }
            }
        };
        thread.start();
        return thread;
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

    private static final HashMap<FilePipe,
            CheckedFunction<TypedStream.Input, Object>> ins = new HashMap<>();

    private static final HashMap<FilePipe,
            CheckedBiConsumer<TypedStream.Output, Object>> outs = new HashMap<>();

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

    public static Object readComponent(TypedStream.Input in) throws IOException {
        var x = ins.get(in.getFilePipe());
        if(x == null) throw new IOException("No component reader");
        return x.apply(in);
    }

    public static TypedStream.Input readStream(TypedStream.Input in) throws IOException {
        if(in.getFilePipe().uses.isMangler()) {
            var x = inMan.get(in.getFilePipe());
            if(x == null) throw new IOException("No component read mangler");
            return x.apply(readComponent(in), in.getFilePipe());
        } else {
            return in;//pass through
        }
    }

    public static void writeComponent(TypedStream.Output out, Object thing) throws IOException {
        var x = outs.get(out.getFilePipe());
        if(x == null) throw new IOException("No component writer");
        x.accept(out, thing);
    }

    public static TypedStream.Output writeStream(TypedStream.Output out) throws IOException {
        if(out.getFilePipe().uses.isMangler()) {
            var x = outMan.get(out.getFilePipe());
            if(x == null) throw new IOException("No component write mangler");
            PipedOutputStream pos = new PipedOutputStream();
            Object obj = x.apply(new TypedStream.Input(
                    new PipedInputStream(pos), out.getFilePipe(), null));
            writeComponent(out, obj);
            return new TypedStream.Output(pos, out.getFilePipe(), out.getTask());
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

    //======================= AUDIO COMPONENT ==========================

    public enum Format {
        WAVE(AudioFileFormat.Type.WAVE),
        AIFF(AudioFileFormat.Type.AIFF),
        AIFC(AudioFileFormat.Type.AIFC),
        AU(AudioFileFormat.Type.AU),
        SND(AudioFileFormat.Type.SND);

        private final String ext;
        private final AudioFileFormat.Type aff;

        Format(AudioFileFormat.Type aff) {
            this.ext = aff.getExtension();
            this.aff = aff;
        }

        public static AudioFileFormat.Type getFileFormat(FilePipe fp) throws IOException {
            for (Format f: Format.values()) {
                if(f.ext.equals(fp.extension)) return f.aff;
            }
            throw new IOException("Bad audio format");
        }
    }

    private static Object getAudio(TypedStream.Input in) throws IOException {
        try {
            return AudioSystem.getAudioInputStream(in);
        } catch(Exception e) {
            throw new IOException(e);
        }
    }

    private static void putAudio(TypedStream.Output out, Object audio) throws IOException {
        try {
            AudioSystem.write((AudioInputStream) audio, Format.getFileFormat(out.getFilePipe()), out);
        } catch(Exception e) {
            throw new IOException(e);
        }
    }

    //============================ AAC COMPONENT =======================

    private static Object getAAudio(TypedStream.Input in) throws IOException {
        AACAudioFileReader aac = new AACAudioFileReader();
        try {
            return aac.getAudioInputStream(in);
        } catch(Exception e) {
            throw new IOException(e);
        }
    }

    private static void putAAudio(TypedStream.Output out, Object audio) throws IOException {
        throw new IOException("No aac write support");
    }

    //============================ RASTER FORMAT =======================

    private static TypedStream.Input getImage(Object in, FilePipe fp) throws IOException {
        BufferedImage bim = (BufferedImage) in;
        //raster basis
        PipedOutputStream pos = new PipedOutputStream();
        LocalDataStream.Output dos = new LocalDataStream.Output(pos);
        Task t = new Task() {
            @Override
            public void run() {
                try {
                    dos.writeInt(bim.getWidth());
                    dos.writeInt(bim.getHeight());
                    for (int y = 0; y < bim.getHeight(); y++) {
                        for (int x = 0; x < bim.getWidth(); x++) {
                            dos.writeInt(bim.getRGB(x, y));
                        }
                    }
                    dos.closeActual();//as object to stream
                } catch (IOException e) {
                    setError(e);
                }
            }
        };
        t.start();
        return new TypedStream.Input(new PipedInputStream(pos), fp, t);
    }

    private static Object putImage(TypedStream.Input in) throws IOException {
        BufferedImage out = new BufferedImage(in.readInt(), in.readInt(), BufferedImage.TYPE_4BYTE_ABGR);
        //raster basis
        for(int y = 0; y < out.getHeight(); y++) {
            for(int x = 0; x < out.getWidth(); x++) {
                out.setRGB(x, y, in.readInt());
            }
        }
        in.close();
        return out;//return raster image
    }

    //============================ AUDIO RASTER FORMAT ========================

    public final static AudioFormat X = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
            44100, 16, 2, 4, 44100, true);

    private static TypedStream.Input getAudio(Object in, FilePipe fp) throws IOException {
        AudioInputStream is = AudioSystem.getAudioInputStream(X, (AudioInputStream) in);//standardize format;
        PipedOutputStream pos = new PipedOutputStream();
        Task t = new Task() {
            @Override
            public void run() {
                try {
                    byte[] frame = new byte[X.getFrameSize()];
                    while (is.read(frame) != -1) {
                        pos.write(frame);//ordering? should be big-endian interleaved CD audio
                    }
                    is.close();
                    pos.close();
                } catch (IOException e) {
                    setError(e);
                }
            }
        };
        t.start();
        return new TypedStream.Input(new PipedInputStream(pos), fp, t);
    }

    private static Object putAudio(TypedStream.Input in) throws IOException {
        AudioInputStream out;
        byte[] b = in.readAllBytes();//all file?
        in.close();
        try {
            //adding header
            out = new AudioInputStream(new ByteArrayInputStream(b), X, b.length);
        } catch(Exception e) {
            throw new IOException(e);
        }
        return out;//return raster audio
    }

    //============ BUILT-IN IMAGE FORMATS ===============

    private static void registerImageComponent(FilePipe fp) {
        registerInputComponent(fp, FilePipe::getImage);
        registerInputMangler(fp, FilePipe::getImage);
        registerOutputComponent(fp, FilePipe::putImage);
        registerOutputMangler(fp, FilePipe::putImage);
    }

    private static void registerAudioInputStreamComponent(FilePipe fp) {
        registerInputComponent(fp, FilePipe::getAudio);
        registerInputMangler(fp, FilePipe::getAudio);//CD standard
        registerOutputComponent(fp, FilePipe::putAudio);
        registerOutputMangler(fp, FilePipe::putAudio);//actually any valid CD expected?
    }

    private static void registerAAudioInputStreamComponent(FilePipe fp) {
        registerInputComponent(fp, FilePipe::getAAudio);
        registerInputMangler(fp, FilePipe::getAudio);//CD standard
        registerOutputComponent(fp, FilePipe::putAAudio);
    }
}
