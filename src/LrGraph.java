import javax.lang.model.util.AbstractElementVisitor14;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class LrGraph {
    LrGrammar grammar;
    ArrayList<ArrayList<LrSituation>> listVecs = new ArrayList<>();
    ArrayList<GraphState> grStates= new ArrayList<>();
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
        sit.left = grammar.getRule(0).left;
        sit.right = grammar.getRule(0).right;
        lrVec.add(sit);

        closure(lrVec);
        listVecs.add(lrVec);

        int childPosition = 1;

        for (int i = 0; i < listVecs.size(); ++i) {
            Set<String> lrSet = new HashSet<>();
            setOfStates(listVecs.get(i), lrSet);
            GraphState grs = new GraphState(i, 0, "");

            for (String symb: lrSet) {
                grs.labelTransition = symb;
                ArrayList<LrSituation> subVec = new ArrayList<>();
                for (LrSituation elem: listVecs.get(i)) {
                    if (Objects.equals(elem.right.get(elem.curIndex), symb)) {
                        if (elem.pointPosition == elem.right.size())	continue;
                        else {
                            elem.pointPosition++;
                            if (elem.curIndex < elem.right.size() - 1) {
                                elem.curIndex++;
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

    public void first(LrSituation item, Set<String> setSymb) {
        if (item.curIndex == item.right.size() - 1) {
            setSymb.add(item.tail);
        } else {
            int size = item.right.get(item.curIndex + 1).length();
            if (GrammarSymbolControl.isTerminal(String.valueOf(item.right.get(item.curIndex + 1).charAt(0))) || (size > 1 && item.right.get(item.curIndex + 1).charAt(1) == '-')) {
                setSymb.add(item.right.get(item.curIndex + 1));
                return;
            }

            for (int i = 1; i < grammar.size(); ++i) {
                GRRule gr = grammar.getRule(i);
                if (Objects.equals(item.right.get(item.curIndex + 1), gr.left)) {
                    int size2 = gr.right.get(0).length();

                    if (GrammarSymbolControl.isTerminal(String.valueOf(gr.right.get(0).charAt(0))) || (size2 > 1 && gr.right.get(0).charAt(1) == '-')) {
                        setSymb.add(gr.right.get(0));
                    } else {
                        for (int j = 1; j < grammar.size(); ++j) {
                            GRRule gr2 = grammar.getRule(j);
                            if (Objects.equals(gr.right.get(0), gr2.left)) {
                                size2 = gr2.right.get(0).length();
                                if (GrammarSymbolControl.isTerminal(String.valueOf(gr2.right.get(0).charAt(0))) || (size2 > 1 && gr2.right.get(0).charAt(1) == '-')) {
                                    setSymb.add(gr2.right.get(0));
                                }
                            }
                        }
                    }
                }
            }

            if (setSymb.isEmpty())
                setSymb.add(item.tail);
        }
    }

    public void closure(ArrayList<LrSituation> lrVec) {
        for (int i = 0; i < lrVec.size(); ++i) {
            LrSituation lrSit = lrVec.get(i);
            Set<String> symbPrev = new HashSet<>();
            first(lrSit, symbPrev);

            for (String symbItem: symbPrev) {
                for (int j = 0 /*1*/; j < grammar.size(); ++j) {
                    GRRule gr = grammar.getRule(j);
                    if (lrSit.pointPosition <= lrSit.curIndex) {
                        if (Objects.equals(lrSit.right.get(lrSit.curIndex), gr.left)) {
                            LrSituation sit = new LrSituation();
                            sit.left = gr.left;
                            sit.right = gr.right;
                            sit.tail = symbItem;
                            sit.rule = j;

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

    public void setOfStates(ArrayList<LrSituation> lrVec, Set<String> setStr) {
        for (LrSituation elem: lrVec) {
            if (elem.right.get(elem.curIndex).length() > 0)
            {
                setStr.add(elem.right.get(elem.curIndex));
                setElements.add(elem.right.get(elem.curIndex));
            }
        }
        setElements.add("$");
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
                grs.childPosition = i;
                return;
            }
        }

        grs.childPosition = listVecs.size();
        listVecs.add(vec);
    }

    public void printGraphStates(BufferedWriter writter) throws IOException {
        int cntState = 0;
        for (ArrayList<LrSituation> state: listVecs) {
            System.out.println("\n====  State: " + cntState + " ====");
            writter.write("\n====  State: " + cntState + " ====" + "\n");

            for (LrSituation elem: state) {
                StringBuilder tmp = new StringBuilder();
                for (int i = 0; i < elem.right.size(); ++i) {
                    if (i == elem.pointPosition) {
                        tmp.append(" . ");
                    }
                    tmp.append(elem.right.get(i));
                    if (i != elem.right.size() - 1) {
                        tmp.append(" ");
                    }
                }
                if (elem.pointPosition == elem.right.size()) {
                    tmp.append(".");
                }
                System.out.println(elem.left + " -> " + tmp + "|" + elem.tail + "\n");
                writter.write(elem.left + " -> " + tmp + "|" + elem.tail + "\n");
            }
        }
    }

}
