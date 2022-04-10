package uk.co.kring.ef396.data;

import uk.co.kring.ef396.utilities.Perl;

import java.io.*;

public enum FilePipe {

    GZIP("gz", Pipe.GZIP),
    RLE("rle", Pipe.RLE),
    RLE_GZIP("rle.gz", Pipe.GZIP),
    //TODO
    NULL("", Pipe.NULL);

    private final String extension;
    private final Pipe uses;

    FilePipe(String extension, Pipe uses) {
        this.extension = extension;
        this.uses = uses;
    }

    public boolean matches(String name) {
        return new Perl().anyOrNone(true)
                .optionOrRepeated().append(".").append(extension).matches(name);
    }

    public boolean matches(File name) {
        return matches(name.getName());
    }

    public static Pipe pipeForName(String name) {
        for (FilePipe p: FilePipe.values()) {
            if(p.matches(name)) return p.uses;
        }
        return Pipe.NULL;
    }

    public static DataInputStream getInputStream(File name) throws IOException {
        return pipeForName(name.getName()).getStream(new FileInputStream(name));
    }

    public static DataOutputStream getOutputStream(File name) throws IOException {
        return pipeForName(name.getName()).getStream(new FileOutputStream(name));
    }
}
