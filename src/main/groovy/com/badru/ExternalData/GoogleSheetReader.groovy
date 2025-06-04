package com.badru.externaldata

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.json.JsonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.ServiceAccountCredentials

/**
 * GoogleSheetReader: reads data from Google Sheets via the Sheets API.
 * Requires a Service Account JSON key file.
 */
class GoogleSheetReader {
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance()

    /**
     * Initializes a Sheets service instance using a service account key.
     * @param serviceAccountKeyPath path to the JSON key file
     * @return Sheets service
     */
    private static Sheets getSheetsService(String serviceAccountKeyPath) {
        def credentialsStream = new FileInputStream(serviceAccountKeyPath)
        ServiceAccountCredentials credentials =
            ServiceAccountCredentials.fromStream(credentialsStream)
                .createScoped(SheetsScopes.SPREADSHEETS_READONLY)

        def httpTransport = GoogleNetHttpTransport.newTrustedTransport()
        return new Sheets.Builder(httpTransport, JSON_FACTORY, new HttpCredentialsAdapter(credentials))
            .setApplicationName("Katalon-GoogleSheetReader")
            .build()
    }

    /**
     * Reads a range of cells from a Google Sheets spreadsheet.
     * @param serviceAccountKeyPath JSON key file for the service account
     * @param spreadsheetId the ID in the sheet's URL
     * @param range e.g. "Sheet1!A1:D"
     * @return List of Maps (header->value)
     */
    static List<Map<String, String>> read(String serviceAccountKeyPath, String spreadsheetId, String range) {
        Sheets service = getSheetsService(serviceAccountKeyPath)
        def response = service.spreadsheets().values()
            .get(spreadsheetId, range)
            .execute()
        List<List<Object>> values = response.getValues()
        if (!values) {
            return []
        }

        // First row as headers
        List<String> headers = values.get(0).collect { it.toString().trim() }
        List<Map<String, String>> rows = []

        values.subList(1, values.size()).each { rowValues ->
            Map<String, String> rowMap = [:]
            headers.eachWithIndex { header, idx ->
                def v = idx < rowValues.size() ? rowValues.get(idx) : ''
                rowMap[header] = v.toString().trim()
            }
            rows << rowMap
        }

        return rows
    }
}

/*
 * build.gradle dependencies:
 * implementation 'com.google.apis:google-api-services-sheets:v4-rev20250504-2.0.0'
 * implementation 'com.google.auth:google-auth-library-oauth2-http:1.16.0'
 */
