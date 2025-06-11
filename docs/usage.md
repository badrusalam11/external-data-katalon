# Usage Guide

## Basic Usage

### Reading Excel Data

```groovy
import com.katalon.externaldata.ExcelReader

// Read Excel file
def data = ExcelReader.readExcel("path/to/your-file.xlsx", "Sheet1")

// Print the data
data.each { row ->
    println "Data: ${row}"
}
```

### File Paths

```groovy
// Relative path from project root
def data = ExcelReader.readExcel("Data Files/test-data.xlsx", "TestData")

// Absolute path
def data = ExcelReader.readExcel("/full/path/to/test-data.xlsx", "Sheet1")
```

## Data Structure

The library returns `List<Map<String, String>>`:
- **List**: Each Excel row becomes a list item
- **Map**: Column headers become keys, cell values become values
- All values are returned as **String**

### Example

**Excel File:**
| username | password | role |
|----------|----------|------|
| john | pass123 | admin |
| jane | pass456 | user |

**Returned Data:**
```groovy
[
  [username: 'john', password: 'pass123', role: 'admin'],
  [username: 'jane', password: 'pass456', role: 'user']
]
```

## Common Patterns

### Data-Driven Testing

```groovy
import com.katalon.externaldata.ExcelReader
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

def loginData = ExcelReader.readExcel("login-credentials.xlsx", "TestData")

loginData.each { credentials ->
    WebUI.setText(findTestObject('username_field'), credentials.username)
    WebUI.setText(findTestObject('password_field'), credentials.password)
    WebUI.click(findTestObject('login_button'))
    
    // Add your test assertions here
    WebUI.verifyTextPresent(credentials.expected_result, false)
}
```

### Accessing Data

```groovy
def data = ExcelReader.readExcel("test.xlsx", "Sheet1")

// Get number of rows
println "Total rows: ${data.size()}"

// Access first row
def firstRow = data[0]
println "First user: ${firstRow.username}"

// Access by index and key
println "Second user email: ${data[1]['email']}"

// Check if column exists
if (data[0].containsKey('phone')) {
    println "Phone: ${data[0].phone}"
}
```

### Error Handling

```groovy
try {
    def data = ExcelReader.readExcel("test-data.xlsx", "Sheet1")
    
    if (data.isEmpty()) {
        println "No data found in Excel file"
        return
    }
    
    // Process data
    data.each { row ->
        // Your test logic
    }
    
} catch (FileNotFoundException e) {
    println "Excel file not found: ${e.getMessage()}"
} catch (Exception e) {
    println "Error reading Excel: ${e.getMessage()}"
}
```

## New Features (v1.1.0+)

### Updating Excel Rows

```groovy
import com.katalon.externaldata.ExcelReader

// Update existing row
ExcelReader.updateExcelRow("test-data.xlsx", "Sheet1", 1, [
    username: "updated_user",
    status: "active"
])
```

### Inserting New Rows

```groovy
import com.katalon.externaldata.ExcelReader

// Insert new row
ExcelReader.insertExcelRow("test-data.xlsx", "Sheet1", [
    username: "new_user",
    password: "new_pass",
    role: "tester"
])
```

## Best Practices

### File Organization
```
Data Files/
└── External/
    ├── login-data.xlsx
    ├── user-data.xlsx
    └── product-data.xlsx
```

### Column Naming
Use consistent, descriptive headers:
```
✓ Good: username, password, expected_result
✗ Avoid: Username, Pass Word, Expected Result
```

### Data Validation
```groovy
def data = ExcelReader.readExcel("test.xlsx", "Sheet1")

// Validate required columns
def requiredColumns = ['username', 'password']
def headers = data[0].keySet()

requiredColumns.each { column ->
    if (!headers.contains(column)) {
        throw new Exception("Missing required column: ${column}")
    }
}
```

## Integration Examples

### With Test Suites
```groovy
// Store in Global Variable
GlobalVariable.testData = ExcelReader.readExcel("suite-data.xlsx", "TestCases")

// Use in test cases
def currentTest = GlobalVariable.testData[GlobalVariable.testIndex]
```

### With Custom Keywords
```groovy
// In Keywords folder
class DataHelper {
    static def getTestData(String fileName) {
        return ExcelReader.readExcel("Data Files/External/${fileName}", "TestData")
    }
}

// Usage
def data = DataHelper.getTestData("login-tests.xlsx")
```