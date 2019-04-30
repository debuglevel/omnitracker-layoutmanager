OMNITRACKER stores layouts (templates used for Crystal Reports and Word
Mail Merge) in the `layouts` database table (Base64 encoded).
Unfortunately, there seems to be no option to update a file (without
temporarily creating a new layout) or to export all existing files in
the OMNITRACKER client.

# Usage
```
$ java -jar .\build\libs\omnitrackerlayoutmanager-0.0.1-SNAPSHOT-all.jar
Usage: omnitrackerlayoutmanager [OPTIONS] COMMAND [ARGS]...

Options:
  -h, --help  Show this message and exit

Commands:
  list    List all layouts
  export  Export layout
  import  Import layout
```

An `configuration.properties` file must exist in the working directory
(or an environment variable `DATABASE_CONNECTION_STRING` is set) to
determine the database:
```
database.connection.string=jdbc:ucanaccess://OT-Templates.mdb
#database.connection.string=jdbc:sqlserver://myhost\\MYINSTANCE;databaseName=mydatabase;user=myuser;password=mypassword
```
Microsoft Access (via `ucanaccess` JDBC driver) and MSSQL (official
Microsoft JDBC driver) are supported.

## List all layouts
```
$ java -jar .\build\libs\omnitrackerlayoutmanager-0.0.1-SNAPSHOT-all.jar list
127	| \(Alle Ordner)\01. ITSM - Change Management\02. RFCs\RFCs - Übersichtsliste
128	| \(Alle Ordner)\01. ITSM - Service Operation\02. Incident Management\Incidents - Overview
172	| \(Alle Ordner)\01. ITSM - Service Operation\02. Incident Management\Event-Statistik - Störungen - Verlauf letzte X Tage (Linien)
176	| \(Alle Ordner)\01. ITSM - Service Operation\Nach Verantwortlichkeit (Torten)
181	| \(Alle Ordner)\01. ITSM - Service Operation\02. Incident Management\Snapshot-Statistik - Nach Kategorie - Letzte 30 Tage (Torten)_CRv85.rpt
209	| \(Alle Ordner)\Deletion Log\Standard 2D statistics (Table)
210	| \(Alle Ordner)\Deletion Log\Standard 2D Statistics (Pie)
211	| \(Alle Ordner)\Deletion Log\Standard 2D Statistics (Bar)
212	| \(Alle Ordner)\Deletion Log\Standard 2D Statistics (Stocking Bar)
213	| \(Alle Ordner)\Deletion Log\Standard throughput statistics
```

## Export layout
```
$ java -jar .\build\libs\omnitrackerlayoutmanager-0.0.1-SNAPSHOT-all.jar export 127 file.data
```

## Export all layouts (using PowerShell)
```
PS> .\bin\omnitrackerlayoutmanager.bat list | ForEach-Object { New-Object PSObject -Property @{ 'id' = $_.Split("`t")[0]; 'extension' = $_.Split("|")[2].Trim() } } | ForEach-Object { .\bin\omnitrackerlayoutmanager.bat export $_.id "export_$($_.id).$($_.extension)" }
```

## Import layout
```
$ java -jar .\build\libs\omnitrackerlayoutmanager-0.0.1-SNAPSHOT-all.jar import 127 file.data
```
Two backup files will be created (`backup_id-6616_2018-12-24.*`) which
contain the binary content of the replaced layout (as if exported via
this tool) and the Base64 encoded content (as stored in the database).

# Security
For MSSQL, you should create a special user which can only access the
`Layout` table (read for `export`, write for `import`). For `list`,
also read-only access to `ProblemArea` and `StringTranslations` is needed.

