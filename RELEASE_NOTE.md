# 📦 External Data Sheet – Release Notes

## Version: 1.0.0  
📅 Release Date: 2025-05-18

---

## 🚀 Features

- ✅ **Spreadsheet Reader Module**  
  Core functionality to read `.xlsx` Excel files and return rows as `List<Map<String, String>>`.

- ✅ **Header Mapping**  
  Automatically maps the first row of the sheet as headers to dictionary keys.

- ✅ **Data Type Handling**  
  Supports string, numeric, boolean, and formula cells.

- ✅ **Safe Defaults**  
  Returns empty string for blank/null cells to ensure stability in test cases.

- ✅ **Reusable Across Projects**  
  Designed to be imported in Katalon Studio without needing custom keywords. Just add the JAR to `Drivers/` and import the class.

---

## 📂 Packaging

- JAR Name: `external-data-sheet-1.0.0.jar`
- Dependencies managed via Gradle.
- Built with:  
  - Groovy  
  - Apache POI (`poi-ooxml`)

---

## 🧪 Compatibility

- ✅ Compatible with **Katalon Studio 8.6.0+**
- ✅ Java 8 or later
- ✅ Groovy 2.5+

---

## 📚 Usage Example

```groovy
import com.katalon.externaldata.ExcelReader

def data = ExcelReader.readExcel("Data/Example.xlsx", "Sheet1")
println data
