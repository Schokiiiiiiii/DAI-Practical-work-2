package ch.heigvd.dai.applicationInterface;

public abstract class AnsiColors {

    // Reference used: https://ss64.com/nt/syntax-ansi.html

    // Unicode escape
    protected final String PREFIX = "\u001B[";

    protected final String RESET = PREFIX + "0m";


    // Colors
    protected final String BLACK_F = PREFIX + "30m";
    protected final String BRIGHT_RED_F = PREFIX + "91m";
    protected final String BRIGHT_RED_B = PREFIX + "101m";
    protected final String BRIGHT_MAGENTA_F = PREFIX + "95m";
    protected final String DARK_YELLOW_F = PREFIX + "33m";
    protected final String BRIGHT_CYAN_F = PREFIX + "96m";
    protected final String BRIGHT_GREEN_F = PREFIX + "92m";
    protected final String DARK_GREEN_B = PREFIX + "42m";

    // Text decorations
    protected final String BOLD = PREFIX + "1m";

}
