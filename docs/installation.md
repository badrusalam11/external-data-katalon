# Installation Guide

## Prerequisites

- **Katalon Studio**: Version 8.0.0 or later
- **Java**: Version 8 or higher

## Installation Methods

### Option 1: Download JAR (Recommended)

This is the easiest and fastest way to get started.

1. **Download the JAR file**:
   - Go to [Releases](https://github.com/badrusalam11/external-data-katalon/releases/)
   - Download the latest `external-data-katalon-x.x.x.jar` file

2. **Add to Katalon Project**:
   - Copy the downloaded JAR file to your Katalon project's `Drivers/` folder
   - Right-click on your project in Katalon Studio and select "Refresh"

3. **Verify Installation**:
   ```groovy
   import com.katalon.externaldata.ExcelReader
   println "Library loaded successfully!"
   ```

### Option 2: Build from Source

Use this method if you want to customize the library or contribute to development.

1. **Clone Repository**:
   ```bash
   git clone https://github.com/badrusalam11/external-data-katalon.git
   cd external-data-katalon
   ```

2. **Build the JAR**:
   ```bash
   # Linux/macOS
   ./gradlew clean build
   
   # Windows
   gradlew.bat clean build
   ```

3. **Copy to Katalon**:
   - Find the generated JAR at `build/libs/external-data-katalon.jar`
   - Copy it to your Katalon project's `Drivers/` folder
   - Refresh your Katalon project

## Project Structure After Installation

```
YourKatalonProject/
├── Drivers/
│   └── external-data-katalon-x.x.x.jar  ← Library here
├── Test Cases/
├── Data Files/
└── ...
```

## Verification

Create a simple test script to verify installation:

```groovy
import com.katalon.externaldata.ExcelReader

try {
    println "✓ External Data Katalon library loaded successfully!"
    println "✓ ExcelReader class available: " + ExcelReader.class.getName()
} catch (Exception e) {
    println "✗ Installation failed: " + e.getMessage()
}
```

## Troubleshooting

**Library not found error**:
- Ensure JAR is in the `Drivers/` folder
- Refresh the Katalon project
- Restart Katalon Studio if needed

**Build failures** (Option 2):
- Check Java version: `java -version`
- Ensure you have Java 8 or higher
- Make gradlew executable: `chmod +x gradlew` (Linux/macOS)

## Version History

- **v1.1.1**: Performance improvements, reduced JAR size
- **v1.1.0**: Added `updateExcelRow()` and `insertExcelRow()` methods
- **v1.0.0**: Initial release with `readExcel()` functionality