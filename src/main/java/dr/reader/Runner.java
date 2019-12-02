package dr.reader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Runner {
    private static final Logger log = LogManager.getLogger(Runner.class);

    public static void main(String[] args) {
        try {
            CSVStatementGenerator csvStatementGenerator = new CSVStatementGenerator();
            csvStatementGenerator.generate(true);
        } catch (Exception e) {
            log.error("Error Generating CSV", e);
            System.out.println("Error Generating CSV !! Check logs.");
        }
    }
}
