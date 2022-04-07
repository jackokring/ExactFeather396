package uk.co.kring.ef396.utilities;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Perl {

    private String literal;
    private String match;
    private Matcher matcher;
    private Pattern pattern;

    private String escapeChar(char c) {
        return Pattern.quote(String.valueOf(c));
    }

    public Perl() {
        literal = "";
    }

    public Perl(String literal) {
        this();
        append(literal);
    }

    public Perl append(String literal) {
        this.literal += Pattern.quote(literal);
        return this;
    }

    public Perl match(String named) {
        this.literal += "\\k<" + named + ">";// back reference to find again
        return this;
    }

    public Perl match(String named, Perl match) {
        this.literal += "(?<" + named + ">" + match.literal + ")";// match and name
        return this;
    }

    public Perl raw(String expression) {
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

    public Perl anyOrNot(String these, boolean notInverted) {
        String inv = "^";
        if(notInverted) inv = "";
        this.literal = "[" + inv + Pattern.quote(these) + "]";// - ??
        return this;
    }

    public Perl anyOrNot(char from, char to, boolean notInverted) {
        String inv = "^";
        if(notInverted) inv = "";
        this.literal = "[" + inv + escapeChar(from) + "-" + escapeChar(to) + "]";
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

    // ================================ MATCHER INTERFACE ==============================

    public Perl reset(String match) {
        if(pattern == null) {
            pattern = Pattern.compile(literal);
            matcher = null;//clear match
        }
        if(matcher == null) {
            matcher = pattern.matcher(match);
            this.match = match;//cache
        }
        if(this.match != match) {
            matcher.reset(match);
            this.match = match;//cache
        }
        return this;//get matcher
    }

    public Perl appendMatch(StringBuffer sb, String named) {
        if(matcher == null) return this;
        matcher.appendReplacement(sb, "${" + named + "}");
        return this;
    }

    public Perl appendQuote(StringBuffer sb, String literal) {
        if(matcher == null) return this;
        matcher.appendReplacement(sb, Matcher.quoteReplacement(literal));
        return this;
    }

    public Perl appendTail(StringBuffer sb) {
        if(matcher == null) return this;
        matcher.appendTail(sb);
        return this;
    }

    public Boolean find() {
        if(matcher == null) return false;
        return matcher.find();
    }

    public Perl reset() {
        if(matcher == null) return this;
        matcher.reset();//reset current
        return this;
    }

    public String replaceAll(String literal) {
        if(matcher == null) return "";
        return matcher.replaceAll(Matcher.quoteReplacement(literal));
    }

    public String get() {
        return literal;
    }

    public Optional<Matcher> getBareMatcher() {
        return Optional.ofNullable(matcher);
    }
}
