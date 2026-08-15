package com.wingedsheep.mtg.sets

import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlin.io.path.readText

/**
 * Enforces the single `Patterns` entry point (SDK architecture review §2.3 follow-up).
 *
 * The domain pattern objects (`LibraryPatterns`, `HandPatterns`, …) are reached through the one
 * curated [com.wingedsheep.sdk.dsl.Patterns] index — `Patterns.Library.scry(2)` — never the bare
 * objects directly. Keeping all call sites on the index is what makes the recipes discoverable
 * from a single root and lets the underlying objects move/split without touching cards.
 *
 * Comments and `import` lines are ignored — only real code is checked.
 */
class PatternsNamespaceTest : FunSpec({

    val forbidden = Regex(
        """\b(Library|Hand|Group|Exile|CreatureType|Mechanic)Patterns\."""
    )

    test("card definitions reach pattern recipes through the Patterns index, not the bare objects") {
        val violations = mutableListOf<String>()

        SetSourceRoots.definitionFiles().forEach { path ->
            stripCommentsAndImports(path.readText()).forEachIndexed { idx, line ->
                val match = forbidden.find(line) ?: return@forEachIndexed
                val domain = match.groupValues[1]
                val rel = SetSourceRoots.relativize(path)
                violations += "$rel:${idx + 1}  →  use `Patterns.$domain.` instead of `${match.value}`"
            }
        }

        withClue(
            "Card definitions must reach pattern recipes through the Patterns index (SDK review §2.3).\n" +
                violations.joinToString("\n")
        ) {
            violations shouldBe emptyList()
        }
    }
})
