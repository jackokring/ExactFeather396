package uk.co.kring.ef396.data;

import uk.co.kring.ef396.data.components.Application;
import uk.co.kring.ef396.data.components.ExecButton;
import uk.co.kring.ef396.data.components.Game;
import uk.co.kring.ef396.data.components.ImageCanvas;
import uk.co.kring.ef396.data.streams.SignedStream;
import uk.co.kring.ef396.data.streams.TypedStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
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
    public static final String VERSION_LIKE = "VERSION CODE";
    public static final String GIT_URL = "GIT REPOSITORY";
    public static final String REPEATS = "...";

    public static final String TAR = "tar cf - ";
    public static final String UN_TAR = "tar xvf - ";
    public static final String GIT = "git clone ";

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
        if(in == null) in = System.in;
        if(out == null) out = System.out;
        FilePipe.Task j1 = FilePipe.cloneStream(in, os);
        FilePipe.Task j2 = FilePipe.cloneStream(is, out);
        FilePipe.Task j3 = FilePipe.cloneStream(es, System.err);
        j1.rejoin();
        j2.rejoin();
        j3.rejoin();
        return p.exitValue();
    }

    public static void imageCanvas(BufferedImage image) throws IOException {
        Application a = new Application(new ImageCanvas(image, "Image"));
        while(a.isVisible()) Thread.yield();//stay open to show
    }

    public static Clip playAudioClip(AudioInputStream audio) throws IOException {
        Clip clip;
        try {
            clip = AudioSystem.getClip();
            // Open audio clip and load samples from the audio input stream.
            clip.open(audio);
            clip.start();
        } catch(Exception e) {
            throw new IOException(e);
        }
        return clip;
    }

    public static void audioCanvas(AudioInputStream audio) throws IOException {
        Application a = new Application(new ExecButton("Audio Stop", null));
        Clip c = playAudioClip(audio);
        while(a.isVisible()) Thread.yield();//stay open to show
        c.stop();
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
        UN_TAR("Un-tar"),
        BAD_VERSION("Version error"),
        GIT("Git clone");

        private final String text;

        Error(String text) {
            this.text = text;
        }
    }

    public enum Command {
        //TODO create repo table hashes for signature store
        REPO_NFT('n', "get NFT url for user",
                (args) -> { }, new String[]{""}, false),

        //TODO a mini game
        GAME('m', "play mini game", (args) -> {
            Game g = new Game();
            while(g.isVisible()) Thread.yield();
        }, new String[]{""}, false),

        //main functions
        REPO_GIT('g', "clone git signature repository", (args) -> {
            exitCode(execute(GIT + args[0] + " "    //Oops, a space
                    + SignedStream.git, null, null), Error.GIT);
        }, new String[]{ GIT_URL }, false),
        AUDIO('p', "play audio", (args) -> {
            audioCanvas((AudioInputStream) FilePipe.readComponent(FilePipe.getInputStream(new File(args[0]))));//create if possible
        }, new String[] { ARCH }, false),
        OK_VERSION('o', "check version is at least required", (args) -> {
            String[] v = version.split(new Perl(".").get());
            String[] t = args[0].split(new Perl(".").get());
            boolean ok = true;
            for (int i = 0; i < v.length; i++) {
                if(Integer.getInteger(v[i]) < Integer.getInteger(t[i])) {
                    ok = false;
                    break;
                }
            }
            exitCode(ok ? 0 : 1, Error.BAD_VERSION);
        }, new String[] { VERSION_LIKE }, false),
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
            imageCanvas((BufferedImage) FilePipe.readComponent(FilePipe.getInputStream(new File(args[0]))));//create if possible
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
                        //exitCode(Error.NONE);
                    } catch(Exception e) {
                        System.err.println(e.getMessage());
                        exitCode(Error.DEFAULT);//default err
                    }
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
