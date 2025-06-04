import com.badru.ExternalData.ExcelReader

def row = 1 // Start reading from the second row (index 1)
def updateData = [
    'FirstName': 'John',
    'LastName': 'Doe',
    'Email': 'john.doe@example.com'
]
ExcelReader.updateExcelRow('path/to/file.xlsx', 'Sheet1', id, updateData)