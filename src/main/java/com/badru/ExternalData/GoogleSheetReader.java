package com.badru.externaldata;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

/**
 * GoogleSheetReader: reads data from Google Sheets via the Sheets API.
 * Requires a Service Account JSON key file.
 */
public class GoogleSheetReader {
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String APPLICATION_NAME = "Katalon-GoogleSheetReader";

    /**
     * Initializes a Sheets service instance using a service account key.
     *
     * @param serviceAccountKeyPath path to the JSON key file
     * @return Sheets service
     * @throws IOException              if key file not found or bad format
     * @throws GeneralSecurityException if security initialization fails
     */
    private static Sheets getSheetsService(String serviceAccountKeyPath)
            throws IOException, GeneralSecurityException {
        InputStream credentialsStream = new FileInputStream(serviceAccountKeyPath);
        GoogleCredentials credentials = GoogleCredentials
                .fromStream(credentialsStream)
                .createScoped(Collections.singletonList(SheetsScopes.SPREADSHEETS_READONLY));

        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        return new Sheets.Builder(httpTransport, JSON_FACTORY,
                new HttpCredentialsAdapter(credentials))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    /**
     * Reads a range of cells from a Google Sheets spreadsheet.
     *
     * @param serviceAccountKeyPath JSON key file for the service account
     * @param spreadsheetId         the ID in the sheet's URL
     * @param range                 e.g. "Sheet1!A1:D"
     * @return List of Maps (header->value)
     * @throws IOException              on network or IO error
     * @throws GeneralSecurityException on security init failure
     */
    public static List<Map<String, String>> read(
            String serviceAccountKeyPath,
            String spreadsheetId,
            String range) throws IOException, GeneralSecurityException {
        Sheets service = getSheetsService(serviceAccountKeyPath);
        ValueRange response = service.spreadsheets().values()
                .get(spreadsheetId, range)
                .execute();
        List<List<Object>> values = response.getValues();
        if (values == null || values.isEmpty()) {
            return Collections.emptyList();
        }

        // First row as headers
        List<String> headers = new ArrayList<String>();
        for (Object headerCell : values.get(0)) {
            headers.add(headerCell.toString().trim());
        }

        List<Map<String, String>> rows = new ArrayList<Map<String, String>>();
        for (int i = 1; i < values.size(); i++) {
            List<Object> rowValues = values.get(i);
            Map<String, String> rowMap = new HashMap<String, String>();
            for (int j = 0; j < headers.size(); j++) {
                String cellValue = j < rowValues.size() ? rowValues.get(j).toString().trim() : "";
                rowMap.put(headers.get(j), cellValue);
            }
            rows.add(rowMap);
        }

        return rows;
    }
}
