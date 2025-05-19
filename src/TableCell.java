public class TableCell {
    public char stateSymb;
    public int numState;
    public String lableElem;
    public String action;

    public TableCell() {
    }
    public TableCell(char stateSymb, int numState) {
        this.stateSymb = stateSymb;
        this.numState = numState;
    }

    public TableCell(char stateSymb, int numState, String lableElem) {
        this.stateSymb = stateSymb;
        this.numState = numState;
        this.lableElem = lableElem;
    }

    public char getStateSymb() {
        return stateSymb;
    }

    public void setStateSymb(char stateSymb) {
        this.stateSymb = stateSymb;
    }

    public int getNumState() {
        return numState;
    }

    public void setNumState(int numState) {
        this.numState = numState;
    }

    public String getLableElem() {
        return lableElem;
    }

    public void setLableElem(String lableElem) {
        this.lableElem = lableElem;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
