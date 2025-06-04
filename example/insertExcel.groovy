import com.badru.ExternalData.ExcelReader

// Insert row with all fields
def data = [
    'FirstName': 'Jane',
    'LastName': 'Smith',
    'Email': 'jane.smith@example.com'
]
ExcelReader.insertExcelRow('path/to/file.xlsx', 'Sheet1', data)