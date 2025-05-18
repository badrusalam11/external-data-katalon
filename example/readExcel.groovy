import com.badru.externaldata.ExcelReader

List<Map<String, String>> data = ExcelReader.readExcel("excel/MakeAppointment.xlsx", "Sheet1")
println(data)
