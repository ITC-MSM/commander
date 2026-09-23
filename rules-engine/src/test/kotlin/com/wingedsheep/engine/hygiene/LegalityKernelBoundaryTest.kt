package com.wingedsheep.engine.hygiene

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import java.io.File

/**
 * Guards the one legality kernel: [com.wingedsheep.engine.legality.LegalityKernel] is the only code
 * that decides whether an `ActivationRestriction` or a `CastRestriction` holds, and the only code
 * that decides whether a `GrantMayCastFromLinkedExile` lets a player cast an exiled card.
 *
 * Before it existed, the activation-restriction `when` was written four times (the activation
 * handler, a private copy in the mana solver, `CastPermissionUtils`, part of it again in the
 * activated-ability enumerator), the cast-restriction one twice, and the linked-exile permission
 * four times — the client view's copy reading base control and printed abilities only. What the
 * client was offered and what the server accepted agreed only by discipline.
 *
 * If this test fails, add the branch to the kernel and call it, rather than dispatching here.
 */
class LegalityKernelBoundaryTest : FunSpec({

    test("only the legality kernel dispatches on activation and cast restrictions") {
        // `is X.Y`, a bare `X.Y ->` branch (data objects need no `is`), and `== X.Y` / `!= X.Y`.
        val restriction = """(ActivationRestriction|CastRestriction)\.\w+"""
        val offenders = findUses(
            Regex("""\bis\s+$restriction|\b$restriction\s*->|[!=]=\s*(\w+\.)*$restriction""")
        ) { it != KERNEL }
        offenders.shouldBeEmpty()
    }

    test("only the legality kernel reads the linked-exile cast grant off a permanent") {
        val offenders = findUses(Regex("""filterIsInstance<\s*(com\.wingedsheep\.sdk\.scripting\.)?GrantMayCastFromLinkedExile\s*>|as\?\s*GrantMayCastFromLinkedExile\b""")) {
            it != KERNEL
        }
        offenders.shouldBeEmpty()
    }
}) {
    companion object {
        private const val KERNEL = "com/wingedsheep/engine/legality/LegalityKernel.kt"

        private fun sourceRoot(): File =
            listOf(File("src/main/kotlin"), File("rules-engine/src/main/kotlin"))
                .firstOrNull { it.isDirectory }
                ?: error("Could not locate rules-engine/src/main/kotlin from ${File(".").absolutePath}")

        private fun findUses(pattern: Regex, include: (String) -> Boolean): List<String> {
            val root = sourceRoot()
            val rootPath = root.absolutePath.replace('\\', '/')
            return root.walkTopDown()
                .filter { it.isFile && it.extension == "kt" }
                .flatMap { file ->
                    val relative = file.absolutePath.replace('\\', '/').removePrefix("$rootPath/")
                    if (!include(relative)) return@flatMap emptySequence()
                    file.readLines().withIndex()
                        .filter { (_, line) -> pattern.containsMatchIn(line) && !line.trimStart().startsWith("*") && !line.trimStart().startsWith("//") }
                        .map { (idx, line) -> "$relative:${idx + 1}: ${line.trim()}" }
                        .asSequence()
                }
                .toList()
        }
    }
}
