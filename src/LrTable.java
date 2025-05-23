import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

public class LrTable {
    protected LrGraph graph;
    protected LrGrammar gram;
    protected ArrayList<ArrayList<TableCell>> table = new ArrayList<>();

    public LrTable(LrGraph graph, LrGrammar gram) {
        this.graph = graph;
        this.gram = gram;
    }

    public void generateParseTable() {
        boolean isError = false;

        for (ArrayList<LrSituation> state: graph.getListVecs()) {
            ArrayList<TableCell> vec = new ArrayList<>();
            for (String elem: graph.getSetElements()) {
                TableCell t = new TableCell(' ', -1);
                vec.add(t);
            }
            table.add(vec);
        }

        for (int i = 0; i < graph.getListVecs().size(); ++i) {
            int j = 0;
            TableCell cell = getElement(i, j);

            for (String elem: graph.getSetElements()) {
                for (LrSituation situation: graph.getListVecs().get(i)){
                    if (situation.getPointPosition() == situation.getRight().size() && Objects.equals(situation.getTail(), elem)) {
                        boolean isHalt = (situation.getRight() == gram.grammar.get(0).getRight());
                        if (cell.getNumState() != -1 && (cell.getStateSymb() == 'R' || cell.getStateSymb() == 'H')) {
                            isError = true;
                            System.out.println("\n 1 Ошибка в грамматике!  Состояние: S" +
                                    i + ", элемент: " + cell.getLableElem() +
                                    ", конфликт: " + cell.getStateSymb() + cell.getNumState() +
                                    "/" + (isHalt ? 'H' : 'R') + situation.rule + "\n");
                        }
                        getElement(i, j).setStateSymb((isHalt ? 'H' : 'R'));
                        getElement(i, j).setNumState(situation.rule);
                        getElement(i, j).setLableElem(elem);
                    }

                    if (situation.getPointPosition() < situation.getRight().size()) {
                        int edge = 0;
                        for (GRRule grRule: gram.grammar) {
                            if (grRule.getLeft().equals(situation.getLeft()) && grRule.getRight().equals(situation.getRight()) &&
                            elem.equals(grRule.getRight().get(situation.getCurIndex()))) {
                                for (GraphState graphState: graph.getGrStates()) {
                                    if (graphState.parentPosition == i && elem.equals(graphState.labelTransition)) {
                                        if (cell.getNumState() != -1 && cell.stateSymb == 'S' && cell.getNumState() != graphState.childPosition) {
                                            System.out.println("\n 2 Ошибка в грамматике!  Состояние: S" + i +
                                                    ", элемент: " + cell.getLableElem() + ", конфликт: S" +
                                                    cell.getNumState() + "/S" + graphState.childPosition + "\n");
                                            isError = true;

                                        }
                                        getElement(i, j).setStateSymb('S');
                                        getElement(i, j).setNumState(graphState.getChildPosition());
                                        getElement(i, j).setLableElem(elem);
                                        getElement(i, j).setAction(gram.grammar.get(situation.getRule()).getAction().get(situation.getCurIndex()));
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
                j++;
            }
        }
        if (isError)
            System.exit(1);
    }

    public ArrayList<ArrayList<TableCell>> getTable() {
        return table;
    }

    public ArrayList<TableCell> getLine(Integer index){
        return table.get(index);
    }

    public TableCell getElement(Integer numOfLine, Integer numOfColumn) {
        return table.get(numOfLine).get(numOfColumn);
    }

    public void printParseTable(BufferedWriter writter) throws IOException {
        final int indentWidth = 5;
        final int tableElemWidth = 8;//4;

        int[] tbWidth = new int[graph.setElements.size()];
        Arrays.fill(tbWidth, tableElemWidth);

        int lineSize = 0;
        int cnt = 0;

        for (String state: graph.getSetElements()){
            if (state.length() > tableElemWidth) {
                tbWidth[cnt] = state.length();
                lineSize += tbWidth[cnt] + 1;
            } else {
                lineSize += tableElemWidth + 1;
            }
            cnt++;
        }

        String line = "-".repeat(lineSize + indentWidth);
        String output = String.format("%" + indentWidth + "s", "|");

        System.out.println("\n" + line);
        System.out.printf(output);
        writter.write("\n" + line);
        writter.write(output);

        cnt = 0;

        for (String state: graph.getSetElements()) {
            String formatted = String.format("%" + tbWidth[cnt] + "s|", state);
            System.out.println(formatted);
            writter.write(formatted);
            cnt++;
        }
        System.out.println(line);
        writter.write("\n" + line + "\n");

        int mState = 0;

        for (ArrayList<TableCell> tableCells: this.table) {
            String tmp = "S" + String.valueOf(mState) + "|";
            String formatted = String.format("%" + indentWidth + "s", tmp);
            System.out.println(formatted);
            writter.write(formatted);
            cnt = 0;

            for (TableCell state: tableCells) {
                if (state.numState != -1) {
                    String tmp2 = state.stateSymb + String.valueOf(state.numState) + " " + state.action;
                    String formatted2 = String.format("%" + tbWidth[cnt] + "s|", tmp2);
                    System.out.println(formatted2);
                    writter.write(formatted2);
                } else {
                    System.out.printf("%" + tbWidth[cnt] + "s|", " ");
                    String formatted2 = String.format("%" + tbWidth[cnt] + "s|", " ");
                    writter.write(formatted2);
                }
                cnt++;
            }
            System.out.println();
            writter.write("\n");
            mState++;
        }
    }
}
