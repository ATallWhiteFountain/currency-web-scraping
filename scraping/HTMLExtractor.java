package scraping;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;
import java.util.StringTokenizer;

/**
 * HTMLExtractor is a class designed to handle extraction of data from given URL.
 * @author Brandon Swatek
 */
class HTMLExtractor {

    private String urlName;
    private String safeWord;
    private String classKeyword;
    private boolean isValid;

    /**
     * A constructor saves key information to the class fields.
     * @param urlName is a URL from which data ought to be extracted
     * @param safeWord is a string which helps to identify given URL as working properly
     * @param classKeyword is a string that helps optimizing search through DOM
     * @throws IOException is an exception that may be generated in case of connection with URL fault
     */
    HTMLExtractor(String urlName, String safeWord, String classKeyword) throws IOException{
        this.urlName = urlName;
        this.safeWord = safeWord;
        this.classKeyword = classKeyword;
        this.isValid = validate();
    }

    /**
     * Getter of information whether URL is working properly.
     * @return a boolean with information whether URL is working properly
     */
    boolean getIsValid(){
        return isValid;
    }

    /**
     * A validation of an URL method, checking for response status, containing of HTML tags, and containing of
     * a given String that is expected to be found on the website.
     * @return a boolean with information whether URL is working properly
     * @throws IOException is an exception that may be generated in case of connection with URL fault
     */
    private boolean validate() throws IOException {
        Connection.Response response = Jsoup.connect(urlName).execute();
        Document doc = Jsoup.connect(urlName).get();
        boolean isHTML = doc.html().contains("html");
        boolean isValid = doc.title().contains(safeWord);
        return response.statusCode() == 200 && isHTML && isValid;
    }

    /**
     * A method that obtains searched String.
     * @param query a keyword after which a searched word should be found
     * @return searched for string
     * @throws IOException is an exception that may be generated in case of connection with URL fault
     */
    String extractDataFromURL(String query) throws IOException {
        Document doc = Jsoup.connect(urlName).get();
        String text = doc.getElementsByClass(classKeyword).text();
        return isolate(text, query);
    }

    /**
     * A method returning String with information needed by user from table structures.
     * @param data a larger string consisting of text obtained from HTML file
     * @param query a keyword after which a searched word should be found
     * @return searched for string
     */
    private String isolate(String data, String query){
        StringTokenizer st = new StringTokenizer(data, " ");
        String retVal = "";
        while (st.hasMoreTokens()){
            if(st.nextToken().equals(query)){
                retVal = st.nextToken();
                break;
            }
        }
        return retVal;
    }

    /**
     * A static method that runs a selected currency scrap by downloading information from Polish National Bank.
     * @throws IOException is an exception that may be generated in case of connection with URL fault
     */
    static void runCurrencyScrap() throws IOException {
        HTMLExtractor e = new HTMLExtractor("https://www.nbp.pl/home.aspx?f=/kursy/kursya.html", "Narodowy Bank Polski", "right");
        System.out.println("Aktualne kursy walut ze strony NBP:");
        System.out.println("- Kurs dolara (USD):" + e.extractDataFromURL("USD") );
        System.out.println("- Kurs euro (EUR):" + e.extractDataFromURL("EUR"));
        System.out.println("- Kurs franka szwajcarskiego (CHF):" + e.extractDataFromURL( "CHF"));
        System.out.println("- Kurs funta szterlinga (GBP):" + e.extractDataFromURL( "GBP"));
        System.exit(e.getIsValid() ? 0 : -1);
    }
}
