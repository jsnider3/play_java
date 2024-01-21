import java.io.IOException;
import java.util.ArrayList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class CountryListFetcher {

  public static void main(String[] args) throws IOException {

    String url = "https://www.cia.gov/the-world-factbook/countries/";

    // Connect to the website and parse HTML using Jsoup
    Document doc = Jsoup.connect(url).get();
    // System.out.println(doc);
    // Select the elements containing country names
    Elements countryLinks = doc.select("a.inline-link"); // Use the appropriate CSS selector

    // Create an ArrayList to store country names
    ArrayList<String> countries = new ArrayList<>();

    // Extract country names and add them to the array
    for (int i = 0; i < countryLinks.size(); i++) {
      String countryName = countryLinks.get(i).text();
      countries.add(countryName);
    }

    // Print the list of countries (or use the array for further processing)
    System.out.println("Countries:");
    for (String country : countries) {
      System.out.println(country);
    }
  }
} /**/
