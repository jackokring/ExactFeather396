package uk.co.kring.ef396.data;

import uk.co.kring.ef396.data.components.Application;
import uk.co.kring.ef396.data.components.ImageCanvas;
import uk.co.kring.ef396.data.streams.TypedStream;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.*;

public class Data {

    public static String jar;
    public static String name;
    public static String version;

    @FunctionalInterface
    public interface Exec {

        void run(String[] args) throws IOException;
    }

    public static final String FILE = "FILE NAME";
    public static final String ARCH = "ARCHIVE NAME";
    public static final String COMMAND = "COMMAND STRING";

    public static void exitCode(int code) {
        if(code != 0) {
            System.err.print("[" + code + "] ");
            System.err.println("Error in data tools causing premature exit.");
        }
        System.exit(code);
    }

    public static int execute(String command, InputStream in, OutputStream out) throws IOException {
        ProcessBuilder builder = new ProcessBuilder(command);
        Process p = builder. /* directory(new File("~")). */ start();
        InputStream is = p.getInputStream();
        InputStream es = p.getErrorStream();
        OutputStream os = p.getOutputStream();
        Thread j1 = new Thread(() -> {
            try {
                FilePipe.cloneStream(in, os);
            } catch (IOException e) {
                System.err.println(e.getMessage());
                throw new RuntimeException("Input stream failure read.");
            }
        });
        j1.start();
        Thread j2 = new Thread(() -> {
            try {
                FilePipe.cloneStream(is, out);
            } catch (IOException e) {
                System.err.println(e.getMessage());
                throw new RuntimeException("Output stream failure write.");
            }
        });
        j2.start();
        Thread j3 = new Thread(() -> {
            try {
                FilePipe.cloneStream(es, System.err);
            } catch (IOException e) {
                System.err.println(e.getMessage());
                throw new RuntimeException("Error stream failure write.");
            }
        });
        j3.start();
        try {// have to join to prevent stream problems on exit value and no stream clone completion
            j1.join();
            j2.join();
            j3.join();
        } catch(Exception e) {
            System.err.println(e.getMessage());
            throw new IOException("Process join error.");
        }
        return p.exitValue();
    }

    public static void imageCanvas(BufferedImage image) {
        Application a = new Application(new ImageCanvas(image, "Image"));
        while(a.isVisible()) Thread.yield();//stay open to show
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
            FilePipe.cloneStream(loadDialog(), System.out);
        }, new String[]{ }),
        SAVE('s', "save dialog", (args) -> {
            FilePipe.cloneStream(System.in, saveDialog());
        }, new String[]{ }),
        COMPRESS('c', "compress", (args) -> {
            FilePipe.cloneStream(System.in, FilePipe.getOutputStream(new File(args[0])));
        }, new String[]{ FILE }),
        EXPAND('e', "expand", (args) -> {
            FilePipe.cloneStream(FilePipe.getInputStream(new File(args[0])), System.out);
        }, new String[]{ ARCH }),
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
        USE('u', "use", (args) -> {
            exitCode(execute(args[2], FilePipe.getInputStream(new File(args[0])),
                    FilePipe.getOutputStream(new File(args[1]))));
        }, new String[]{ ARCH, FILE, COMMAND }),
        IMAGE('i', "image load", (args) -> {
            FilePipe.readComponent(FilePipe.getInputStream(new File(args[0])))
                    .ifPresent((image) -> {
                        imageCanvas((BufferedImage) image);//create if possible
                    });
        }, new String[]{ ARCH });

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
                System.out.print(name + " " + c.option + " <");
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
        try {
            jar = Data.class.getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI()
                    .getPath();
            name = jar.substring(jar.lastIndexOf("/") + 1);
            int starts = name.lastIndexOf("-") + 1;
            version = name.substring(starts, name.lastIndexOf("."));
        } catch(Exception e) {
            System.err.println(e.getMessage());
            exitCode(-2);//unexpected naming problem
        }
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
