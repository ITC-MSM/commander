package com.wingedsheep.mtg.sets

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.isDirectory
import kotlin.io.path.name

/**
 * Where card definitions live *on disk*, for the corpus-wide tests that read source text rather
 * than loaded classes (facade boundary, Patterns index, catalog discovery).
 *
 * The definitions used to be one directory inside this module. They are now spread across
 * `:mtg-sets-core` (setless cards) and one `:mtg-sets-<era>` module per fixed release-year range,
 * so that no single Kotlin compilation has to hold the whole corpus. These tests still run from
 * `:mtg-sets` — the aggregator, the only module that sees every set — so they reach the sources by
 * walking up to the repo root and globbing the card modules. Adding an era module is picked up
 * automatically; nothing here needs to know the era names.
 */
object SetSourceRoots {

    private const val DEFINITIONS_SUFFIX = "src/main/kotlin/com/wingedsheep/mtg/sets/definitions"

    /** The repository root — tests run with `:mtg-sets` as their working directory. */
    val repoRoot: Path = Paths.get("").toAbsolutePath().parent

    /** Every card module's `definitions/` directory. */
    val definitionsDirs: List<Path> by lazy {
        Files.list(repoRoot).use { stream ->
            stream
                .filter { it.name.startsWith("mtg-sets") }
                .map { it.resolve(DEFINITIONS_SUFFIX) }
                .filter { it.isDirectory() }
                .toList()
                .sortedBy { it.toString() }
        }.also {
            check(it.isNotEmpty()) { "No card definition modules found under $repoRoot" }
        }
    }

    /** Every `.kt` file under every card module's `definitions/`. */
    fun definitionFiles(): List<Path> =
        definitionsDirs.flatMap { dir ->
            Files.walk(dir).use { stream -> stream.filter { it.name.endsWith(".kt") }.toList() }
        }

    /** A repo-relative path, so failure messages stay readable across modules. */
    fun relativize(path: Path): Path = repoRoot.relativize(path)
}
