package uk.co.kring.ef396.data;

import uk.co.kring.ef396.data.components.Application;
import uk.co.kring.ef396.data.components.Game;
import uk.co.kring.ef396.data.components.ImageCanvas;
import uk.co.kring.ef396.data.streams.TypedStream;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;

public class Data {

    public static String jar;
    public static String name;
    public static String version;

    @FunctionalInterface
    public interface Exec {

        void run(String[] args) throws IOException;
    }

    public static final String FILE = "SAVE AS NAME";
    public static final String ARCH = "ARCHIVE NAME";
    public static final String COMMAND = "COMMAND STRING";
    public static final String DIRS = "DIRECTORY NAME";
    public static final String REPEATS = "...";

    public static final String TAR = "tar cf - ";
    public static final String UN_TAR = "tar xvf - ";

    public static void exitCode(Error code) {
        System.err.print("[" + code.ordinal() + "] ");
        System.err.println(code.text + " error in " + name + " tools causing premature exit.");
        System.exit(code.ordinal());
    }

    public static void exitCode(int exit, Error code) {
        if(exit != 0) exitCode(code);
    }

    public static int tar(String[] dirs, OutputStream arch) throws IOException {
        return execute(TAR + Arrays.stream(dirs).reduce((s, t) -> s + " " + t), System.in, arch);
    }

    public static int unTar(InputStream arch) throws IOException {
        return execute(UN_TAR, arch, System.out);
    }

    public static int execute(String command, InputStream in, OutputStream out) throws IOException {
        ProcessBuilder builder = new ProcessBuilder(command);
        Process p = builder. /* directory(new File("~")). */ start();
        InputStream is = p.getInputStream();
        InputStream es = p.getErrorStream();
        OutputStream os = p.getOutputStream();
        FilePipe.Task j1 = FilePipe.cloneStream(in, os);
        FilePipe.Task j2 = FilePipe.cloneStream(is, out);
        FilePipe.Task j3 = FilePipe.cloneStream(es, System.err);
        j1.rejoin();
        j2.rejoin();
        j3.rejoin();
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
        return FilePipe.readStream(FilePipe.getInputStream(new File(file)));
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
            if(dialog()) return FilePipe.writeStream(FilePipe.getOutputStream(f));
        }
        throw new IOException("Can't save file as it already exists.");
    }

    public enum Error {
        NONE("No"),
        DOCUMENTATION("Documentation"),
        DEFAULT("Unimplemented catch"),
        URL("Unexpected URI format for name"),
        USED("Used command made an error"),
        TAR("Tar"),
        UN_TAR("Un-tar");

        private final String text;

        Error(String text) {
            this.text = text;
        }
    }

    public enum Command {
        //TODO create repo table hashes for signature store
        REPO_GIT('g', "clone git signature repository",
                (args) -> {}, new String[]{""}, false),
        REPO_NFT('n', "get NFT url for user",
                (args) -> { }, new String[]{""}, false),

        //TODO a mini game
        GAME('p', "play mini game", (args) -> {
            Game g = new Game();
            while(g.isVisible()) Thread.yield();
        }, new String[]{""}, false),

        //main functions
        ARCHIVE('a', "archive", (args) -> {
            String[] dirs = shift(args);
            TypedStream.Output os = FilePipe.getOutputStream(new File(args[0]));
            if(os.getFilePipe().isTarable()) {
                exitCode(tar(dirs, FilePipe.writeStream(os)), Error.TAR);
            } else {
                exitCode(Error.TAR);
            }
        }, new String[]{ FILE, DIRS }, true),
        EXTRACT('x', "extract", (args) -> {
            TypedStream.Input is = FilePipe.getInputStream(new File(args[0]));
            if(is.getFilePipe().isTarable()) {
                exitCode(unTar(FilePipe.readStream(is)), Error.UN_TAR);
            } else {
                exitCode(Error.UN_TAR);
            }
        }, new String[]{ ARCH }, false),
        LOAD('l', "common load dialog", (args) -> {
            FilePipe.cloneStream(loadDialog(), System.out).rejoin();
        }, new String[]{ }, false),
        SAVE('s', "common save dialog", (args) -> {
            FilePipe.cloneStream(System.in, saveDialog()).rejoin();
        }, new String[]{ }, false),
        COMPRESS('c', "compress file", (args) -> {
            FilePipe.cloneStream(System.in,
                    FilePipe.writeStream(FilePipe.getOutputStream(new File(args[0])))).rejoin();
        }, new String[]{ FILE }, false),
        EXPAND('e', "expand file", (args) -> {
            FilePipe.cloneStream(
                    FilePipe.readStream(FilePipe.getInputStream(new File(args[0]))), System.out).rejoin();
        }, new String[]{ ARCH }, false),
        VERSION('v', "version information", (args) -> {
            System.out.println(version);
        }, new String[]{ }, false),
        HELP('h', "help with options", (args) -> {
            if(args == null) {
                System.out.println("Try one of the following options for the first argument");
                show(null, false);
            } else {
                System.out.println("Help for option " + args[0]);
                show(args[0], true);
            }
        }, new String[]{ }, false),
        USE('u', "use command process on file to file", (args) -> {
            exitCode(execute(args[2], FilePipe.readStream(FilePipe.getInputStream(new File(args[0]))),
                    FilePipe.writeStream(FilePipe.getOutputStream(new File(args[1])))), Error.USED);
        }, new String[]{ ARCH, FILE, COMMAND }, false),
        TRANSCODE('t', "transcode from file to file", (args) -> {
            FilePipe.cloneStream(FilePipe.readStream(FilePipe.getInputStream(new File(args[0]))),
                    FilePipe.writeStream(FilePipe.getOutputStream(new File(args[1]))));
        }, new String[]{ ARCH, FILE }, false),
        IMAGE('i', "image load and view", (args) -> {
            FilePipe.readComponent(FilePipe.getInputStream(new File(args[0])))
                    .ifPresent((image) -> {
                        imageCanvas((BufferedImage) image);//create if possible
                    });
        }, new String[]{ ARCH }, false);

        private final char option;
        private final String description;
        private final Exec action;
        private final String[] params;
        private final boolean repeats;

        Command(char option, String description, Exec action, String[] params, boolean repeats) {
            this.option = option;
            this.description = description;
            this.action = action;
            this.params = params;
            this.repeats = repeats;
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
                System.out.print("> ");// end
                if(c.repeats) System.out.print(REPEATS);
                System.out.println();//new line
                if(c.description == null) throw new IOException("Needs documentation");
                System.out.println("\t" + c.description);
            }
        }
    }

    public static String[] shift(String[] args) {
        String[] s = new String[args.length - 1];
        System.arraycopy(args, 1, s, 0, args.length - 1);
        return s;
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
            exitCode(Error.URL);//unexpected naming problem
        }
        if(args.length >= 1 && args[0].length() == 1) {
            for (Command c: Command.values()) {
                if(args[0].charAt(0) == c.option) {
                    try {
                        c.action.run(shift(args));
                    } catch(Exception e) {
                        System.err.println(e.getMessage());
                        exitCode(Error.DEFAULT);//default err
                    }
                    return;//assuming no exit code
                }
            }
        } else {
            try {
                Command.HELP.action.run(null);//doesn't need it
            } catch(Exception e) {
                exitCode(Error.DOCUMENTATION);//documentation
            }
        }
    }
}
