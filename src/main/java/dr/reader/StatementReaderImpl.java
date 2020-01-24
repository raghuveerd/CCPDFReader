package dr.reader;

import dr.reader.transaction.TransactionCSVEntry;
import dr.reader.transaction.TransactionCSVEntryBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.regex.Pattern;

public class StatementReaderImpl implements StatementReader {

    private static final Logger log = LogManager.getLogger(StatementReaderImpl.class);
    private static final String UNCATEGORISED_CATEGORY = "UNCATEGORISED";

    public List<String> getTransactionsAsCSV(String cardFileName, String[] lines, Properties cardProps, Map<String, List<String>> categories) {
        List<String> transactions = getTransactionLines(lines, cardProps);
        List<String> transactionEntries = new ArrayList<>();

        //ICICI statements contains duplicate entries when there are more statements
        //they do this to show everything back in one page.
        List<String> refIds = new ArrayList<>(transactions.size());

        String cardName = cardFileName.substring(0, cardFileName.indexOf("."));

        TransactionCSVEntryBuilder builder = new TransactionCSVEntryBuilder(cardName);

        for (String transaction : transactions) {

            transaction = handleSpecialCases(transaction);

            List<String> transactionSplit = Arrays.asList(transaction.split(SINGLE_SPACE));
            String date = transactionSplit.get(0);
            String amount = transactionSplit.get(transactionSplit.size() - 1);
            String refId = transactionSplit.get(1);

            if (refIds.contains(refId)) {
                log.error("Duplicate Refid: " + refId);
                log.error("Duplicate transaction : " + transaction);
                continue;
            } else {
                refIds.add(refId);
            }

            //replace thousand separators
            amount = amount.replace(COMMA, EMPTY_STRING);
            String description = mergeListIntoString(transactionSplit, 2, transactionSplit.size() - 1);
            //replace comma in description
            description = description.replace(COMMA, SINGLE_SPACE);
            String isCredit = amount.endsWith(CREDIT_SUFFIX) ? CREDIT_SUFFIX : EMPTY_STRING;
            String category = getCategory(categories, transaction);

            TransactionCSVEntry entry = builder.date(date)
                    .refString(refId)
                    .amount(amount)
                    .description(description)
                    .type(isCredit)
                    .category(category)
                    .build();

            String csvString = entry.toString();
            log.info(csvString);
            transactionEntries.add(csvString);

        }
        return transactionEntries;
    }

    private String handleSpecialCases(String transaction) {
        //handle ICICI case where CR has space prefixed.
        if (transaction.endsWith(CREDIT_SUFFIX)) {
            transaction = transaction.replace(SINGLE_SPACE + CREDIT_SUFFIX, CREDIT_SUFFIX);
        }
        return transaction;
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

    private List<String> getTransactionLines(String[] lines, Properties cardProps) {
        List<String> transactions = new ArrayList<>();
        String startString = cardProps.getProperty(StatementProps.START.getPropKey());
        List<String> startStringList = Arrays.asList(startString.split(COMMA));
        String endString = cardProps.getProperty(StatementProps.END.getPropKey());
        List<String> endStringList = Arrays.asList(endString.split(COMMA));
        String statementStringIdentifier = cardProps.getProperty(StatementProps.LINE_START.getPropKey());
        String statementStringIdentifierRegex = cardProps.getProperty(StatementProps.LINE_REGEXP.getPropKey());

        List<Pattern> lineStartRegexPatterns = getLineStartRegexPatterns(statementStringIdentifierRegex);

        boolean statementStarted = false;
        boolean statementEnded = false;

        for (String line : lines) {
            if (isLineStartWith(startStringList, line) && !statementStarted) {
                statementStarted = true;
            }

            if (isLineStartWith(endStringList, line)) {
                statementEnded = true;
            }

            if (statementStarted && !statementEnded) {
                if (isStatementLineTransaction(line, statementStringIdentifier, lineStartRegexPatterns)) {
                    transactions.add(line);
                }
            }
        }
        return transactions;
    }

    private boolean isStatementLineTransaction(String line, String statementStringIdentifier, List<Pattern> lineStartRegexPatterns) {

        List<String> list = Arrays.asList(line.split(SINGLE_SPACE));
        String firstWord = list.size() > 0 ? list.get(0) : "";

        // Regex matching
        if (lineStartRegexPatterns.size() > 0) {
            for (Pattern p : lineStartRegexPatterns) {
                boolean isMatches = p.matcher(firstWord).matches();
                if (isMatches) {
                    return true;
                }
            }
        }

        // contains matching
        if (lineStartRegexPatterns.size() == 0) {
            return firstWord.contains(statementStringIdentifier) &&
                    Character.isDigit(firstWord.charAt(0));
        }
        return false;
    }

    private List<Pattern> getLineStartRegexPatterns(String statementStringIdentifierRegex) {
        List<Pattern> patterns = new ArrayList<>();
        List<String> patternStringList = Arrays.asList(statementStringIdentifierRegex.split(COMMA));
        for (String entry : patternStringList) {
            Pattern tempPattern = Pattern.compile(entry);
            patterns.add(tempPattern);
        }
        return patterns;
    }

    private boolean isLineStartWith(List<String> startStringList, String line) {
        for (String entry : startStringList) {
            if (line.startsWith(entry.trim()))
                return true;
        }
        return false;
    }

    private String mergeListIntoString(List<String> list, int start, int end) {
        StringBuilder builder = new StringBuilder();
        for (int i = start; i < end; i++) {
            builder.append(list.get(i)).append(SINGLE_SPACE);
        }
        return builder.toString();
    }
}
