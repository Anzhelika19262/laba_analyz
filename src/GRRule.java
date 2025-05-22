import java.util.ArrayList;

public class GRRule {
    String left;
    ArrayList<String> right;
    ArrayList<String> action;

    public GRRule() {
        this.left = "";
        this.right = new ArrayList<>();
        this.action = new ArrayList<>();
    }

    public ArrayList<String> getRight() {
        return right;
    }

    public void setRight(ArrayList<String> right) {
        this.right = right;
    }

    public ArrayList<String> getAction() {
        return action;
    }

    public void setAction(ArrayList<String> action) {
        this.action = action;
    }

    public String getLeft() {
        return left;
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
