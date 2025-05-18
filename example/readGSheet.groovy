import com.badru.externaldata.GoogleSheetReader

String keyPath     = "config/sa-key.json"
String spreadsheetId = "1-9rIx1cILw4WDUEO8vInJywWdDxatel9Vkluu6u3QNY"
String range         = "Sheet1!A1:E"

def rows = GoogleSheetReader.read(keyPath, spreadsheetId, range)
rows.each { println it }
