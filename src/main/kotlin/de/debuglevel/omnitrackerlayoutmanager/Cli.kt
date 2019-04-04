package de.debuglevel.omnitrackerlayoutmanager

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.output.TermUi.echo
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.file
import com.github.ajalt.clikt.parameters.types.int
import de.debuglevel.omnitrackerdatabasebinding.OmnitrackerDatabase
import de.debuglevel.omnitrackerdatabasebinding.models.Layout
import mu.KotlinLogging
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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
        importLayout(id, importFile)
    }

    companion object {
        fun importLayout(layoutId: Int, importFile: File) {
            logger.debug("Getting current layout from database...")
            val omnitrackerDatabase = OmnitrackerDatabase()
            val layout = omnitrackerDatabase.layouts[layoutId]

            if (layout == null) {
                echo("Given ID $layoutId does not exist.", err = true)
            } else {
                createBackup(layoutId, layout)

                logger.debug("Reading report data from file '${importFile.absolutePath}'...")
                val reportData = importFile.readBytes()

                logger.debug("Updating report data in database...")
                omnitrackerDatabase.updateLayoutReportData(layout, reportData)
            }
        }

        private fun createBackup(layoutId: Int, layout: Layout) {
            val backupDatetime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            val backupBaseFilename = "backup_id-${layoutId}_$backupDatetime"
            logger.debug("Creating backup to files '$backupBaseFilename.*'...")
            Export.exportLayout(layout, File("$backupBaseFilename.binary"), false)
            Export.exportLayout(layout, File("$backupBaseFilename.base64"), true)
        }
    }
}

class Export : CliktCommand(help = "Export layout") {
    private val id: Int by argument("layout-id", help = "The ID of the layout to export").int()
    private val exportFile: File by argument("export-file", help = "The file to export to").file(folderOkay = false)

    override fun run() {
        exportLayout(id, exportFile)
    }

    companion object {
        fun exportLayout(layoutId: Int, exportFile: File) {
            logger.debug("Getting layout from database...")
            val layout = OmnitrackerDatabase().layouts[layoutId]

            if (layout == null) {
                echo("Given ID $layoutId does not exist.", err = true)
            } else {
                exportLayout(layout, exportFile)
            }
        }

        fun exportLayout(layout: Layout, exportFile: File, base64: Boolean = false) {
            if (base64) {
                logger.debug("Writing Base64 encoded report data (e.g. original database content) to file '${exportFile.absolutePath}'...")
                exportFile.writeBytes(layout.reportDataBase64.toByteArray())
            } else {
                logger.debug("Writing binary report data to file '${exportFile.absolutePath}'...")
                exportFile.writeBytes(layout.reportData)
            }
        }
    }
}

class List : CliktCommand(help = "List all layouts") {
    override fun run() {
        logger.debug("Getting layouts from database...")
        OmnitrackerDatabase().layouts
            .values
            .sortedBy { "${it.folder?.path}\\\${it.name}" }
            .forEach { println("${it.id}\t| ${it.folder?.path}\\${it.name}") }
    }
}

fun main(args: Array<String>) = OmnitrackerLayoutManager()
    .subcommands(List(), Export(), Import())
    .main(args)