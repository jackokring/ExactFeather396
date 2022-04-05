package uk.co.kring.ef396.utilities;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Perl {

    String literal;
    Pattern pattern;

    public Perl() {
        literal = "";
    }

    public Perl(String literal) {
        this();
        this.literal = Pattern.quote(literal);
    }

    public Perl append(String literal) {
        this.literal += Pattern.quote(literal);
        return this;
    }

    public Perl match(String named) {
        this.literal += "\\k<" + named + ">";// back reference
        return this;
    }

    public Perl match(String named, String match) {
        this.literal += "(?<" + named + ">" + Pattern.quote(match) + ")";
        return this;
    }

    public Perl match(String named, Perl match) {
        this.literal += "(?<" + named + ">" + match.literal + ")";
        return this;
    }

    public Perl find(String expression) {
        this.literal += "(" + expression + ")";// as a matching expression
        return this;
    }

    public Perl option() {
        this.literal += "?";// 0 or 1 times
        return this;
    }

    public Perl optionOrRepeated() {
        this.literal += "*";// 0 to n times
        return this;
    }

    public Perl repeated() {
        this.literal += "+";// 1 to n times
        return this;
    }

    public Perl once() {
        this.literal += "{1}";// 1 time
        return this;
    }

    public Perl optionRepeat(boolean optional, boolean repeats) {
        if(repeats) {
            if(optional) {
                return optionOrRepeated();
            } else {
                return repeated();
            }
        } else {
            if(optional) {
                return option();
            } else {
                return once();
            }
        }
    }

    public Perl or(Perl or) {
        this.literal = "(" + this.literal + ")|(" + or.literal + ")";
        return this;
    }

    public Perl anyOrNot(String these, boolean not) {
        String inv = "^";
        if(not) inv = "";
        this.literal = "[" + inv + these + "]";
        return this;
    }

    public Perl anyOrNot(char from, char to, boolean not) {
        String inv = "^";
        if(not) inv = "";
        this.literal = "[" + inv + from + "-" + to + "]";
        return this;
    }

    public Perl extendPattern() {
        pattern = null;
        return this;
    }

    public Perl resetPattern() {
        literal = "";
        return extendPattern();
    }

    public Matcher matcherFor(String match) {
        if(pattern == null) pattern = Pattern.compile(literal);
        return pattern.matcher(match);//get matcher
    }

    @Override
    public String toString() {
        return literal;
    }
}
