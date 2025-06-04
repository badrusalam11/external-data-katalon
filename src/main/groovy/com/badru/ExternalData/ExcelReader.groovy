package com.badru.externaldata

import java.io.File
import java.io.FileInputStream
import java.io.InputStream

import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.apache.poi.hssf.usermodel.HSSFWorkbook

class ExcelReader {

    /**
     * Reads an Excel sheet into a List of Maps keyed by the header row.
     * Supports both .xlsx and .xls files.
     *
     * @param filePath absolute path or relative to working dir
     * @param sheetName the sheet to read
     * @return List of row‑maps, headers→cell‑values
     * @throws FileNotFoundException if file not found
     * @throws IllegalArgumentException if sheet missing
     */
    static List<Map<String, String>> readExcel(String filePath, String sheetName) {
        File file = new File(filePath)
        if (!file.exists()) {
            throw new FileNotFoundException("Excel file not found: ${file.absolutePath}")
        }

        InputStream fis = new FileInputStream(file)
        Workbook workbook
        if (file.name.toLowerCase().endsWith('.xlsx')) {
            workbook = new XSSFWorkbook(fis)
        } else if (file.name.toLowerCase().endsWith('.xls')) {
            workbook = new HSSFWorkbook(fis)
        } else {
            fis.close()
            throw new IllegalArgumentException("Unsupported Excel format: ${file.name}")
        }

        Sheet sheet = workbook.getSheet(sheetName)
        if (sheet == null) {
            workbook.close()
            fis.close()
            throw new IllegalArgumentException("Sheet '${sheetName}' not found in ${file.name}")
        }

        DataFormatter formatter = new DataFormatter()
        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator()

        // Read header row
        Row headerRow = sheet.getRow(0)
        if (headerRow == null) {
            workbook.close(); fis.close()
            return []
        }
        List<String> headers = headerRow.cellIterator()
            .collect { Cell cell -> formatter.formatCellValue(cell, evaluator).trim() }

        // Read data rows
        List<Map<String, String>> dataList = []
        (1..sheet.lastRowNum).each { i ->
            Row row = sheet.getRow(i)
            if (row == null) return  // skip empty rows
            Map<String, String> rowData = [:]
            headers.eachWithIndex { String header, int j ->
                Cell cell = row.getCell(j)
                String value = formatter.formatCellValue(cell, evaluator).trim()
                rowData[header] = value
            }
            dataList << rowData
        }

        workbook.close()
        fis.close()
        return dataList
    }

    /**
     * Updates specific columns in an existing Excel row
     * 
     * @param filePath absolute path or relative to working dir
     * @param sheetName the sheet to update
     * @param rowIndex the index of row to update (0-based)
     * @param data Map of column header → new value (can be partial update)
     * @throws FileNotFoundException if file not found
     * @throws IllegalArgumentException if sheet or row missing
     */
    static void updateExcelRow(String filePath, String sheetName, int rowIndex, Map<String, String> data) {
        File file = new File(filePath)
        if (!file.exists()) {
            throw new FileNotFoundException("Excel file not found: ${file.absolutePath}")
        }

        FileInputStream fis = new FileInputStream(file)
        Workbook workbook
        if (file.name.toLowerCase().endsWith('.xlsx')) {
            workbook = new XSSFWorkbook(fis)
        } else if (file.name.toLowerCase().endsWith('.xls')) {
            workbook = new HSSFWorkbook(fis)
        } else {
            fis.close()
            throw new IllegalArgumentException("Unsupported Excel format: ${file.name}")
        }

        try {
            Sheet sheet = workbook.getSheet(sheetName)
            if (sheet == null) {
                throw new IllegalArgumentException("Sheet '${sheetName}' not found in ${file.name}")
            }

            // Get headers and their indices
            Row headerRow = sheet.getRow(0)
            if (headerRow == null) {
                throw new IllegalArgumentException("Header row not found")
            }

            Map<String, Integer> headerIndexMap = [:]
            headerRow.cellIterator().each { Cell cell ->
                headerIndexMap[cell.stringCellValue] = cell.columnIndex
            }

            // Validate all headers exist
            def missingHeaders = data.keySet().findAll { !headerIndexMap.containsKey(it) }
            if (missingHeaders) {
                throw new IllegalArgumentException("Column(s) not found: ${missingHeaders.join(', ')}")
            }

            // Update only specified columns
            Row row = sheet.getRow(rowIndex)
            if (row == null) {
                throw new IllegalArgumentException("Row ${rowIndex} not found")
            }

            data.each { header, value ->
                int colIndex = headerIndexMap[header]
                Cell cell = row.getCell(colIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                cell.setCellValue(value)
            }

            // Write changes
            FileOutputStream fos = new FileOutputStream(file)
            workbook.write(fos)
            fos.close()
        } finally {
            workbook.close()
            fis.close()
        }
    }

    /**
     * Inserts a new row at the end of Excel sheet with specified fields
     * 
     * @param filePath absolute path or relative to working dir
     * @param sheetName the sheet name
     * @param data Map of column header → value (can be partial or full fields)
     * @throws FileNotFoundException if file not found
     * @throws IllegalArgumentException if sheet missing or invalid headers
     */
    static void insertExcelRow(String filePath, String sheetName, Map<String, String> data) {
        File file = new File(filePath)
        if (!file.exists()) {
            throw new FileNotFoundException("Excel file not found: ${file.absolutePath}")
        }

        FileInputStream fis = new FileInputStream(file)
        Workbook workbook
        if (file.name.toLowerCase().endsWith('.xlsx')) {
            workbook = new XSSFWorkbook(fis)
        } else if (file.name.toLowerCase().endsWith('.xls')) {
            workbook = new HSSFWorkbook(fis)
        } else {
            fis.close()
            throw new IllegalArgumentException("Unsupported Excel format: ${file.name}")
        }

        try {
            Sheet sheet = workbook.getSheet(sheetName)
            if (sheet == null) {
                throw new IllegalArgumentException("Sheet '${sheetName}' not found in ${file.name}")
            }

            // Get headers and their indices
            Row headerRow = sheet.getRow(0)
            if (headerRow == null) {
                throw new IllegalArgumentException("Header row not found")
            }

            Map<String, Integer> headerIndexMap = [:]
            headerRow.cellIterator().each { Cell cell ->
                headerIndexMap[cell.stringCellValue] = cell.columnIndex
            }

            // Validate headers exist
            def missingHeaders = data.keySet().findAll { !headerIndexMap.containsKey(it) }
            if (missingHeaders) {
                throw new IllegalArgumentException("Column(s) not found: ${missingHeaders.join(', ')}")
            }

            // Create new row at the end
            int newRowIndex = sheet.getLastRowNum() + 1
            Row newRow = sheet.createRow(newRowIndex)

            // Set values only for specified fields
            data.each { header, value ->
                int colIndex = headerIndexMap[header]
                Cell cell = newRow.createCell(colIndex)
                cell.setCellValue(value)
            }

            // Write changes
            FileOutputStream fos = new FileOutputStream(file)
            workbook.write(fos)
            fos.close()
        } finally {
            workbook.close()
            fis.close()
        }
    }
}
