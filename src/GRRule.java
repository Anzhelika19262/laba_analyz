import java.util.ArrayList;

public class GRRule {
    String left = "";
    ArrayList<String> right = new ArrayList<>();
    ArrayList<String> action = new ArrayList<>();

    public GRRule() {
    }

    public void setLeft(String left) {
        this.left = left;
    }

    public void addAction(String action) {
        this.action.add(action);
    }

    public void addRight(String right) {
        this.right.add(right);
    }

    @Override
    public String toString(){
        return this.left + " -> " + String.join(" ", right);
    }
}
