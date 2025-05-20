import javax.lang.model.util.AbstractElementVisitor14;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.*;

public class LrGraph {
    LrGrammar grammar;
    ArrayList<ArrayList<LrSituation>> listVecs = new ArrayList<>();
    ArrayList<GraphState> grStates = new ArrayList<>();
    Set<String> setElements = new HashSet<>();

    public LrGraph(LrGrammar grammar) {
        this.grammar = grammar;
    }

    public LrGrammar getGrammar() {
        return grammar;
    }

    public ArrayList<ArrayList<LrSituation>> getListVecs() {
        return listVecs;
    }

    public ArrayList<GraphState> getGrStates() {
        return grStates;
    }

    public Set<String> getSetElements() {
        return setElements;
    }

    public void defMultipleStates() {
        ArrayList<LrSituation> lrVec = new ArrayList<>();
        LrSituation sit = new LrSituation();
        sit.setLeft(grammar.getRule(0).getLeft());
        sit.setRight(grammar.getRule(0).getRight());
        lrVec.add(sit);

        closure(lrVec);
        listVecs.add(lrVec);

        int listVecsSize = listVecs.size();
        for (int i = 0; i < listVecsSize; ++i) {
            Set<String> lrSet = setOfStates(listVecs.get(i));
            GraphState grs = new GraphState(i, 0, "");

            for (String symb: lrSet) {
                grs.setLabelTransition(symb);
                ArrayList<LrSituation> subVec = new ArrayList<>();

                for (LrSituation elem: listVecs.get(i)) {
                    if (Objects.equals(elem.getRight().get(elem.getCurIndex()), symb)) {
                        if (elem.getPointPosition() == elem.getRight().size())
                            continue;
                        else {
                            elem.setPointPosition(elem.getPointPosition() + 1);
                            if (elem.getCurIndex() < (elem.getRight().size() - 1)) {
                                elem.setCurIndex(elem.getCurIndex() + 1);
                            }
                            subVec.add(elem);
                        }
                    }
                }
                if (!subVec.isEmpty()) {
                    closure(subVec);
                    compareAndStore(subVec, grs);
                    grStates.add(grs);
                }
            }
        }
    }

    public Set<String> first(LrSituation item) {
        Set<String> setSymb = new HashSet<>();

        if (item.getCurIndex() == item.getRight().size() - 1) {
            setSymb.add(item.getTail());
        } else {
            int size = item.getRight().get(item.getCurIndex() + 1).length();
            if (GrammarSymbolControl.isTerminal(String.valueOf(item.getRight().get(item.getCurIndex() + 1).charAt(0)))
                    || (size > 1 && item.getRight().get(item.getCurIndex() + 1).charAt(1) == '-')) {
                setSymb.add(item.getRight().get(item.getCurIndex() + 1));
                return setSymb;
            }

            for (int i = 1; i < grammar.size(); ++i) {
                GRRule gr = grammar.getRule(i);
                if (Objects.equals(item.getRight().get(item.getCurIndex() + 1), gr.getLeft())) {
                    int size2 = gr.getRight().get(0).length();

                    if (GrammarSymbolControl.isTerminal(String.valueOf(gr.getRight().get(0).charAt(0)))
                            || (size2 > 1 && gr.getRight().get(0).charAt(1) == '-')) {
                        setSymb.add(gr.getRight().get(0));
                    } else {
                        for (int j = 1; j < grammar.size(); ++j) {
                            GRRule gr2 = grammar.getRule(j);
                            if (Objects.equals(gr.getRight().get(0), gr2.getLeft())) {
                                size2 = gr2.getRight().get(0).length();
                                if (GrammarSymbolControl.isTerminal(String.valueOf(gr2.getRight().get(0).charAt(0)))
                                        || (size2 > 1 && gr2.getRight().get(0).charAt(1) == '-')) {
                                    setSymb.add(gr2.getRight().get(0));
                                }
                            }
                        }
                    }
                }
            }

            if (setSymb.isEmpty())
                setSymb.add(item.tail);
        }
        return setSymb;
    }

    public void closure(ArrayList<LrSituation> lrVec) {
        for (int i = 0; i < lrVec.size(); ++i) {
            LrSituation lrSit = lrVec.get(i);
            Set<String> symbPrev = first(lrSit);

            for (String symbItem: symbPrev) {
                for (int j = 0; j < grammar.size(); ++j) {
                    GRRule gr = grammar.getRule(j);
                    if (lrSit.getPointPosition() <= lrSit.getCurIndex()) {
                        if (Objects.equals(lrSit.getRight().get(lrSit.getCurIndex()), gr.getLeft())) {
                            LrSituation sit = new LrSituation(gr.getLeft(), gr.getRight(), symbItem, j);

                            boolean isNewSituation = false;

                            for (LrSituation situation : lrVec) {
                                if (situation.equals(sit)) {
                                    isNewSituation = true;
                                    break;
                                }
                            }
                            if (!isNewSituation)
                                lrVec.add(sit);
                        }
                    }
                }
            }
        }
    }

    public Set<String> setOfStates(ArrayList<LrSituation> lrVec) {
        Set<String> setStr = new HashSet<>();
        for (LrSituation elem: lrVec) {
            if (elem.getRight().get(elem.getCurIndex()).length() > 0)
            {
                setStr.add(elem.getRight().get(elem.getCurIndex()));
                setElements.add(elem.getRight().get(elem.getCurIndex()));
            }
        }
        setElements.add("$");
        return setStr;
    }

    public void compareAndStore(ArrayList<LrSituation> vec, GraphState grs) {
        for (int i = 1; i < listVecs.size(); ++i) {
            boolean isExcessElem = true;
            for (int j = 0; j < vec.size(); ++j) {
                if (vec.get(j) != listVecs.get(i).get(j) || vec.size() != listVecs.get(i).size()) {
                    isExcessElem = false;
                    break;
                }
            }
            if (isExcessElem) {
                grs.setChildPosition(i);
                return;
            }
        }

        grs.setChildPosition(listVecs.size());
        listVecs.add(vec);
    }

    public void printGraphStates(BufferedWriter writter) throws IOException {
        int cntState = 0;
        for (ArrayList<LrSituation> state: listVecs) {
            System.out.println("\n====  State: " + cntState + " ====");
            writter.write("\n====  State: " + cntState + " ====" + "\n");

            for (LrSituation elem: state) {
                StringBuilder tmp = new StringBuilder();
                for (int i = 0; i < elem.getRight().size(); ++i) {
                    if (i == elem.getPointPosition()) {
                        tmp.append(" . ");
                    }
                    tmp.append(elem.getRight().get(i));
                    if (i != (elem.getRight().size() - 1)) {
                        tmp.append(" ");
                    }
                }
                if (elem.getPointPosition() == elem.getRight().size()) {
                    tmp.append(".");
                }
                System.out.println(elem.getLeft() + " -> " + tmp + "|" + elem.getTail() + "\n");
                writter.write(elem.getLeft() + " -> " + tmp + "|" + elem.getTail() + "\n");
            }
        }
    }

}
