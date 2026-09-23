package com.wingedsheep.sdk.core

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.ints.shouldBeGreaterThan
import java.io.File

/**
 * `Keyword` is mirrored by hand in `web-client/src/types/enums.ts`, the same way `CounterType` is
 * (see [CounterTypeClientMirrorTest]). Nothing enforced the match, and when this test was written
 * 28 keywords were missing from the client — crew, saddle, suspend, vanishing, devour and others.
 * The client then had no name for a keyword the server sent.
 *
 * `KeywordDisplayNames` is a `Record<Keyword, string>`, so TypeScript already rejects a mirrored
 * keyword that has no label. This test covers the other half: a keyword added to `Keyword.kt` and
 * never mirrored.
 */
class KeywordClientMirrorTest : DescribeSpec({

    describe("the web client's Keyword mirror") {

        val source = repoFile("web-client/src/types/enums.ts").readText()
        val clientKeywords = parseTsEnum(source, "Keyword")

        it("parses a plausible enum out of enums.ts") {
            // Guards the parser: a reshape of enums.ts must fail here, not pass by comparing an
            // empty list against the engine's.
            clientKeywords.size shouldBeGreaterThan 50
            clientKeywords shouldContain "FLYING"
        }

        it("declares every engine keyword") {
            Keyword.entries.map { it.name }.filterNot { it in clientKeywords }.shouldBeEmpty()
        }

        it("declares no keyword the engine does not have") {
            val engineKeywords = Keyword.entries.map { it.name }.toSet()
            clientKeywords.filterNot { it in engineKeywords }.shouldBeEmpty()
        }

        it("gives every keyword a display name") {
            val labelled = Regex("""\[Keyword\.([A-Z][A-Z0-9_]*)]\s*:""")
                .findAll(source.substringAfter("KeywordDisplayNames"))
                .map { it.groupValues[1] }
                .toSet()
            clientKeywords.filterNot { it in labelled }.shouldBeEmpty()
        }
    }
}) {
    companion object {
        /**
         * Resolve [relative] against the repository root, found by walking up from the working
         * directory (Gradle runs tests from the module dir, not the root).
         */
        private fun repoFile(relative: String): File {
            var dir: File? = File(System.getProperty("user.dir")).absoluteFile
            while (dir != null) {
                val candidate = File(dir, relative)
                if (candidate.isFile) return candidate
                dir = dir.parentFile
            }
            error("Could not find $relative above ${System.getProperty("user.dir")}")
        }

        /** The constant names declared in `export enum <name> { … }`, ignoring comments. */
        private fun parseTsEnum(source: String, name: String): List<String> {
            val body = source.substringAfter("export enum $name {").substringBefore("\n}")
            val withoutComments = body
                .replace(Regex("""/\*[\s\S]*?\*/"""), "")
                .replace(Regex("""//.*$""", RegexOption.MULTILINE), "")
            return Regex("""^\s*([A-Z][A-Z0-9_]*)\s*=""", RegexOption.MULTILINE)
                .findAll(withoutComments)
                .map { it.groupValues[1] }
                .toList()
        }
    }
}
