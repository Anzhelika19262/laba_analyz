public class GraphState {
    protected Integer parentPosition;
    protected Integer childPosition;
    protected String labelTransition;

    public GraphState() {
    }

    public GraphState(int parentPosition, int childPosition, String labelTransition) {
        this.parentPosition = parentPosition;
        this.childPosition = childPosition;
        this.labelTransition = labelTransition;
    }

    public Integer getParentPosition() {
        return parentPosition;
    }

    public void setParentPosition(Integer parentPosition) {
        this.parentPosition = parentPosition;
    }

    public Integer getChildPosition() {
        return childPosition;
    }

    public void setChildPosition(Integer childPosition) {
        this.childPosition = childPosition;
    }

    public String getLabelTransition() {
        return labelTransition;
    }

    public void setLabelTransition(String labelTransition) {
        this.labelTransition = labelTransition;
    }
}
