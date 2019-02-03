OMNITRACKER stores layouts (templates used for Crystal Reports and Word Mail Merge) in the `layouts` database table. Unfortunately, there seems to be no option to update a file (without temporarily creating a new layout) or to export all existing files.

# Usage
```
Usage: omnitrackerlayoutmanager [OPTIONS] COMMAND [ARGS]...

Options:
  -h, --help  Show this message and exit

Commands:
  list    List all layouts
  export  Export layout
  import  Import layout
```

# List all layouts
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

# Export layout
```
$ java -jar .\build\libs\omnitrackerlayoutmanager-0.0.1-SNAPSHOT-all.jar export 127 file.data
```

# Export all layouts (using PowerShell)
```
PS> java -jar .\build\libs\omnitrackerlayoutmanager-0.0.1-SNAPSHOT-all.jar list | ForEach-Object { $_.Split("`t")[0] } | ForEach-Object { java -jar .\build\libs\omnitrackerlayoutmanager-0.0.1-SNAPSHOT-all.jar export $_ "export_$_.data" }
```