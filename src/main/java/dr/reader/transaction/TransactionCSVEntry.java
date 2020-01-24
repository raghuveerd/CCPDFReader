package dr.reader.transaction;

/*
Representation of a Transaction in CSV.
Columns are as below.
CARD, DATE, REF, DESCRIPTION, AMOUNT, TYPE, CATEGORY"
 */

import dr.reader.StatementReader;

public class TransactionCSVEntry {

    private final String card;
    private final String date;
    private final String refString;
    private final String description;
    private final String amount;
    private final String type;
    private final String category;

    public TransactionCSVEntry(TransactionCSVEntryBuilder builder) {
        this.card = builder.getCard();
        this.date = builder.getDate();
        this.refString = builder.getRefString();
        this.description = builder.getDescription();
        this.amount = builder.getAmount();
        this.type = builder.getType();
        this.category = builder.getCategory();
    }

    public String toString() {
        return card + StatementReader.COMMA +
                date + StatementReader.COMMA +
                refString + StatementReader.COMMA +
                description + StatementReader.COMMA +
                amount + StatementReader.COMMA +
                type + StatementReader.COMMA +
                category + StatementReader.COMMA;
    }
}
