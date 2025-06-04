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
}
