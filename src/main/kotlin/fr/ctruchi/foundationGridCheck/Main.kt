package fr.ctruchi.foundationGridCheck

import mu.KotlinLogging
import net.htmlparser.jericho.Element
import net.htmlparser.jericho.Source
import java.nio.charset.StandardCharsets
import java.nio.file.Paths
import kotlin.io.path.readLines

private val logger = KotlinLogging.logger {}

fun main(args: Array<String>) {
    val errors = mutableListOf<FileGridError>()

    val folder = Paths.get(args[0])

    logger.info { "Starting check on $folder" }

    folder.toFile().walk()
        .filter { it.path.endsWith(".html") }
        .forEach { file ->
            errors += checkHtmlFile(file.absolutePath)
                .map {
                    FileGridError(
                        file = file.toRelativeString(folder.toFile()),
                        error = it
                    )
                }
        }

    logger.info { "Found ${errors.size} errors" }

    errors.forEach {
        with(it) {
            println("$file: ${error.error} (${error.debugInfo})")
        }
    }
}

private fun checkHtmlFile(htmlFile: String): List<GridError> {
    val errors = mutableListOf<GridError>()

    val tsFile = htmlFile.replace(".html", ".ts")
    val tsLines = Paths.get(tsFile).readLines(StandardCharsets.UTF_8)
    val (parentGridClass, tsErrors) = extractGridClassAndCheck(tsLines)

    errors += tsErrors

    val source = Source(Paths.get(htmlFile).toFile())
    source.children().forEach {
        errors += checkElement(it, parentGridClass)
    }

    return errors
}

private fun extractGridClassAndCheck(tsLines: List<String>): Pair<String?, List<GridError>> {
    val hostBindingIndex = tsLines.indexOfFirst { it.contains("@HostBinding") && it.contains("class") }
    if (hostBindingIndex == -1) {
        return null to listOf()
    }

    val hostBindingLine = tsLines.get(hostBindingIndex)

    var classLine: String
    if (hostBindingLine.contains("=")) {
        classLine = hostBindingLine
    } else {
        classLine = tsLines.get(hostBindingIndex + 1)
    }

    var errors: List<GridError> = emptyList()
    if (hostBindingLine.contains("")) {
        errors = listOf(GridError("Component should not have cell class", "Typescript file"))
    }

    return classLine.substringAfter('\'')
        .replace("'", "")
        .split(" ")
        .firstOrNull { it == "grid-x" || it == "grid-y" } to errors
}

fun checkElement(
    element: Element,
    parentGridClass: String?
): List<GridError> {
    val classes = element.getAttributeValue("class")?.split(" ").orEmpty()
    val gridClass = classes.firstOrNull { it == "grid-x" || it == "grid-y" }

    val errors = mutableListOf<GridError>()

    if (parentGridClass != null && !classes.contains("cell")) {
        errors += GridError("Missing cell class", element.debugInfo)
    }

    if (parentGridClass == null && classes.contains("cell")) {
        errors += GridError("Useless cell class", element.debugInfo)
    }

    element.children().forEach {
        errors += checkElement(it, gridClass)
    }

    return errors
}

fun Source.children() = childElements.filter { it.name != "router-outlet" && it.name != "ng-container" }
fun Element.children() = childElements.filter { it.name != "router-outlet" && it.name != "ng-container" }