package uk.co.kring.ef396.data;

public class Data {

    @FunctionalInterface
    public interface Exec {

        String[] run(String[] args);
    }

    public static class Command {

        public Command(char option, String description, Exec action) {

        }
    }

    public static final Command[] actions = {
        new Command('a', "archive", (args) -> { return args; }),
        new Command('x', "extract", (args) -> { return args; }),
        new Command('l', "load dialog", (args) -> { return args; }),
        new Command('s', "save dialog", (args) -> { return args; }),
        new Command('c', "compress", (args) -> { return args; }),
        new Command('e', "expand", (args) -> { return args; }),
        new Command('v', "version", (args) -> { return args; }),
        new Command('u', "use", (args) -> { return args; }),
        new Command('i', "image load", (args) -> { return args; })
    };

    public static void main(String[] args) {

    }
}
