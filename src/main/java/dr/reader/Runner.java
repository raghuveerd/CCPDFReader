package dr.reader;

public class Runner {
    public static void main(String[] args) {
        try {
            CSVStatementGenerator csvStatementGenerator = new CSVStatementGenerator();
            csvStatementGenerator.generate(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
