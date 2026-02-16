package com.turnquest.reportservice.service.impl;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.firebase.cloud.StorageClient;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FirebaseService {

    private final Storage storage;

    /**
     * Initializes the Firebase Storage client using service account credentials.
     *
     * @throws IOException If there is an error reading the service account key file.
     */
    public FirebaseService() throws IOException {
        // Initialize the Firebase Storage client
        FileInputStream serviceAccount =
                new FileInputStream("src/main/resources/serviceAccountKey.json");
        StorageOptions storageOptions = StorageOptions.newBuilder()
                .setCredentials(ServiceAccountCredentials.fromStream(serviceAccount))
                .build();
        this.storage = storageOptions.getService();
    }

    /**
     * Downloads a file from Firebase Storage given its URL.
     *
     * @param fileUrl The URL of the file to download from Firebase Storage.
     * @return A byte array containing the content of the downloaded file.
     * @throws IOException If there is an error during the download process or if the file is not found.
     */
    public byte[] downloadFromFirebase(String fileUrl) throws IOException {
        // Extract bucket name and file path from the URL
        String bucketName = "first-file-firebase.appspot.com"; // Replace with your Firebase Storage bucket name
        String filePath = extractFilePathFromUrl(fileUrl);

        // Retrieve the file from Google Cloud Storage
        Blob blob = storage.get(bucketName, filePath);
        if (blob == null) {
            throw new IOException("File not found in Firebase");
        }

        // Download the file content as a byte array
        return blob.getContent();
    }

    /**
     * Extracts the file path from a Firebase Storage URL.
     *
     * @param fileUrl The URL of the file in Firebase Storage.
     * @return The extracted file path that can be used to access the file in Firebase Storage.
     */
    private String extractFilePathFromUrl(String fileUrl) {
        // Example: https://firebasestorage.googleapis.com/v0/b/your-bucket-name/o/path%2Fto%2Fyour%2Ffile.pdf?alt=media
        // Should return: "path/to/your/file.pdf"
        String filePath = fileUrl.substring(fileUrl.indexOf("/o/") + 3, fileUrl.indexOf("?"));
        return filePath.replace("%2F", "/");
    }
}