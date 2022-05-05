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

    public static boolean verbose = false;

    @FunctionalInterface
    public interface Exec {
        //primary command execution interface
        void run(String[] args) throws IOException;
    }

    //========================== HELP TEXT LITERALS ==================

    public static final String FILE = "SAVE AS NAME";
    public static final String ARCH = "ARCHIVE NAME";
    public static final String COMMAND = "COMMAND STRING";
    public static final String DIRS = "DIRECTORY NAME";
    public static final String VERSION_LIKE = "VERSION CODE";
    public static final String GIT_URL = "GIT REPOSITORY";
    public static final String OPTION = "OPTION";
    public static final String REPEATS = "...";

    //======================== EXECUTE LITERALS ========================

    public static final String TAR = "tar cf - ";
    public static final String UN_TAR = "tar xvf - ";
    public static final String GIT = "git clone ";

    //======================= EXITS =========================

    public static void exitCode(Error code) {
        if(code != Error.NONE) {
            System.err.print("[" + code.ordinal() + "] ");
            System.err.println(code.text + " error in " + name + " tools causing premature exit.");
        }
        System.exit(code.ordinal());
    }

    public static void exitCode(InputStream exit, Error code) {
        if(exit == null) {//error
            exitCode(code);
        } else {
            try {
                exit.close();
            } catch(IOException e) {
                exitCode(code, e);//print exception
            }
            exitCode(Error.NONE);
        }
    }

    public static void exitCode(Error code, Exception e) {
        while(e != null) {
            System.err.println(e.getMessage());
            if(verbose) e.printStackTrace();
            if(e.getCause() != null && e.getCause() instanceof Exception f) e = f;
        }
        exitCode(code);
    }

    //====================== TAR / UN-TAR ===============================

    public static InputStream tar(String[] dirs, OutputStream arch) throws IOException {
        return execute(TAR + Arrays.stream(dirs).reduce((s, t) -> s + " " + t),
                System.in, arch,true);
    }

    public static InputStream unTar(InputStream arch) throws IOException {
        return execute(UN_TAR, arch, System.out, true);
    }

    //========================== PROCESS EXECUTE ================================

    public static InputStream execute(String command, InputStream in, OutputStream out,
                              boolean closeOut) throws IOException {
        ProcessBuilder builder = new ProcessBuilder(command);
        Process p = builder. /* directory(new File("~")). */ start();
        InputStream is = p.getInputStream();
        InputStream es = p.getErrorStream();
        OutputStream os = p.getOutputStream();
        if(in == null) in = System.in;
        if(out == null) out = System.out;
        FilePipe.Task j1 = FilePipe.cloneStream(in, os, true);
        FilePipe.Task j2 = FilePipe.cloneStream(is, out, closeOut);
        FilePipe.Task j3 = FilePipe.cloneStream(es, System.err, false);//leave errors open
        j2.rejoin().close();//no output from process left so done producing
        //returned input stream should be empty
        es.close();//cause j3 join ok.
        j3.rejoin().close();//all errors placed for view
        //returned stream should be empty
        os.close();//cause j1 join
        InputStream ret = j1.rejoin();//might even throw as way of exit
        boolean noError = p.exitValue() == 0;
        if(noError) {
            return ret;
        }
        ret.close();//close stream to cascade use of data fault
        return null;//null as error
    }

    //============================= IMAGE ================================

    public static void imageCanvas(BufferedImage image) {
        Application a = new Application(new ImageCanvas(image, "Image"));
        a.whileOpenHalt();
    }

    //============================== AUDIO ==============================

    public static Clip playAudioClip(AudioInputStream audio) throws IOException {
        Clip clip = null;
        try {
            clip = AudioSystem.getClip();
            // Open audio clip and load samples from the audio input stream.
            clip.open(audio);
            clip.start();
        } catch(Exception e) {
            io(e);
        }
        return clip;
    }

    public static void audioCanvas(AudioInputStream audio) throws IOException {
        Application a = new Application(new ExecButton("Audio Stop", null));
        Clip c = playAudioClip(audio);
        a.whileOpenHalt();
        c.stop();
        audio.close();
    }

    //============================== DIALOGS ===============================

    public static TypedStream.Input loadDialog() throws IOException {
        FileDialog fd = new FileDialog((Frame) null, "Load", FileDialog.LOAD);
        fd.setFilenameFilter((dir, name) -> FilePipe.filePipeForName(name) != FilePipe.NULL);
        fd.setDirectory("~");//home
        fd.setVisible(true);
        File file = new File(fd.getFile());
        if(fileAndNotDirectory(file)) return FilePipe.readStream(FilePipe.getInputStream(file));
        io(new IllegalArgumentException("Bad filename"));
        return null;
    }

    public static boolean replaceDialog() {
        Dialog ok = new Dialog((Frame) null, "Replace");
        ok.setModalityType(Dialog.DEFAULT_MODALITY_TYPE);
        ok.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                ok.setVisible(false);
            }
        });
        Label text = new Label("File exists. Replace file? Close for No or Cancel. OK replaces file.");
        ExecButton yes = new ExecButton("OK", null);//default parent hide action
        ok.add(text, BorderLayout.CENTER);
        ok.add(yes, BorderLayout.PAGE_END);
        ok.setVisible(true);//modal
        return yes.getClicked();
    }

    public static TypedStream.Output saveDialog() throws IOException {
        FileDialog fd = new FileDialog((Frame) null, "Save", FileDialog.SAVE);
        fd.setFilenameFilter((dir, name) -> FilePipe.filePipeForName(name) != FilePipe.NULL);
        fd.setDirectory("~");//home
        fd.setVisible(true);
        String file = fd.getFile();
        File f = new File(file);
        if(fileAndNotDirectory(f)) {
            if(replaceDialog()) return FilePipe.writeStream(FilePipe.getOutputStream(f));
        }
        io(new IllegalArgumentException("Can't save file"));
        return null;
    }

    //=========================== ERROR CODES ==========================

    public enum Error {
        NONE("No"),
        DOCUMENTATION("Documentation"),
        DEFAULT("Unimplemented catch"),
        URL("Unexpected URI format for name"),
        USED("Used command made an error"),
        TAR("Tar archival"),
        UN_TAR("Un-tar archival"),
        BAD_VERSION("Version error"),
        GIT("Git clone"),
        NOT_MULTI("Tar archiving is not applicable"),
        COMPONENT("Component not available"),
        IO("IO stream exception");

        private final String text;

        Error(String text) {
            this.text = text;
        }
    }

    //========================== COMMAND SPECIFICATIONS ============================

    public enum Command {
        //'bfjknqrwyz' <== left??
        //main functions
        DEBUG('d', "debug errors", (args) -> {
            main(shift(args));
        }, new String[] { OPTION }, true),
        GAME('m', "play mini game", (args) -> {
            Game g = new Game();
            g.whileOpenHalt();
        }, new String[]{ }, false),
        REPO_GIT('g', "clone git signature repository", (args) -> {
            exitCode(execute(GIT + args[0] + " "    //Oops, a space
                    + SignedStream.git, null, null, false), Error.GIT);
        }, new String[]{ GIT_URL }, false),
        AUDIO('p', "play audio", (args) -> {
            audioCanvas((AudioInputStream)
                    FilePipe.readComponent(FilePipe.getInputStream(new File(args[0])),
                            false));//create if possible
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
            exitCode(ok ? System.in : null, Error.BAD_VERSION);//any stream for error no!
        }, new String[] { VERSION_LIKE }, false),
        ARCHIVE('a', "archive", (args) -> {
            String[] dirs = shift(args);
            TypedStream.Output os = FilePipe.getOutputStream(new File(args[0]));
            if(os.getFilePipe().isTarable()) {
                exitCode(tar(dirs, FilePipe.writeStream(os)), Error.TAR);
            } else {
                exitCode(Error.NOT_MULTI);
            }
        }, new String[]{ FILE, DIRS }, true),
        EXTRACT('x', "extract", (args) -> {
            TypedStream.Input is = FilePipe.getInputStream(new File(args[0]));
            if(is.getFilePipe().isTarable()) {
                exitCode(unTar(FilePipe.readStream(is)), Error.UN_TAR);
            } else {
                exitCode(Error.NOT_MULTI);
            }
        }, new String[]{ ARCH }, false),
        LOAD('l', "common load dialog", (args) -> {
            FilePipe.cloneStream(loadDialog(), System.out, false)
                    .rejoin().close();//close in
        }, new String[]{ }, false),
        SAVE('s', "common save dialog", (args) -> {
            FilePipe.cloneStream(System.in, saveDialog(), true)
                    .rejoin().close();
            //close input to back propagate inability to make sense of data by processing
        }, new String[]{ }, false),
        COMPRESS('c', "compress file", (args) -> {
            FilePipe.cloneStream(System.in,
                    FilePipe.writeStream(FilePipe.getOutputStream(new File(args[0]))),
                    true)
                    .rejoin().close();//finished
        }, new String[]{ FILE }, false),
        EXPAND('e', "expand file", (args) -> {
            FilePipe.cloneStream(FilePipe.readStream(FilePipe.getInputStream(new File(args[0]))),
                    System.out, false)
                    .rejoin().close();//finished
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
                    FilePipe.writeStream(FilePipe.getOutputStream(new File(args[1]))),
                    true), Error.USED);
        }, new String[]{ ARCH, FILE, COMMAND }, false),
        TRANSCODE('t', "transcode from file to file", (args) -> {
            FilePipe.cloneStream(FilePipe.readStream(FilePipe.getInputStream(new File(args[0]))),
                    FilePipe.writeStream(FilePipe.getOutputStream(new File(args[1]))),
                    true)
                    .rejoin().close();//close input
        }, new String[]{ ARCH, FILE }, false),
        IMAGE('i', "image load and view", (args) -> {
            imageCanvas((BufferedImage)
                    FilePipe.readComponent(FilePipe.getInputStream(new File(args[0])),
                            true));//create if possible
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
            System.out.println("Note that git, tar and netpbm are dependencies.");
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
                if(c.description == null) io(new IllegalArgumentException("Needs documentation"));
                System.out.println("\t" + c.description);
            }
        }
    }

    //============================== UTILS ======================================

    public static String[] shift(String[] args) {
        String[] s = new String[args.length - 1];
        System.arraycopy(args, 1, s, 0, args.length - 1);
        return s;
    }

    public static boolean fileAndNotDirectory(File file) {
        return file.exists() && !file.isDirectory();
    }

    public static void io(Exception e) throws IOException {
        throw new IOException(e);
    }

    //============================== ENTRY OF PROGRAM =============================

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
                        exitCode(Error.NONE);
                    } catch(ClassCastException cast) {
                        exitCode(Error.COMPONENT, cast);
                    } catch(IOException io) {
                        exitCode(Error.IO, io);
                    } catch(Exception e) {
                        exitCode(Error.DEFAULT, e);//default err
                    }
                }
            }
        } else {
            try {
                Command.HELP.action.run(null);//doesn't need it
            } catch(Exception e) {
                exitCode(Error.DOCUMENTATION, e);//documentation
            }
        }
    }
}
