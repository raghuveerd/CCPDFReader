package dr.reader;

public enum StatementProps {
    START("statement.start"),
    LINE_START("statement.transaction.first.word.contains"),
    END("statement.end"),
    INPUT_DIR("statement.input.dir"),
    OUTPUT_DIR("statement.output.dir"),
    PASSWORDS("statement.passwords");


    private String prop;

    StatementProps(String prop) {
        this.prop = prop;
    }

    public String getPropKey() {
        return this.prop;
    }
}
