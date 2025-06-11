# API Reference

## ExcelReader Class

**Package**: `com.katalon.externaldata`

### Methods

#### readExcel(String filePath, String sheetName)

Reads data from an Excel (.xlsx) file.

**Parameters:**
- `filePath` (String): Path to the Excel file
- `sheetName` (String): Name of the sheet to read

**Returns:**
- `List<Map<String, String>>`: List of rows with column headers as keys

**Example:**
```groovy
def data = ExcelReader.readExcel("test.xlsx", "Sheet1")
```

#### updateExcelRow(String filePath, String sheetName, int rowIndex, Map<String, String> newData)

*Available in v1.1.0+*

Updates an existing row in the Excel file.

**Parameters:**
- `filePath` (String): Path to the Excel file
- `sheetName` (String): Name of the sheet
- `rowIndex` (int): Row index to update (0-based, excluding header)
- `newData` (Map): New data to update

**Example:**
```groovy
ExcelReader.updateExcelRow("test.xlsx", "Sheet1", 0, [username: "new_name"])
```

#### insertExcelRow(String filePath, String sheetName, Map<String, String> rowData)

*Available in v1.1.0+*

Inserts a new row into the Excel file.

**Parameters:**
- `filePath` (String): Path to the Excel file
- `sheetName` (String): Name of the sheet
- `rowData` (Map): Data for the new row

**Example:**
```groovy
ExcelReader.insertExcelRow("test.xlsx", "Sheet1", [
    username: "new_user",
    password: "pass123"
])
```

## Data Structure

### Input Requirements

**Supported Format:**
- `.xlsx` files only
- First row must contain headers
- Data starts from second row

**Excel Structure:**
```
Row 1: | username | password | email     |  ← Headers
Row 2: | john     | pass123  | john@.com |  ← Data
Row 3: | jane     | pass456  | jane@.com |  ← Data
```

### Output Format

**Return Type:** `List<Map<String, String>>`

```groovy
[
  [username: "john", password: "pass123", email: "john@.com"],
  [username: "jane", password: "pass456", email: "jane@.com"]
]
```

## Error Handling

### Common Exceptions

```groovy
try {
    def data = ExcelReader.readExcel("file.xlsx", "Sheet1")
} catch (FileNotFoundException e) {
    // File doesn't exist
} catch (IllegalArgumentException e) {
    // Sheet name not found
} catch (IOException e) {
    // File access error
} catch (Exception e) {
    // Other errors
}
```

## Compatibility

- **Katalon Studio**: 8.0.0+
- **Java**: 8+
- **File Format**: .xlsx only
- **Dependencies**: Apache POI (bundled)