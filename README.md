# ExternalDataKatalon

A lightweight, reusable Groovy library for reading data from external spreadsheet (Excel `.xlsx`) files in Katalon Studio. This approach avoids conflicts caused by storing shared test data inside the Katalon project.

## 📦 Features

- Read `.xlsx` Excel files as structured data (`List<Map<String, String>>`).
- Lightweight, dependency-managed via Gradle and Apache POI.
---

## 📁 Project Structure

external-data-katalon/
├── build.gradle
├── settings.gradle
├── src/
│ └── main/
│ └── groovy/
│ └── com/
│ └── katalon/
│ └── externaldata/
│ └── ExcelReader.groovy
└── Excel/
└── MakeAppointment.xlsx (optional example file)


---

## 🔧 How to Build

### Requirements

- Java JDK 8+
- Gradle (or use the included `gradlew`/`gradlew.bat`)

### Build the JAR

1. Open terminal or PowerShell in the project folder.

2. Run:

   ```bash
   ./gradlew clean build    # Linux/macOS

3. Output JAR file will be located at: build/libs/external-data-katalon.jar

### How to Use it in Katalon
1. Download the latest jar from [Release](https://github.com/badrusalam11/external-data-katalon/releases/) 
2. Copy external-data-katalon.jar to your Katalon project’s Drivers/ folder.
3. Import and call it from your test case:
    ```groovy
    import com.katalon.externaldata.ExcelReader
    def data = ExcelReader.readExcel("path/to/MakeAppointment.xlsx", "Sheet1")
    println data
4. Given the excel:
    | facility                    | check_list_apply   | healthcare_program  | visit_date  | comment         |
    | --------------------------- | ------------------ | ------------------- | ----------- | --------------- |
    | Hongkong CURA Healthcare... | true               | Medicare            | 29/04/2025  | this is comment |
    
    Calling the reader will return:
    ```groovy
    [
        [
            facility: 'Hongkong CURA Healthcare Center',
            check_list_apply: 'true',
            healthcare_program: 'Medicare',
            visit_date: '29/04/2025',
            comment: 'this is comment'
        ]
    ]

## 📖 Documentation
-  Read the complete documentation: [Documentation](https://badrusalam11.github.io/external-data-katalon/)

📚 License
MIT License

Copyright © 2025 Muhamad Badru Salam  
[LinkedIn Profile](https://www.linkedin.com/in/muhamad-badru-salam-3bab2531b/)