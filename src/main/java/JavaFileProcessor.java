import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.*;

public class JavaFileProcessor {

  public static void main(String[] args) {
    String startingDirectory = "."; // Replace with your directory path
    processDirectory(startingDirectory);
  }

  private static void processDirectory(String directoryPath) {
    try (Stream<Path> paths = Files.walk(Paths.get(directoryPath))) {
      paths
          .filter(Files::isRegularFile)
          .filter(path -> path.toString().endsWith(".java"))
          .forEach(JavaFileProcessor::processJavaFile);
    } catch (IOException e) {
      System.err.println("Error walking directory: " + e.getMessage());
    }
  }

  private static void processJavaFile(Path javaFilePath) {
    try {
      // Read the file content
      String content = Files.readString(javaFilePath);

      // Call your "foo" function on the content
      String modifiedContent = foo(content);

      // Generate the output filename (".java.mod")
      String outputFilename = javaFilePath.toString() + ".mod";

      // Write the modified content to the .java.mod file
      Files.writeString(Paths.get(outputFilename), modifiedContent);

    } catch (IOException e) {
      System.err.println("Error processing file " + javaFilePath + ": " + e.getMessage());
    }
  }

  public static String foo(String javaCode) {
    // TODO Format the given Java code.
    return javaCode;
  }
}
