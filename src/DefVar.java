public class DefVar {
    SecName essence;
    String varName;
    String typeName;

    public DefVar() {
        this.essence = SecName.NO_NAME;
    }

    public SecName getEssence() {
        return essence;
    }

    public void setEssence(SecName essence) {
        this.essence = essence;
    }

    public String getVarName() {
        return varName;
    }

    public void setVarName(String varName) {
        this.varName = varName;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}
