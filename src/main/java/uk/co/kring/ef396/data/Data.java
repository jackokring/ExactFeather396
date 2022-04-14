package uk.co.kring.ef396.data;

public class Data {

    @FunctionalInterface
    public interface Exec {

        void run(String[] args);
    }

    public enum Command {
        ARCHIVE('a', "archive", (args) -> {  }),
        EXTRACT('x', "extract", (args) -> {  }),
        LOAD('l', "load dialog", (args) -> {  }),
        SAVE('s', "save dialog", (args) -> {  }),
        COMPRESS('c', "compress", (args) -> {  }),
        EXPAND('e', "expand", (args) -> {  }),
        VERSION('v', "version", (args) -> {  }),
        HELP('h', "<help with option>", (args) -> {
            if(args == null) {
                System.out.println("Try one of the following options for the first argument");
                for (Command c: Command.values()) {
                    System.out.println("./<data> " + c.option + " " + c.description);
                }
            }
        }),
        USE('u', "use", (args) -> {  }),
        IMAGE('i', "image load", (args) -> {  });

        private char option;
        private String description;
        private Exec action;

        Command(char option, String description, Exec action) {
            this.option = option;
            this.description = description;
            this.action = action;
        }
    }

    public static void main(String[] args) {
        if(args.length >= 1 && args[0].length() == 1) {
            for (Command c: Command.values()) {
                if(args[0].charAt(0) == c.option) {
                    String[] s = new String[args.length - 1];
                    for(int i = 1; i < args.length; i++) {
                        s[i-1] = args[i];
                    }
                    c.action.run(s);
                    return;
                }
            }
        } else {
            Command.HELP.action.run(null);//doesn't need it
        }
    }
}
