import java.io.*;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
         try (BufferedReader ifs = new BufferedReader(new FileReader("C:\\Users\\user\\IdeaProjects\\laba_analyser\\src\\input.txt"));
              BufferedReader ifsGram = new BufferedReader(new FileReader("C:\\Users\\user\\IdeaProjects\\laba_analyser\\src\\grammar.txt"));
              BufferedWriter writer = new BufferedWriter(new FileWriter("C:\\Users\\user\\IdeaProjects\\laba_analyser\\src\\output.txt")))
         {
             LrGrammar grammar = new LrGrammar();
             FileData.fillDataFromFile(ifsGram, grammar);
             grammar.print();

             String textForParsing = FileData.fillRawDataFromFile(ifs);
             System.out.println(textForParsing);

             LrGraph graph = new LrGraph(grammar);
             graph.defMultipleStates();
             graph.printGraphStates(writer);

             LrTable table = new LrTable(graph, grammar);
             table.generateParseTable();
             table.printParseTable(writer);

             LrDetAutoStoreMem automate = new LrDetAutoStoreMem(grammar, table);
             automate.stringParsing(textForParsing);
             automate.printImplActionsData();

         } catch (FileNotFoundException e){
             System.out.println("File not found!");
             throw new FileNotFoundException(e.toString());
         } catch (SecurityException e) {
            System.out.println("Permission denied when trying to access files");
            throw new SecurityException(e);
         } catch (IOException e) {
             throw new RuntimeException(e);
         }
    }
}