import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class FileData {
    public static void fillDataFromFile(BufferedReader inputFile, LrGrammar grammar) throws IOException {
        String fileLine;
        int cntLine = 0;

        while ((fileLine = inputFile.readLine()) != null){
            GRRule newRule = new GRRule();
            String lineWord = "";
            String startWord = "";
            cntLine++;

            Scanner scanner = new Scanner(fileLine);
            if (scanner.hasNext())
                startWord = scanner.next();

            if (startWord.isEmpty() || startWord.charAt(0) == '#')
                continue;

            newRule.setLeft(startWord);

            if (scanner.hasNext())
                lineWord = scanner.next();

            if (!lineWord.contains("->"))
                System.out.println("Неверный формат записи файла с грамматикой, строка: " + cntLine);

            boolean isAction = true;
            while (scanner.hasNext()){
                lineWord = scanner.next();
                if (!lineWord.equals("|")){
                    if (lineWord.charAt(0) == '<' && lineWord.charAt(lineWord.length() - 1) == '>') {
                        newRule.addAction(lineWord);
                        isAction = true;
                    } else {
                        if (!isAction)
                            newRule.addAction("");
                        newRule.addRight(lineWord);
                        isAction = false;
                    }
                } else {
                    if (!isAction)
                        newRule.addAction("");
                    if (!newRule.getRight().isEmpty()) {
                        GRRule copyRule = new GRRule();
                        copyRule.setLeft(newRule.getLeft());
                        copyRule.setRight(new ArrayList<>(newRule.getRight()));
                        copyRule.setAction(new ArrayList<>(newRule.getAction()));

                        grammar.setRule(copyRule);

                        newRule.getRight().clear();
                        newRule.getAction().clear();
                        isAction = true;
                    }
                }
            }
            if (!isAction)
                newRule.addAction("");

            grammar.setRule(newRule);
            scanner.close();
        }
    }
    public static String fillRawDataFromFile(BufferedReader inputFile) throws IOException {
        StringBuilder text = new StringBuilder();
        String fileLine;

        while ((fileLine = inputFile.readLine()) != null){
            text.append((fileLine.length() > 0) ? (fileLine + "\n") : "\n");
        }
        text.append('$');
        return text.toString();
    }
}
