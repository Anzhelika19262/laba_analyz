public class GraphState {
    protected int	parentPosition;
    protected int	childPosition;
    protected String labelTransition;

    public GraphState() {
    }

    public GraphState(int parentPosition, int childPosition, String labelTransition) {
        this.parentPosition = parentPosition;
        this.childPosition = childPosition;
        this.labelTransition = labelTransition;
    }
}
