# External Data Katalon

A lightweight, reusable Groovy library for reading data from external Excel (.xlsx) files in Katalon Studio. This approach avoids conflicts caused by storing shared test data inside the Katalon project.

## Features

- **Excel File Support**: Read `.xlsx` Excel files as structured data (`List<Map<String, String>>`)
- **Lightweight**: Dependency-managed via Gradle and Apache POI
- **Katalon Integration**: Seamlessly integrates with Katalon Studio projects
- **Row Updates**: Modify existing rows in Excel files (v1.1.0+)
- **Row Insertion**: Add new rows to Excel files (v1.1.0+)

## Quick Start

### Prerequisites
- Java 8+
- Katalon Studio 8.0.0+

### Installation
Choose one of the following methods:

#### Option 1: Download JAR (Recommended)
1. Download the latest JAR from [Releases](https://github.com/badrusalam11/external-data-katalon/releases/)
2. Copy the JAR file to your Katalon project's `Drivers/` folder
3. Refresh your Katalon project

#### Option 2: Build from Source
1. Clone the repository: `git clone https://github.com/badrusalam11/external-data-katalon.git`
2. Build: `./gradlew clean build`
3. Copy `build/libs/external-data-katalon.jar` to your Katalon `Drivers/` folder

### Basic Usage

```groovy
import com.badru.externaldata.ExcelReader

def data = ExcelReader.readExcel("path/to/MakeAppointment.xlsx", "Sheet1")
println data
```

## Example

Given this Excel file:

| facility | check_list_apply | healthcare_program | visit_date | comment |
|----------|------------------|-------------------|------------|---------|
| Hongkong CURA Healthcare Center | true | Medicare | 29/04/2025 | this is comment |

The library returns:
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
```

## Documentation

- [Installation Guide](./installation.md)
- [Usage Guide](./usage.md)
- [API Reference](./api-reference.md)
- [Examples](./examples.md)

## License

MIT License - Copyright © 2025 Muhamad Badru Salam

## Author

[Muhamad Badru Salam](https://www.linkedin.com/in/muhamad-badru-salam-3bab2531b/)i