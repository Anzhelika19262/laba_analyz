public class GrammarSymbolControl {
    static String terminals = "abcdefghijklmnopqrstuvwxyz_.,;+-*(){}[]:=_\\\\& ";
    static String nonTerminals = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public GrammarSymbolControl() {}

    public static boolean isTerminal(String ch) {
        return terminals.contains(ch);
    }

    public static boolean isNonTerminal(String ch) {
        return nonTerminals.contains(ch);
    }
}
