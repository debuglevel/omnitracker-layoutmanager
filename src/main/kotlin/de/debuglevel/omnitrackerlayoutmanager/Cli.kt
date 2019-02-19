package de.debuglevel.omnitrackerlayoutmanager

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.file
import com.github.ajalt.clikt.parameters.types.int
import de.debuglevel.omnitrackerdatabasebinding.OmnitrackerDatabase
import mu.KotlinLogging
import java.io.File

private val logger = KotlinLogging.logger {}

class OmnitrackerLayoutManager : CliktCommand() {
    override fun run() = Unit
}

class Import : CliktCommand(help = "Import layout") {
    private val id: Int by argument("layout-id", help = "The ID of the layout to update").int()
    private val importFile: File by argument("import-file", help = "The file to import").file(
        folderOkay = false,
        readable = true,
        exists = true
    )

    override fun run() {
        logger.debug("Getting current layout from database...")
        val omnitrackerDatabase = OmnitrackerDatabase()
        val layout = omnitrackerDatabase.layouts[id]

        if (layout == null) {
            echo("Given ID $id does not exist.", err = true)
        } else {
            logger.debug("Reading report data from file...")
            val reportData = importFile.readBytes()

            logger.debug("Updating report data in database...")
            omnitrackerDatabase.updateLayoutReportData(layout, reportData)
        }
    }
}

class Export : CliktCommand(help = "Export layout") {
    private val id: Int by argument("layout-id", help = "The ID of the layout to export").int()
    private val exportFile: File by argument("export-file", help = "The file to export to").file(folderOkay = false)

    override fun run() {
        val layout = OmnitrackerDatabase().layouts[id]

        if (layout == null) {
            echo("Given ID $id does not exist.", err = true)
        } else {
            exportFile.writeBytes(layout.reportData)
        }
    }
}

class List : CliktCommand(help = "List all layouts") {
    override fun run() {
        OmnitrackerDatabase().layouts
            .values
            .sortedBy { it.id }
            .forEach { println("${it.id}\t| ${it.folder?.path}\\${it.name}") }
    }
}

fun main(args: Array<String>) = OmnitrackerLayoutManager()
    .subcommands(List(), Export(), Import())
    .main(args)