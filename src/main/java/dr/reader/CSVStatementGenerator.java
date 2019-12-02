package dr.reader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;

import java.io.*;
import java.util.*;

public class CSVStatementGenerator {

    private static final Logger log = LogManager.getLogger(CSVStatementGenerator.class);
    private static final String CSV_HEADER = "DATE, DESCRIPTION, AMOUNT, TYPE, CATEGORY";

    private Properties getProp() throws Exception {
        InputStream input = CSVStatementGenerator.class.getClassLoader().getResourceAsStream("statement.properties");
        Properties prop = new Properties();
        prop.load(input);
        return prop;
    }

    public void generate(boolean consolidated) throws Exception {

        PDDocument document = null;

        try {

            Properties prop = getProp();
            Map<String, List<String>> categories = getCategorySettings(prop);

            String inputDir = prop.getProperty("statement.input.dir");
            String passwordString = prop.getProperty("statement.passwords");
            List<String> passwords = Arrays.asList(passwordString.split(","));

            File dir = new File(inputDir);
            File[] pdfFiles = dir.listFiles();

            PDFTextStripperByArea stripper = new PDFTextStripperByArea();
            stripper.setSortByPosition(true);

            PDFTextStripper tStripper = new PDFTextStripper();

            List<String> allCSVs = new ArrayList<>();

            if (pdfFiles != null) {
                for (File pdfFile : pdfFiles) {

                    boolean isPDFOpened = false;

                    for (String password : passwords) {
                        try {
                            document = PDDocument.load(pdfFile, password);
                            isPDFOpened = true;
                            break;
                        } catch (InvalidPasswordException ex) {
                            //don't do anything
                        }
                    }

                    if (!isPDFOpened) {
                        throw new Exception("Passwords does not work !!");
                    }


                    document.getClass();
                    String pdfFileInText = tStripper.getText(document);
                    // split by whitespace
                    String[] lines = pdfFileInText.split("\\r?\\n");

                    StatementReader reader = new StatementReaderImpl();
                    List<String> csvStrings = reader.getTransactionsAsCSV(lines, prop, categories);

                    if (!consolidated) {
                        writeOutputFile(pdfFile.getName(), prop, csvStrings);
                    } else {
                        allCSVs.addAll(csvStrings);
                    }

                }

                if (consolidated) {
                    writeOutputFile("statement", prop, allCSVs);
                }

            }


        } catch (Exception e) {
            log.error("Error !!!", e);
            System.out.println("ERROR !! Statement Generation Failed. Check Logs");
            throw new Exception("Error Generating Statements");
        } finally {
            if (document != null) {
                document.close();
            }
        }

        System.out.println("Statement Generation Completed");
    }

    private void writeOutputFile(String fileName, Properties prop, List<String> csvStrings) throws IOException {

        String outputDir = prop.getProperty("statement.output.dir");
        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;
        try {
            fileWriter = new FileWriter(outputDir + File.separator + fileName + ".csv");
            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(CSV_HEADER);
            bufferedWriter.newLine();
            for (String csvString : csvStrings) {
                bufferedWriter.write(csvString);
                bufferedWriter.newLine();
            }
        } catch (Exception e) {
            log.error("Error!!", e);
            throw e;
        } finally {
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (fileWriter != null) {
                fileWriter.close();
            }
        }


    }

    private Map<String, List<String>> getCategorySettings(Properties prop) {
        Map<String, List<String>> categories = new HashMap<>();
        Set<String> propertyNames = prop.stringPropertyNames();

        for (String propertyName : propertyNames) {
            if (propertyName.startsWith("CATEGORY")) {
                List<String> categoryEntries = Arrays.asList(prop.getProperty(propertyName).split(","));
                categories.put(propertyName.replace("CATEGORY.", ""), categoryEntries);
            }
        }
        return categories;
    }

}
