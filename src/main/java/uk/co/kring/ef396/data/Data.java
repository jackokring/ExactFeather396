package uk.co.kring.ef396.data;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Data {

    @FunctionalInterface
    public interface Exec {

        void run(String[] args) throws IOException;
    }

    public static final String version = "1.0.0";

    public static final String FILE = "FILE NAME";
    public static final String ARCHIVE = "ARCHIVE NAME";

    private static InputStream in = System.in;
    private static OutputStream out = System.out;

    public static void exitCode(int code) {
        System.err.print("[" + code + "] ");
        System.err.println("Error in data tools causing premature exit.");
        System.exit(code);
    }

    public static void imageCanvas(TypedStream.Input in) {
        //TODO
    }

    public static TypedStream.Input loadDialog() throws IOException {
        FileDialog fd = new FileDialog((Frame) null, "Load", FileDialog.LOAD);
        fd.setFilenameFilter((dir, name) -> FilePipe.filePipeForName(name) != FilePipe.NULL);
        fd.setDirectory("~");//home
        fd.setVisible(true);
        String file = fd.getFile();
        return FilePipe.getInputStream(new File(file));
    }

    public static boolean dialog() {
        Dialog ok = new Dialog((Frame) null, "Replace", true);
        final boolean[] closed = {false};
        ok.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                closed[0] = true;
                ok.setVisible(false);
            }
        });
        Label text = new Label("File exists. Replace file? Close for No or Cancel.");
        Button yes = new Button("OK");
        yes.addActionListener(actionEvent -> {
            //default false
            ok.setVisible(false);//close
        });
        ok.add(text);
        ok.add(yes);
        ok.setVisible(true);//modal
        return !closed[0];
    }

    public static TypedStream.Output saveDialog() throws IOException {
        FileDialog fd = new FileDialog((Frame) null, "Save", FileDialog.SAVE);
        fd.setFilenameFilter((dir, name) -> FilePipe.filePipeForName(name) != FilePipe.NULL);
        fd.setDirectory("~");//home
        fd.setVisible(true);
        String file = fd.getFile();
        File f = new File(file);
        if(f.exists()) {
            if(dialog()) return FilePipe.getOutputStream(f);
        }
        throw new IOException("Can't save file as it already exists.");
    }

    public enum Command {
        ARCHIVE('a', "archive", (args) -> {  }, new String[]{""}),
        EXTRACT('x', "extract", (args) -> {  }, new String[]{""}),
        LOAD('l', "load dialog", (args) -> {
            in = loadDialog();//TODO
        }, new String[]{ }),
        SAVE('s', "save dialog", (args) -> {
            out = saveDialog();//TODO
        }, new String[]{ }),
        COMPRESS('c', "compress", (args) -> {  }, new String[]{ FILE }),
        EXPAND('e', "expand", (args) -> {  }, new String[]{ FILE }),
        VERSION('v', "version", (args) -> {
            System.out.println(version);
        }, new String[]{ }),
        HELP('h', "help with options", (args) -> {
            if(args == null) {
                System.out.println("Try one of the following options for the first argument");
                show(null, false);
            } else {
                System.out.println("Help for option " + args[0]);
                show(args[0], true);
            }
        }, new String[]{ }),
        USE('u', "use", (args) -> {  }, new String[]{""}),
        IMAGE('i', "image load", (args) -> {
            //TODO
        }, new String[]{ FILE });

        private final char option;
        private final String description;
        private final Exec action;
        private final String[] params;

        Command(char option, String description, Exec action, String[] params) {
            this.option = option;
            this.description = description;
            this.action = action;
            this.params = params;
        }

        private static void show(String arg, boolean singular) throws IOException {
            for (Command c: Command.values()) {
                if(singular && !arg.equals("" + c.option)) continue;//skip
                System.out.print("data " + c.option + " <");
                boolean printed = true;
                for (String s: c.params) {
                    if(!printed) System.out.print("> <");
                    printed = false;
                    System.out.print(s);
                }
                System.out.println(">");// end
                if(c.description == null) throw new IOException("Needs documentation");
                System.out.println("\t" + c.description);
            }
        }
    }

    public static void main(String[] args) {
        if(args.length >= 1 && args[0].length() == 1) {
            for (Command c: Command.values()) {
                if(args[0].charAt(0) == c.option) {
                    String[] s = new String[args.length - 1];
                    System.arraycopy(args, 1, s, 0, args.length - 1);
                    try {
                        c.action.run(s);
                    } catch(Exception e) {
                        System.err.println(e.getMessage());
                        exitCode(-1);//default err
                    }
                    return;//assuming no exit code
                }
            }
        } else {
            try {
                Command.HELP.action.run(null);//doesn't need it
            } catch(Exception e) {
                exitCode(1);//documentation
            }
        }
    }
}
