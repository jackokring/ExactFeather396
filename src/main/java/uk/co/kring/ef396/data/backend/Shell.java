package uk.co.kring.ef396.data.backend;

import uk.co.kring.ef396.data.Data;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Shell {

    //shell abstractions for command executive
    public static int exec(String command, InputStream in, OutputStream out, boolean closeOut) throws IOException {
        return Data.execute(command, in, out, closeOut);
    }
}
