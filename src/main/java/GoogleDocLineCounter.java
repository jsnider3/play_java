import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GoogleDocLineCounter {
    private static final String APPLICATION_NAME = "Google Doc Line Counter";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE_READONLY);

    public static void main(String[] args) {
        String documentId = "1U-GwlUUzzwI8IeLlKQQXntx6luFNJt_ZSEICQkwxx14";
        String credentialsFilePath = "./credentials.json";

        try {
            Drive driveService = createDriveService(credentialsFilePath);
            downloadGoogleDoc(driveService, documentId, "downloaded_doc.txt");
            int lineCount = countLinesStartingWith("downloaded_doc.txt", "##");
            System.out.println("Number of lines starting with '##': " + lineCount);
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
        }
    }

    private static Drive createDriveService(String credentialsFilePath) throws IOException, GeneralSecurityException {
        GoogleCredential credential = GoogleCredential.fromStream(new FileInputStream(credentialsFilePath))
                .createScoped(SCOPES);

        return new Drive.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    private static void downloadGoogleDoc(Drive driveService, String documentId, String outputFile)
            throws IOException {
        File fileMetadata = driveService.files().get(documentId).execute();
        String mimeType = "text/plain";

        try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
            driveService.files().export(documentId, mimeType)
                    .executeMediaAndDownloadTo(outputStream);
        }
    }

    private static int countLinesStartingWith(String filePath, String prefix) throws IOException {
        int lineCount = 0;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(filePath), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(prefix)) {
                    lineCount++;
                }
            }
        }

        return lineCount;
    }
}
