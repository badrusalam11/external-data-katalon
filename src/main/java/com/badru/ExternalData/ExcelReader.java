package com.badru.externaldata;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class ExcelReader {

    /**
     * Reads an Excel sheet into a List of Maps keyed by the header row.
     * Supports both .xlsx and .xls files.
     *
     * @param filePath  absolute path or relative to working dir
     * @param sheetName the sheet to read
     * @return List of row-maps, headers→cell-values
     * @throws IOException              if file not found or IO error
     * @throws IllegalArgumentException if sheet missing or unsupported format
     */
    public static List<Map<String, String>> readExcel(String filePath, String sheetName) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("Excel file not found: " + file.getAbsolutePath());
        }

        try (InputStream fis = new FileInputStream(file);
             Workbook workbook = createWorkbook(fis, file.getName())) {

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new IllegalArgumentException("Sheet '" + sheetName + "' not found in " + file.getName());
            }

            DataFormatter formatter = new DataFormatter();
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

            // Read header row
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                return new ArrayList<>();
            }
            List<String> headers = new ArrayList<>();
            for (Cell cell : headerRow) {
                headers.add(formatter.formatCellValue(cell, evaluator).trim());
            }

            // Read data rows
            List<Map<String, String>> dataList = new ArrayList<>();
            int lastRowNum = sheet.getLastRowNum();
            for (int i = 1; i <= lastRowNum; i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue; // skip empty rows
                Map<String, String> rowData = new HashMap<>();
                for (int j = 0; j < headers.size(); j++) {
                    Cell cell = row.getCell(j, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    String value = formatter.formatCellValue(cell, evaluator).trim();
                    rowData.put(headers.get(j), value);
                }
                dataList.add(rowData);
            }

            return dataList;
        }
    }

    /**
     * Updates specific columns in an existing Excel row
     *
     * @param filePath  absolute path or relative to working dir
     * @param sheetName the sheet to update
     * @param rowIndex  the index of row to update (0-based)
     * @param data      Map of column header → new value
     * @throws IOException              if file not found or IO error
     * @throws IllegalArgumentException if sheet, row, or headers missing
     */
    public static void updateExcelRow(String filePath, String sheetName, int rowIndex, Map<String, String> data) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("Excel file not found: " + file.getAbsolutePath());
        }

        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = createWorkbook(fis, file.getName())) {

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new IllegalArgumentException("Sheet '" + sheetName + "' not found in " + file.getName());
            }

            // Get headers and their indices
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new IllegalArgumentException("Header row not found");
            }

            Map<String, Integer> headerIndexMap = new HashMap<>();
            for (Cell cell : headerRow) {
                headerIndexMap.put(cell.getStringCellValue(), cell.getColumnIndex());
            }

            // Validate headers
            List<String> missing = new ArrayList<>();
            for (String key : data.keySet()) {
                if (!headerIndexMap.containsKey(key)) missing.add(key);
            }
            if (!missing.isEmpty()) {
                throw new IllegalArgumentException("Column(s) not found: " + missing);
            }

            Row row = sheet.getRow(rowIndex);
            if (row == null) {
                throw new IllegalArgumentException("Row " + rowIndex + " not found");
            }

            // Update cells
            for (Map.Entry<String, String> entry : data.entrySet()) {
                int colIndex = headerIndexMap.get(entry.getKey());
                Cell cell = row.getCell(colIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                cell.setCellValue(entry.getValue());
            }

            // Write back
            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
            }
        }
    }

    /**
     * Inserts a new row at the end of Excel sheet with specified fields
     *
     * @param filePath  absolute path or relative to working dir
     * @param sheetName the sheet name
     * @param data      Map of column header → value
     * @throws IOException              if file not found or IO error
     * @throws IllegalArgumentException if sheet or headers missing
     */
    public static void insertExcelRow(String filePath, String sheetName, Map<String, String> data) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("Excel file not found: " + file.getAbsolutePath());
        }

        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = createWorkbook(fis, file.getName())) {

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new IllegalArgumentException("Sheet '" + sheetName + "' not found in " + file.getName());
            }

            // Headers
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new IllegalArgumentException("Header row not found");
            }
            Map<String, Integer> headerIndexMap = new HashMap<>();
            for (Cell cell : headerRow) {
                headerIndexMap.put(cell.getStringCellValue(), cell.getColumnIndex());
            }

            List<String> missing = new ArrayList<>();
            for (String key : data.keySet()) {
                if (!headerIndexMap.containsKey(key)) missing.add(key);
            }
            if (!missing.isEmpty()) {
                throw new IllegalArgumentException("Column(s) not found: " + missing);
            }

            int newRowIndex = sheet.getLastRowNum() + 1;
            Row newRow = sheet.createRow(newRowIndex);
            for (Map.Entry<String, String> entry : data.entrySet()) {
                int colIndex = headerIndexMap.get(entry.getKey());
                Cell cell = newRow.createCell(colIndex);
                cell.setCellValue(entry.getValue());
            }

            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
            }
        }
    }

    /**
     * Utility to create the correct Workbook based on file extension
     */
    private static Workbook createWorkbook(InputStream fis, String fileName) throws IOException {
        String lower = fileName.toLowerCase();
        if (lower.endsWith(".xlsx")) {
            return new XSSFWorkbook(fis);
        } else if (lower.endsWith(".xls")) {
            return new HSSFWorkbook(fis);
        } else {
            throw new IllegalArgumentException("Unsupported Excel format: " + fileName);
        }
    }
}
