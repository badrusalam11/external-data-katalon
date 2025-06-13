# Examples

## Basic Examples

### Simple Data Reading

**Excel File** (`user-data.xlsx`):
| username | password | role |
|----------|----------|------|
| admin | admin123 | administrator |
| user1 | pass123 | user |
| tester | test456 | tester |

**Katalon Test Case:**
```groovy
import com.badru.externaldata.ExcelReader

def userData = ExcelReader.readExcel("Data Files/user-data.xlsx", "Users")

userData.each { user ->
    println "Username: ${user.username}"
    println "Password: ${user.password}"
    println "Role: ${user.role}"
    println "---"
}
```

**Output:**
```
Username: admin
Password: admin123
Role: administrator
---
Username: user1
Password: pass123
Role: user
---
Username: tester
Password: test456
Role: tester
---
```

## Login Test Example

**Excel File** (`login-tests.xlsx`):
| username | password | expected_result | test_description |
|----------|----------|----------------|------------------|
| valid_user | correct_pass | success | Valid login test |
| invalid_user | wrong_pass | error | Invalid credentials |
| empty_user | | error | Empty username |

**Test Case:**
```groovy
import com.badru.externaldata.ExcelReader
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

def loginTests = ExcelReader.readExcel("Data Files/login-tests.xlsx", "TestCases")

loginTests.each { testCase ->
    println "Running: ${testCase.test_description}"
    
    // Open login page
    WebUI.openBrowser("")
    WebUI.navigateToUrl("https://example.com/login")
    
    // Enter credentials
    WebUI.setText(findTestObject('Page_Login/input_username'), testCase.username)
    WebUI.setText(findTestObject('Page_Login/input_password'), testCase.password)
    WebUI.click(findTestObject('Page_Login/btn_login'))
    
    // Verify result
    if (testCase.expected_result == "success") {
        WebUI.verifyElementPresent(findTestObject('Page_Dashboard/welcome_message'), 10)
    } else {
        WebUI.verifyElementPresent(findTestObject('Page_Login/error_message'), 10)
    }
    
    WebUI.closeBrowser()
    println "✓ Test completed: ${testCase.test_description}"
}
```

## E-commerce Product Test

**Excel File** (`products.xlsx`):
| product_name | price | category | in_stock |
|-------------|-------|----------|----------|
| Laptop | 999.99 | Electronics | true |
| T-Shirt | 29.99 | Clothing | true |
| Book | 19.99 | Books | false |

**Test Case:**
```groovy
import com.badru.externaldata.ExcelReader
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

def products = ExcelReader.readExcel("Data Files/products.xlsx", "Products")

products.each { product ->
    println "Testing product: ${product.product_name}"
    
    // Search for product
    WebUI.setText(findTestObject('search_field'), product.product_name)
    WebUI.click(findTestObject('search_button'))
    
    // Verify product details
    WebUI.verifyTextPresent(product.product_name, false)
    WebUI.verifyTextPresent(product.price, false)
    WebUI.verifyTextPresent(product.category, false)
    
    // Check stock status
    if (product.in_stock == "true") {
        WebUI.verifyElementPresent(findTestObject('add_to_cart_button'), 10)
    } else {
        WebUI.verifyElementPresent(findTestObject('out_of_stock_message'), 10)
    }
}
```

## Form Validation Test

**Excel File** (`form-validation.xlsx`):
| name | email | phone | expected_error |
|------|-------|-------|----------------|
| John Doe | john@example.com | 1234567890 | none |
| | john@example.com | 1234567890 | Name is required |
| John Doe | invalid-email | 1234567890 | Invalid email format |
| John Doe | john@example.com | 123 | Invalid phone number |

**Test Case:**
```groovy
import com.badru.externaldata.ExcelReader
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

def formTests = ExcelReader.readExcel("Data Files/form-validation.xlsx", "ValidationTests")

formTests.each { testData ->
    println "Testing form with: ${testData.name ?: 'empty name'}"
    
    // Fill form
    WebUI.setText(findTestObject('name_field'), testData.name)
    WebUI.setText(findTestObject('email_field'), testData.email)
    WebUI.setText(findTestObject('phone_field'), testData.phone)
    WebUI.click(findTestObject('submit_button'))
    
    // Verify expected result
    if (testData.expected_error == "none") {
        WebUI.verifyElementPresent(findTestObject('success_message'), 10)
    } else {
        WebUI.verifyTextPresent(testData.expected_error, false)
    }
    
    // Clear form for next test
    WebUI.clearText(findTestObject('name_field'))
    WebUI.clearText(findTestObject('email_field'))
    WebUI.clearText(findTestObject('phone_field'))
}
```

## API Testing Example

**Excel File** (`api-tests.xlsx`):
| endpoint | method | payload | expected_status |
|----------|--------|---------|----------------|
| /users | GET | | 200 |
| /users | POST | {"name":"John","email":"john@test.com"} | 201 |
| /users/999 | GET | | 404 |

**Test Case:**
```groovy
import com.badru.externaldata.ExcelReader
import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS

def apiTests = ExcelReader.readExcel("Data Files/api-tests.xlsx", "APITests")

apiTests.each { test ->
    println "Testing API: ${test.method} ${test.endpoint}"
    
    RequestObject request = findTestObject('API_Requests/generic_request')
    request.setRestUrl("https://api.example.com${test.endpoint}")
    request.setRestRequestMethod(test.method)
    
    if (test.payload && !test.payload.isEmpty()) {
        request.setBodyContent(test.payload)
    }
    
    def response = WS.sendRequest(request)
    
    // Verify status code
    WS.verifyResponseStatusCode(response, Integer.parseInt(test.expected_status))
    
    println "✓ API test passed: ${test.method} ${test.endpoint}"
}
```

## Data Update Example (v1.1.0+)

**Original Excel File** (`users.xlsx`):
| username | status | last_login |
|----------|--------|------------|
| john | active | 2025-01-01 |
| jane | inactive | 2025-01-02 |

**Update Test:**
```groovy
import com.badru.externaldata.ExcelReader

// Read current data
def users = ExcelReader.readExcel("Data Files/users.xlsx", "Users")
println "Before update: ${users}"

// Update john's status
ExcelReader.updateExcelRow("Data Files/users.xlsx", "Users", 0, [
    status: "inactive",
    last_login: "2025-06-11"
])

// Add new user
ExcelReader.insertExcelRow("Data Files/users.xlsx", "Users", [
    username: "new_user",
    status: "active", 
    last_login: "2025-06-11"
])

// Read updated data
def updatedUsers = ExcelReader.readExcel("Data Files/users.xlsx", "Users")
println "After update: ${updatedUsers}"
```

## Multi-Sheet Example

**Excel File** (`test-suite.xlsx`) with multiple sheets:
- Sheet "LoginTests"
- Sheet "ProductTests" 
- Sheet "CheckoutTests"

**Test Case:**
```groovy
import com.badru.externaldata.ExcelReader

def testFile = "Data Files/test-suite.xlsx"

// Read different test types
def loginTests = ExcelReader.readExcel(testFile, "LoginTests")
def productTests = ExcelReader.readExcel(testFile, "ProductTests")
def checkoutTests = ExcelReader.readExcel(testFile, "CheckoutTests")

println "Login tests: ${loginTests.size()}"
println "Product tests: ${productTests.size()}"
println "Checkout tests: ${checkoutTests.size()}"

// Run all tests
[loginTests, productTests, checkoutTests].each { testGroup ->
    testGroup.each { testCase ->
        // Run individual test case
        println "Running: ${testCase}"
    }
}
```

## Error Handling Example

```groovy
import com.badru.externaldata.ExcelReader

def safeReadExcel(String filePath, String sheetName) {
    try {
        def data = ExcelReader.readExcel(filePath, sheetName)
        
        if (data.isEmpty()) {
            println "Warning: No data found in ${filePath}"
            return []
        }
        
        println "Successfully loaded ${data.size()} rows from ${sheetName}"
        return data
        
    } catch (FileNotFoundException e) {
        println "Error: File not found - ${filePath}"
        return []
    } catch (IllegalArgumentException e) {
        println "Error: Sheet '${sheetName}' not found in ${filePath}"
        return []
    } catch (Exception e) {
        println "Error reading Excel: ${e.getMessage()}"
        return []
    }
}

// Usage
def testData = safeReadExcel("Data Files/tests.xlsx", "TestCases")
if (!testData.isEmpty()) {
    // Proceed with tests
    testData.each { test ->
        // Run test
    }
}
```