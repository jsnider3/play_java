import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class StoryDownloader {

  public static void main(String[] args) throws IOException, InterruptedException {
    String baseURL =
        "https://forums.spacebattles.com/threads/the-coffin-of-roboute-and-his-20-sisters-canon-guilliman-peggy-sue-into-female-primarchs-au.1132597/reader/";
    // int pageNum = 1;
    StringBuilder storyContent = new StringBuilder();

    for (int pageNum = 1; pageNum < 4; pageNum++) {
      String pageURL = baseURL + (pageNum > 1 ? "page-" + pageNum : "");
      String pageContent = downloadPage(pageURL);

      if (pageContent.isEmpty()) {
        // No more pages found
        break;
      }

      storyContent.append(filterContent(pageContent));
      Thread.sleep(4000);
    }

    saveStoryToFile(storyContent.toString());
  }

  public static String filterContent(String htmlSource) {
    StringBuilder filteredContent = new StringBuilder();

    Document doc = Jsoup.parse(htmlSource);

    // Select the elements with the desired classes
    Elements elements = doc.select("div.bbWrapper, span.threadmarkLabel");

    for (Element element : elements) {
      // for (Element element : elements) {
      if (element.tagName().equals("div")) {
        filteredContent.append(
            sanitizeHtmlWithPandocModelB(
                element)); // sanitizeHtmlForLatex(element.text())); // Preserve HTML structure
        // foo(element);
      } else if (element.tagName().equals("span")) {
        filteredContent.append("\\addcontentsline{toc}{section}{" + element.text() + "}\n");
        filteredContent.append("\\section*{" + element.text() + "}");
      }
      // }
      filteredContent.append("\n");
    }

    return filteredContent.toString();
  }

  public static String sanitizeHtmlWithPandocModelB(Element divElement) {
    String pandocOutput = "";

    try {
      // Create Process for pandoc command
      Process pandocProcess = Runtime.getRuntime().exec("pandoc --from=html --to=latex");

      // Get output and input streams
      BufferedReader stdoutReader =
          new BufferedReader(new InputStreamReader(pandocProcess.getInputStream()));
      OutputStream stdinWriter = pandocProcess.getOutputStream();

      // Send div content to pandoc through stdin
      String inhtml = divElement.html() /*
                                .replaceAll("<div style=\"text-align: center\">(.+?)</div>", "\\\\centering\n$1\n")
                                .replaceAll("<div style=\"text-align: right\">(.+?)</div>", "\\\\raggedleft\n$1\n")*/;
      stdinWriter.write(inhtml.getBytes());
      stdinWriter.close();

      // Read pandoc output from stdout
      String outputLine;
      while ((outputLine = stdoutReader.readLine()) != null) {
        pandocOutput += outputLine + "\n";
      }

      // Wait for pandoc to finish, checking for errors
      int exitVal = pandocProcess.waitFor();
      if (exitVal != 0) {
        // Handle pandoc error (consider logging or throwing an exception)
        System.err.println("pandoc error: exit value " + exitVal);
      }
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }

    return pandocOutput;
  }

  // Function to handle div elements using pandoc
  public static String sanitizeHtmlWithPandoc(Element divElement) {
    String pandocOutput = "";

    try {
      // Create Process for pandoc command
      Process pandocProcess = Runtime.getRuntime().exec("pandoc --from=html --to=latex");

      // Get output and input streams
      BufferedReader stdoutReader =
          new BufferedReader(new InputStreamReader(pandocProcess.getInputStream()));
      OutputStream stdinWriter = pandocProcess.getOutputStream();

      // Send div content to pandoc through stdin
      stdinWriter.write(divElement.text().getBytes());
      stdinWriter.close();

      // Read pandoc output from stdout
      String outputLine;
      while ((outputLine = stdoutReader.readLine()) != null) {
        pandocOutput += outputLine + "\n";
      }

      // Wait for pandoc to finish, checking for errors
      int exitVal = pandocProcess.waitFor();
      if (exitVal != 0) {
        // Handle pandoc error (consider logging or throwing an exception)
        System.err.println("pandoc error: exit value " + exitVal);
      }
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }

    return pandocOutput;
  }

  private static final Pattern SPECIAL_CHARS_PATTERN = Pattern.compile("[\\\\$&%#_{}~^]");
  private static final Pattern ZERO_WIDTH_SPACE_PATTERN = Pattern.compile("\\u200B");
  private static final String EM_DASH = "—";

  public static String sanitizeHtmlForLatex(String htmlString) {
    // Escape LaTeX special characters
    String escaped = SPECIAL_CHARS_PATTERN.matcher(htmlString).replaceAll("\\\\$0");

    // Replace zero-width space with empty string
    escaped = ZERO_WIDTH_SPACE_PATTERN.matcher(escaped).replaceAll("");

    // Replace em-dash with triple hyphen
    escaped = escaped.replace(EM_DASH, "---");
    escaped = escaped.replace("<br>", "\\linebreak");
    return escaped;
  }

  /*public static String sanitizeHtmlForLatex(String text) {

      // Escape LaTeX special characters
      String[] specialChars = {"$", "&", "%", "#", "_", "{", "}", "~", "^", "\\"};
      for (String specialChar : specialChars) {
          text = text.replace(specialChar, "\\" + specialChar);
      }

      // Replace em-dash with LaTeX em-dash
      text = text.replace("—", "---"); // Replace with actual em-dash character if needed

      // Fix Unicode issues (normalize and remove zero-width spaces)
      text = text.normalize("NFC", Normalizer.Form.NFKC);
      text = text.replaceAll("\\p{IsZeroWidth}", "");

      // (Optional) Remove remaining HTML tags
      text = text.replaceAll("<[^>]+>", "");

      return text;
  }*/

  public static String regexFilterContent(String htmlSource) {
    StringBuilder filteredContent = new StringBuilder();

    String regex = "<(?i)(div|span)\\s+class=\"(bbWrapper|threadmarkLabel)\">(.*?)</\\1>";
    // regex = "<div class=\"(bbWrapper|threadmarkLabel)\">(.*?)</div>";
    Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
    Matcher matcher = pattern.matcher(htmlSource);

    while (matcher.find()) {
      filteredContent.append(matcher.group(3)); // Append only the content within the matching tags
    }

    return filteredContent.toString();
  }

  private static String downloadPage(String urlString) throws IOException {
    URL url = new URL(urlString);
    URLConnection connection = url.openConnection();
    connection.setRequestProperty("User-Agent", "Mozilla/5.0"); // Imitate a web browser
    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
    StringBuilder pageContent = new StringBuilder();
    String line;
    while ((line = reader.readLine()) != null) {
      pageContent.append(line).append("\n");
    }
    reader.close();
    return pageContent.toString();
  }

  private static void saveStoryToFile(String storyContent) throws IOException {
    // FileWriter writer = new FileWriter("story.tex");
    try (BufferedWriter writer =
        new BufferedWriter(
            new OutputStreamWriter(
                new FileOutputStream("story.tex", true), StandardCharsets.UTF_8))) {
      writer.write("\\documentclass[11pt,twoside,a4paper]{article}\n");
      writer.write("\\usepackage{fontspec}\n");
      writer.write("\\usepackage{geometry}\n");
      writer.write("\\setmainfont{Arial}\n");
      writer.write(
          "\\title{The Coffin of Roboute and his 20 sisters (Canon Guilliman Peggy Sue into Female-Primarchs AU)}\n");
      writer.write("\\AddToHook{cmd/section/before}{\\clearpage}\n");
      writer.write("\\author{Brosef}\n");
      writer.write("\\date{ }\n");
      writer.write("\\begin{document}\n");
      writer.write("\\maketitle\n");
      writer.write("\\tableofcontents\n");
      writer.write("\\newpage\n");
      writer.write(storyContent);
      writer.write("\\end{document}\n");
      writer.close();
      System.out.println("Story saved to story.tex");
    }
  }
}
