import java.lang.reflect.Array;
import java.util.ArrayList;

public class SemanticType {
    ArrayList<DefVar> var = new ArrayList<>();
    int	level;

    public SemanticType() {
        this.level = 0;
    }
    void clear() {
        level = 0;
        var.clear();
    }
}
