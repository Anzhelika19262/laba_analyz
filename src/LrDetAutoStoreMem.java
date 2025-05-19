import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Stack;

public class LrDetAutoStoreMem {
    LrGrammar gram;
    LrTable	table;
    Stack<TableCell> store = new Stack<>();
    ArrayList<SemanticType> implStore = new ArrayList<>();
    static ArrayList<String> reservedWords = new ArrayList<>();

    public LrDetAutoStoreMem(LrGrammar gr, LrTable tb){
        gram = gr;
        table = tb;
    }

    boolean isSymbolInSet(String strSet, String inpStr) {
        int size = strSet.length();
        if (strSet.length() > inpStr.length() && inpStr.length() == 1) {
            char ch = inpStr.charAt(0);
            char setStart = strSet.charAt(0);
            char setEnd = strSet.charAt(size - 1);

            if (strSet.equals("\\s") && (ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r')) {
                return true;
            } else return (size > 1 && strSet.charAt(1) == '-') && setEnd >= setStart && ch >= setStart && ch <= setEnd;
        }
        return false;
    }

    boolean isAllowSymbol(char ch) {
        return ch == '_' || (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || (ch >= '0' && ch <= '9');
    }

    int determReserveWord(String str, int position) {
        String formNewString = str.substring(position);

        for (String word : reservedWords) {
            if (formNewString.compareTo(word) == 0) {
                if (position > 0 && (isAllowSymbol(str.charAt(position - 1)) || isAllowSymbol(str.charAt(position + word.length())))) {
                    int a = 1;
                    break;
                }
                return word.length();
            }
        }
        return 0;
    }

    int stringParsing(String str) {
        int linePos = 1;
        int itemPos = 0;
        String parseStr = str;
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
                if (Objects.equals(tmp, "\n")) linePos--;
                status = ParseErrorStatus.PARSE_ERROR_ACTION;
                break;
            }

            if (elStore.stateSymb == 'R') {
                int numLine = elStore.numState;

                for (String k: gram.grammar.get(numLine).right) {
                    store.pop();
                }

                elStore = store.firstElement();
                elStore.lableElem = gram.grammar.get(numLine).left;
                linePositionInTable = elStore.numState;

                if (getElementPositionInLine(linePositionInTable, elStore.lableElem, elStore) != 0)
                    break;
                if (implAction(elStore, elStore.lableElem) != 0) {
                    if (Objects.equals(tmp, "\n")) linePos--;
                    status = ParseErrorStatus.PARSE_ERROR_ACTION;
                    break;
                }
                store.push(elStore);
                linePositionInTable = elStore.numState;
            } else if (elStore.stateSymb == 'H') {
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

                linePositionInTable = elStore.numState;
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

        return status.ordinal(); //?
    }

    int getElementPositionInLine(int linePosition, String inputText, TableCell cell) {
        for (int k = 0; k < table.getLine(linePosition).size(); ++k) {
            TableCell tbCell = table.getLine(linePosition).get(k);
            if (Objects.equals(tbCell.lableElem, inputText) || isSymbolInSet(tbCell.lableElem, inputText)) {
                cell = tbCell;
                return 0;
            }
        }
        return 1;
    }

    int implAction(TableCell cell, String lex) {
        int level = 0;
        String type = "";
        StringBuilder varName = new StringBuilder(); // тип данных идентификатора

        cell.action = cell.action.replaceAll("<", " ");
        cell.action = cell.action.replaceAll(">", " ");

        SemanticType st = new SemanticType();

        /* Вычитываем последовательно внедрённые действия из потока, помещая их в строковую переменную */
        for (String sa: cell.action.trim().split("\\s+")) {
            if (Objects.equals(sa, "TYPE") || Objects.equals(sa, "NAMESPACE")) {
                type = lex;
            } else if (Objects.equals(sa, "ADDPARAM") || Objects.equals(sa, "ADDFUNCNAME") || Objects.equals(sa, "ADDNAMESPACE")) {
                DefVar dv = new DefVar();
                switch (sa) {
                    case "ADDFUNCNAME" -> dv.essence = SecName.FUNC_NAME;
                    case "ADDPARAM" -> dv.essence = SecName.ARG_NAME;
                    case "ADDNAMESPACE" -> dv.essence = SecName.SPACE_NAME;
                    default -> dv.essence = SecName.NO_NAME;
                }

                dv.typeName = type;
                dv.varName = varName.toString();
                st.var.add(dv);
                type = "";
                varName = new StringBuilder();
            } else if (Objects.equals(sa, "LEVEL+")) {
                level++;
            } else if (Objects.equals(sa, "LEVEL-")) {
                level = (level > 0) ? (level-1) : (0);
            } else if (Objects.equals(sa, "LIT")) {
                varName.append(lex);
            } else if (Objects.equals(sa, "CHECKARGLIST")) {
                ArrayList<DefVar> v = st.var;
                if (v.size() > 2) {
                    for (int i = 1; i < v.size(); ++i) {
                        for (int j = 2; j < v.size(); ++j) {
                            if (i == j || v.get(i).essence != SecName.ARG_NAME || v.get(j).essence != SecName.ARG_NAME)	break;
                            String var1 = ((v.get(i).varName.charAt(0) == '&' || v.get(i).varName.charAt(0) == '*') ? (v.get(i).varName.substring(1)) : (v.get(i).varName));
                            String var2 = ((v.get(j).varName.charAt(0) == '&' || v.get(j).varName.charAt(0) == '*') ? (v.get(j).varName.substring(1)) : (v.get(j).varName));
                            if (Objects.equals(var1, var2)) {
                                System.out.println("Конфликт имён параметров функции!\n");
                                return -1;
                            }
                        }
                    }
                }
            } else if (Objects.equals(sa, "CHECKPROTO")) {
                st.level = level;
                implStore.add(st);
                st.clear();
                for (int i = 0; i < implStore.size(); ++i) {
                    for (int j = 1; j < implStore.size(); ++j) {
                        if (i == j || implStore.get(i).var.get(0).essence != implStore.get(j).var.get(0).essence || implStore.get(i).level != implStore.get(j).level ||
                                implStore.get(i).var.size() != implStore.get(j).var.size() || implStore.get(i).var.get(0).varName != implStore.get(j).var.get(0).varName) continue;

                        ArrayList<DefVar> v = implStore.get(i).var;
                        ArrayList<DefVar> v2 = implStore.get(j).var;
                        boolean checkListParams = true;

                        for (int k = 0; k < v.size(); ++k) {
                            if (!Objects.equals(v.get(k).typeName, v2.get(k).typeName) /*&& v[k].varName[0] != v2[k].varName[0]*/) {
                                checkListParams = false;
                                break;
                            } else {
                                if ((v.get(k).varName.charAt(0) == '*' || v.get(k).varName.charAt(0) == '&')
                                        || (v2.get(k).varName.charAt(0) == '*' || v2.get(k).varName.charAt(0) == '&')) {
                                    if (v.get(k).varName.charAt(0) != v2.get(k).varName.charAt(0)) {
                                        checkListParams = false;
                                        break;
                                    }
                                }
                                //cout << v[k].typeName << " " << v[k].varName[0] << " " << v2[k].typeName << " " << v2[k].varName[0] << "\n";
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
    void printImplActionsData() {
        for (SemanticType itSt : implStore) {
            for (DefVar itVar : itSt.var) {
                System.out.println("Type_name=" + itVar.essence + " " + itVar.typeName + " " + itVar.varName);
            }
            System.out.println("level=" + itSt.level);
            System.out.println("=====================");
        }
    }

}
