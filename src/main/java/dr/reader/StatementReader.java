package dr.reader;

import java.util.List;
import java.util.Map;
import java.util.Properties;

public interface StatementReader {
    String CREDIT_SUFFIX = "CR";
    String SINGLE_SPACE = " ";
    String EMPTY_STRING = "";
    String COMMA = ",";

    List<String> getTransactionsAsCSV(String name, String[] lines, Properties cardProps, Map<String, List<String>> categories);
}
