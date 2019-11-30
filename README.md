# CCPDFReader
#### Generate CSV out of Credit Card PDF statments. 
- The idea is to read the PDF line by line and extract transaction lines as CSV. **statement.properties** can be used to specify the starting and ending lines in the PDF which contains the transactions.
- As each bank has a different representation of transactions, the propery _statement.transaction.first.word.contains_ is used to identify if the line is a transaction.
- The assumption is that the first word in transaction line is transaction date and last word is transaction amount.

Example: For Citi Bank Cash Back card, the first word in a transaction line is date represented as 22/06 for 22-June

    statement.start=Detailed Statement
    statement.transaction.first.word.contains=/
    statement.end=Your Cash Back Summary
- The properties file can be updated with comma seperated values to handle different banks. I tested Citi and ICIC bank's statements.
- The generated CSV contains **DATE, DESCRIPTION, AMOUNT, TYPE, CATEGORY** columns
- Specify transaction Categories in **statement.properties**. The property needs to start with _CATEGORY._

Example:

    CATEGORY.AMAZON=AMAZON
    CATEGORY.FLIPCART=Flipkart
    CATEGORY.HEALTH_CARE=MEDPLUS
    CATEGORY.BILLS=AIRTEL,ACTTV,Max Life- 
    CATEGORY.COMMUTE=OLA,Uber,UBER
    
        
#### Building and Running
- Includes Ant build.xml and pom.xml.
- For Ant, run the target _package_ which builds the dist in _target_ directory.
- Run _reader.bat_ in dist by updating _JAVA_HOME_


**Drop PDFs in input folder and generate CSV. Convert CSVs to Excel and generate your favourite Pivots, Charts, Graphs** 
    

