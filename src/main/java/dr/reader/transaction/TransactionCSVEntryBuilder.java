package dr.reader.transaction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static dr.reader.StatementReader.CREDIT_SUFFIX;
import static dr.reader.StatementReader.EMPTY_STRING;

public class TransactionCSVEntryBuilder {

    private static final Logger log = LogManager.getLogger(TransactionCSVEntryBuilder.class);
    private final String card;
    private String date;
    private String refString;
    private String description;
    private String amount;
    private String type;
    private String category;


    public TransactionCSVEntry build() {
        TransactionCSVEntry user = new TransactionCSVEntry(this);
        return user;
    }

    public TransactionCSVEntryBuilder(String card) {
        this.card = card;
    }

    public TransactionCSVEntryBuilder date(String date) {
        this.date = date;
        return this;
    }

    public TransactionCSVEntryBuilder refString(String refString) {
        this.refString = refString;
        return this;
    }

    public TransactionCSVEntryBuilder description(String description) {
        this.description = description;
        return this;
    }

    public TransactionCSVEntryBuilder amount(String amount) {

        //replace CR in amount
        amount = amount.replace(CREDIT_SUFFIX, EMPTY_STRING);
        amount = checkAmount(amount);

        this.amount = amount;
        return this;
    }

    public TransactionCSVEntryBuilder type(String type) {
        this.type = type;
        return this;
    }

    public TransactionCSVEntryBuilder category(String category) {
        this.category = category;
        return this;
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


    //getters


    public String getCard() {
        return card;
    }

    public String getDate() {
        return date;
    }

    public String getRefString() {
        return refString;
    }

    public String getDescription() {
        return description;
    }

    public String getAmount() {
        return amount;
    }

    public String getType() {
        return type;
    }

    public String getCategory() {
        return category;
    }
}
