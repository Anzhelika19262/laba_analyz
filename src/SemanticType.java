import java.lang.reflect.Array;
import java.util.ArrayList;

public class SemanticType {
    ArrayList<DefVar> var = new ArrayList<>();
    int	level;

    public SemanticType() {
        this.level = 0;
    }

    public ArrayList<DefVar> getVar() {
        return var;
    }

    public void setVar(ArrayList<DefVar> var) {
        this.var = var;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void clear() {
        level = 0;
        var.clear();
    }
}
