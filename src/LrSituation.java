import java.util.ArrayList;
import java.util.Objects;

public class LrSituation {
    public String left;
    public String tail;
    public int curIndex;
    public int pointPosition;
    public int rule;
    public ArrayList<String> right;

    public LrSituation(){
        tail = "$";
        rule = 0;
        curIndex = 0;
        pointPosition = 0;
    }

    public LrSituation(LrSituation lrs) {
        this.left = lrs.left;
        this.tail = lrs.tail;
        this.curIndex = lrs.curIndex;
        this.pointPosition = lrs.pointPosition;
        this.rule = lrs.rule;
        this.right = lrs.right;
    }

    public boolean equals(LrSituation that) {
        if (this == that) return true;
        if (that == null || getClass() != that.getClass()) return false;

        if (curIndex != that.curIndex) return false;
        if (pointPosition != that.pointPosition) return false;
        if (rule != that.rule) return false;
        if (!Objects.equals(left, that.left)) return false;
        if (!Objects.equals(tail, that.tail)) return false;
        return Objects.equals(right, that.right);
    }
}
