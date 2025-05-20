import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Stack;

public class LrDetAutoStoreMem {
    protected LrGrammar gram;
    protected LrTable	table;
    protected Stack<TableCell> store = new Stack<>();
    protected ArrayList<SemanticType> implStore = new ArrayList<>();
    protected static String [] reservedWords = {"void", "int", "char", "bool", "float", "double", "short", "long", "...", "namespace"};

    public LrDetAutoStoreMem(LrGrammar gr, LrTable tb){
        gram = gr;
        table = tb;
    }

    public boolean isSymbolInSet(String strSet, String inpStr) {
        int size = strSet.length();
        if (size > inpStr.length() && inpStr.length() == 1) {
            char ch = inpStr.charAt(0);
            char setStart = strSet.charAt(0);
            char setEnd = strSet.charAt(size - 1);

            if (strSet.equals("\\s") && (ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r')) {
                return true;
            } else return strSet.charAt(1) == '-' && setEnd >= setStart && ch >= setStart && ch <= setEnd;
        }
        return false;
    }

    public boolean isAllowSymbol(char ch) {
        return ch == '_' || (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || (ch >= '0' && ch <= '9');
    }

    public int determReserveWord(String str, int position) {
        String formNewString = str.substring(position);

        for (String word : reservedWords) {
            if (formNewString.compareTo(word) == 0) {
                if (position > 0 && (isAllowSymbol(str.charAt(position - 1))
                        || isAllowSymbol(str.charAt(position + word.length())))) {
                    break;
                }
                return word.length();
            }
        }
        return 0;
    }

    public int stringParsing(String parseStr) {
        int linePos = 1;
        int itemPos = 0;
        String tmp = "";
        TableCell elStore = new TableCell('S', 0);

        store.push(elStore);

        int len, i = 0;
        if ((len = determReserveWord(parseStr, i)) > 0) {
            tmp = parseStr.substring(i, len);
            i += len;
        }  else  {
            tmp = Character.toString(parseStr.charAt(i++));
        }

        itemPos = i;

        if (tmp.equals("\n")) {
            linePos++;
            itemPos = 0;
        }

        int linePositionInTable = 0;
        ParseErrorStatus status = ParseErrorStatus.PARSE_ERROR_TABLE;

        while (true) {
            if (getElementPositionInLine(linePositionInTable, tmp, elStore) != 0)
                break;
            if (implAction(elStore, tmp) != 0) {
                if (Objects.equals(tmp, "\n"))
                    linePos--;
                status = ParseErrorStatus.PARSE_ERROR_ACTION;
                break;
            }

            if (elStore.getStateSymb() == 'R') {
                int numLine = elStore.getNumState();

                for (String k: gram.grammar.get(numLine).getRight()) {
                    store.pop();
                }

                elStore = store.peek();
                elStore.setLableElem(gram.grammar.get(numLine).getLeft());
                linePositionInTable = elStore.getNumState();

                if (getElementPositionInLine(linePositionInTable, elStore.getLableElem(), elStore) != 0)
                    break;
                if (implAction(elStore, elStore.getLableElem()) != 0) {
                    if (Objects.equals(tmp, "\n"))
                        linePos--;
                    status = ParseErrorStatus.PARSE_ERROR_ACTION;
                    break;
                }
                store.push(elStore);
                linePositionInTable = elStore.getNumState();
            } else if (elStore.getStateSymb() == 'H') {
                status = ParseErrorStatus.PARSE_SUCCESS;
                break;
            } else {	/* elStore.stateSymb == 'S' */
                store.push(elStore);
                if ((len = determReserveWord(parseStr, i)) > 0) {
                    tmp = parseStr.substring(i, len);
                    i += len;
                    itemPos += len;
                } else {
                    tmp = Character.toString(parseStr.charAt(i++));
                    itemPos++;
                } if (tmp.equals("\n")) {
                    itemPos = 0;
                    linePos++;
                }

                linePositionInTable = elStore.getNumState();
            }
        }

        if (status == ParseErrorStatus.PARSE_SUCCESS) {
            System.out.println("Разбор выражения успешно завершён");
        } else if (status == ParseErrorStatus.PARSE_ERROR_TABLE) {
            System.out.println("Ошибка в " + linePos + " строке, " + itemPos + " позиции");
        } else if (status == ParseErrorStatus.PARSE_ERROR_ACTION) {
            System.out.println("Ошибка в " + linePos + " строке" );
        } else {
            System.out.println("Неизвестная ошибка в " + linePos + " строке, ");
        }

        return status.ordinal();
    }

    public int getElementPositionInLine(int linePosition, String inputText, TableCell cell) {
        for (int k = 0; k < table.getLine(linePosition).size(); ++k) {
            TableCell tbCell = table.getLine(linePosition).get(k);
            if (Objects.equals(tbCell.getLableElem(), inputText)
                    || isSymbolInSet(tbCell.getLableElem(), inputText)) {
                cell.setStateSymb(tbCell.getStateSymb());
                cell.setNumState(tbCell.getNumState());
                cell.setLableElem(tbCell.getLableElem());
                cell.setAction(tbCell.getAction());
                return 0;
            }
        }
        return 1;
    }

    public int implAction(TableCell cell, String lex) {
        int level = 0;
        String type = "";
        StringBuilder varName = new StringBuilder();

        cell.setAction(cell.action.replaceAll("<", " "));
        cell.setAction(cell.action.replaceAll(">", " "));

        SemanticType st = new SemanticType();

        for (String sa: cell.getAction().trim().split("\\s+")) {
            if (Objects.equals(sa, "TYPE") || Objects.equals(sa, "NAMESPACE")) {
                type = lex;
            } else if (Objects.equals(sa, "ADDPARAM") || Objects.equals(sa, "ADDFUNCNAME")
                    || Objects.equals(sa, "ADDNAMESPACE")) {
                DefVar dv = new DefVar();
                switch (sa) {
                    case "ADDFUNCNAME" -> dv.setEssence(SecName.FUNC_NAME);
                    case "ADDPARAM" -> dv.setEssence(SecName.ARG_NAME);
                    case "ADDNAMESPACE" -> dv.setEssence(SecName.SPACE_NAME);
                    default -> dv.setEssence(SecName.NO_NAME);
                }

                dv.setTypeName(type);
                dv.setVarName(varName.toString());
                st.getVar().add(dv);

                type = "";
                varName = new StringBuilder();
            } else if (Objects.equals(sa, "LEVEL+")) {
                level++;
            } else if (Objects.equals(sa, "LEVEL-")) {
                level = (level > 0) ? (level - 1) : (0);
            } else if (Objects.equals(sa, "LIT")) {
                varName.append(lex);
            } else if (Objects.equals(sa, "CHECKARGLIST")) {
                ArrayList<DefVar> v = st.getVar();
                if (v.size() > 2) {
                    for (int i = 1; i < v.size(); ++i) {
                        for (int j = 2; j < v.size(); ++j) {
                            if (i == j || v.get(i).getEssence() != SecName.ARG_NAME || v.get(j).getEssence() != SecName.ARG_NAME)	break;
                            String var1 = ((v.get(i).getVarName().charAt(0) == '&' || v.get(i).getVarName().charAt(0) == '*') ? (v.get(i).getVarName().substring(1)) : (v.get(i).getVarName()));
                            String var2 = ((v.get(j).getVarName().charAt(0) == '&' || v.get(j).getVarName().charAt(0) == '*') ? (v.get(j).getVarName().substring(1)) : (v.get(j).getVarName()));
                            if (Objects.equals(var1, var2)) {
                                System.out.println("Конфликт имён параметров функции!\n");
                                return -1;
                            }
                        }
                    }
                }
            } else if (Objects.equals(sa, "CHECKPROTO")) {
                st.setLevel(level);
                implStore.add(st);
                st.clear();
                for (int i = 0; i < implStore.size(); ++i) {
                    for (int j = 1; j < implStore.size(); ++j) {
                        if (i == j || implStore.get(i).getVar().get(0).getEssence() != implStore.get(j).getVar().get(0).getEssence() || implStore.get(i).getLevel() != implStore.get(j).getLevel() ||
                                implStore.get(i).getVar().size() != implStore.get(j).getVar().size() || !Objects.equals(implStore.get(i).getVar().get(0).getVarName(), implStore.get(j).getVar().get(0).getVarName()))
                            continue;

                        ArrayList<DefVar> v = implStore.get(i).var;
                        ArrayList<DefVar> v2 = implStore.get(j).var;
                        boolean checkListParams = true;

                        for (int k = 0; k < v.size(); ++k) {
                            if (!Objects.equals(v.get(k).getTypeName(), v2.get(k).getTypeName())) {
                                checkListParams = false;
                                break;
                            } else {
                                if ((v.get(k).getVarName().charAt(0) == '*' || v.get(k).getVarName().charAt(0) == '&')
                                        || (v2.get(k).getVarName().charAt(0) == '*' || v2.get(k).getVarName().charAt(0) == '&')) {
                                    if (v.get(k).getVarName().charAt(0) != v2.get(k).getVarName().charAt(0)) {
                                        checkListParams = false;
                                        break;
                                    }
                                }
                            }
                        }
                        if (checkListParams) {
                            System.out.println("Конфликт, повторное объявление функции!\n");
                            return -1;
                        }
                    }
                }
            }
        }
        return 0;
    }
    public void printImplActionsData() {
        for (SemanticType itSt : implStore) {
            for (DefVar itVar : itSt.getVar()) {
                System.out.println("Type_name=" + itVar.getEssence() + " " + itVar.getTypeName() + " " + itVar.getVarName());
            }
            System.out.println("level=" + itSt.getLevel());
            System.out.println("=====================");
        }
    }

}
