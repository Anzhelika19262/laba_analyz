import java.io.*;

public class Main {
    public static LrGrammar grammar = new LrGrammar();
    public static LrGraph graph = new LrGraph(grammar);
    public static LrTable table = new LrTable(graph, grammar);
    public static LrDetAutoStoreMem automate = new LrDetAutoStoreMem(grammar, table);

    public static void main(String[] args) throws FileNotFoundException {
         try (BufferedReader ifs = new BufferedReader(new FileReader("input.txt"));
              BufferedReader ifsGram = new BufferedReader(new FileReader("grammar.txt")))
         {
             FileData.fillDataFromFile(ifsGram, grammar);
             grammar.print();

             String textForParsing = FileData.fillRawDataFromFile(ifs);
             System.out.println(textForParsing);
             graph.defMultipleStates();
             //graph.printGraphStates(ofs); надо BufferWriter передавать

             table.generateParseTable();
             //table.printParseTable(ofs); надо BufferWriter передавать

             /* Согласно таблицы разбора производим проверку (парсинг) данных из входного файла */
             automate.stringParsing(textForParsing);
             //automateюprintImplActionsData();

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