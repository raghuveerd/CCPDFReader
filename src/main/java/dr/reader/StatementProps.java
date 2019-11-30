package dr.reader;

public enum StatementProps {
    START("statement.start"),
    LINE_START("statement.transaction.first.word.contains"),
    END("statement.end");

    private String prop;

    StatementProps(String prop) {
        this.prop = prop;
    }

    public String getPropKey() {
        return this.prop;
    }
}
