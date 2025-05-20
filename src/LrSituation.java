import java.util.ArrayList;

public class LrSituation {
    protected String left;
    protected String tail;
    protected int curIndex;
    protected int pointPosition;
    protected int rule;
    protected ArrayList<String> right;

    public LrSituation(){
        tail = "$";
        rule = 0;
        curIndex = 0;
        pointPosition = 0;
    }

    public LrSituation(String left, ArrayList<String> right, String tail, int rule) {
        this.left = left;
        this.tail = tail;
        this.curIndex = 0;
        this.pointPosition = 0;
        this.rule = rule;
        this.right = right;
    }

    public LrSituation(LrSituation lrs) {
        this.left = lrs.left;
        this.tail = lrs.tail;
        this.curIndex = lrs.curIndex;
        this.pointPosition = lrs.pointPosition;
        this.rule = lrs.rule;
        this.right = lrs.right;
    }

    public String getLeft() {
        return left;
    }

    public void setLeft(String left) {
        this.left = left;
    }

    public String getTail() {
        return tail;
    }

    public void setTail(String tail) {
        this.tail = tail;
    }

    public int getCurIndex() {
        return curIndex;
    }

    public void setCurIndex(int curIndex) {
        this.curIndex = curIndex;
    }

    public int getPointPosition() {
        return pointPosition;
    }

    public void setPointPosition(int pointPosition) {
        this.pointPosition = pointPosition;
    }

    public int getRule() {
        return rule;
    }

    public void setRule(int rule) {
        this.rule = rule;
    }

    public ArrayList<String> getRight() {
        return right;
    }

    public void setRight(ArrayList<String> right) {
        this.right = right;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LrSituation situation)) return false;

        if (curIndex != situation.curIndex) return false;
        if (pointPosition != situation.pointPosition) return false;
        if (rule != situation.rule) return false;
        if (!left.equals(situation.left)) return false;
        if (!tail.equals(situation.tail)) return false;
        return right.equals(situation.right);
    }
}
