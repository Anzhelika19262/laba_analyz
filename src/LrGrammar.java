import java.util.ArrayList;

public class LrGrammar {
    ArrayList<GRRule> grammar = new ArrayList<>();
    public LrGrammar() {}

    public void print() {
        for (GRRule rule: grammar) {System.out.println(rule.toString());}
    }

    public void setRule(GRRule rule) {
        grammar.add(rule);
    }

    public GRRule getRule(int position) {
        return (position <= (grammar.size() - 1)) ? grammar.get(position) : grammar.get(grammar.size() - 1);
    }

    public Integer size() {
        return grammar.size();
    }
}
