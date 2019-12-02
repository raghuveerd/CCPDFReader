package dr.reader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class StatementReaderImpl implements StatementReader {

    private static final Logger log = LogManager.getLogger(StatementReaderImpl.class);
    private static final String UNCATEGORISED_CATEGORY = "UNCATEGORISED";

    public List<String> getTransactionsAsCSV(String[] lines, Properties cardProps, Map<String, List<String>> categories) {
        List<String> transactions = getTransactions(lines, cardProps);
        List<String> csvStrings = new ArrayList<>();
        for (String transaction : transactions) {

            transaction = handleSpecialCases(transaction);

            List<String> transactionSplit = Arrays.asList(transaction.split(SINGLE_SPACE));
            String date = transactionSplit.get(0);
            String amount = transactionSplit.get(transactionSplit.size() - 1);
            //replace thousand separators
            amount = amount.replace(COMMA, EMPTY_STRING);
            String description = mergeList(transactionSplit, 1, transactionSplit.size() - 1);
            //replace comma in description
            description = description.replace(COMMA, SINGLE_SPACE);
            String isCredit = amount.endsWith(CREDIT_SUFFIX) ? CREDIT_SUFFIX : EMPTY_STRING;
            String category = getCategory(categories, transaction);

            StringBuilder csvString = new StringBuilder();
            csvString.append(date);
            csvString.append(COMMA);
            csvString.append(description);
            csvString.append(COMMA);
            //replace CR in amount
            amount = amount.replace(CREDIT_SUFFIX, EMPTY_STRING);
            amount = checkAmount(amount);
            csvString.append(amount);
            csvString.append(COMMA);
            csvString.append(isCredit);
            csvString.append(COMMA);
            csvString.append(category);

            log.info(csvString.toString());
            csvStrings.add(csvString.toString());

        }
        return csvStrings;
    }

    private String handleSpecialCases(String transaction) {
        //handle ICICI case where CR has space prefixed.
        if (transaction.endsWith(CREDIT_SUFFIX)) {
            transaction = transaction.replace(SINGLE_SPACE + CREDIT_SUFFIX, CREDIT_SUFFIX);
        }
        return transaction;
    }

    private String checkAmount(String amount) {
        try {
            if (amount != null && Character.isDigit(amount.charAt(0))) {
                return amount;
            }
        } catch (Exception e) {
            log.error("Error checking amount for :" + amount, e);
        }
        return "0.0";
    }

    private String getCategory(Map<String, List<String>> categories, String description) {
        for (Map.Entry<String, List<String>> entry : categories.entrySet()) {
            String key = entry.getKey();
            List<String> catStrings = entry.getValue();
            for (String catString : catStrings) {
                if (description.contains(catString)) {
                    return key;
                }
            }
        }
        return UNCATEGORISED_CATEGORY;
    }

    private List<String> getTransactions(String[] lines, Properties cardProps) {
        List<String> transactions = new ArrayList<>();
        String startString = cardProps.getProperty(StatementProps.START.getPropKey());
        List<String> startStringList = Arrays.asList(startString.split(COMMA));
        String endString = cardProps.getProperty(StatementProps.END.getPropKey());
        List<String> endStringList = Arrays.asList(endString.split(COMMA));
        String statementStringIdentifier = cardProps.getProperty(StatementProps.LINE_START.getPropKey());
        boolean statementStarted = false;
        boolean statementEnded = false;

        for (String line : lines) {
            if (isLineStatementStart(startStringList, line) && !statementStarted) {
                statementStarted = true;
            }

            if (isLineStatementStart(endStringList, line)) {
                statementEnded = true;
            }

            if (statementStarted && !statementEnded) {
                List<String> list = Arrays.asList(line.split(SINGLE_SPACE));
                if (list.size() > 0 && list.get(0).contains(statementStringIdentifier) &&
                        Character.isDigit(list.get(0).charAt(0))) {
                    transactions.add(line);
                }
            }
        }
        return transactions;
    }

    private boolean isLineStatementStart(List<String> startStringList, String line) {
        for (String entry : startStringList) {
            if (line.startsWith(entry.trim()))
                return true;
        }
        return false;
    }


    private String mergeList(List<String> list, int start, int end) {
        StringBuilder builder = new StringBuilder();
        for (int i = start; i < end; i++) {
            builder.append(list.get(i)).append(SINGLE_SPACE);
        }
        return builder.toString();
    }
}
