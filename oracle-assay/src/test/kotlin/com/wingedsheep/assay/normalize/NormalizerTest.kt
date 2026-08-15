package com.wingedsheep.assay.normalize

import com.wingedsheep.assay.corpus.OracleFace
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

/**
 * Normalization is held to the same standard as the grammar: every pass ships with its inverse.
 * If a pass throws information away, the touchstone stops being a proof and becomes a formality,
 * so each test here asserts the *inverse* as well as the forward direction.
 */
class NormalizerTest : StringSpec({

    fun face(name: String, text: String, typeLine: String = "Creature — Human") =
        OracleFace(name = name, oracleText = text, typeLine = typeLine)

    "reminder text is stripped for parsing and restored exactly" {
        val f = face("Storm Crow", "Flying (This creature can't be blocked except by creatures with flying or reach.)")
        val n = Normalizer.normalize(f)

        n.lines shouldBe listOf("Flying")
        n.restore(n.lines) shouldBe f.oracleText
    }

    "a reminder occupying its own line leaves the line count alone" {
        val f = face("Whatever", "Flying\n(A reminder on its own line.)")
        val n = Normalizer.normalize(f)

        n.lines shouldBe listOf("Flying", "")
        n.restore(n.lines) shouldBe f.oracleText
    }

    "the card's own name becomes ~ and comes back" {
        val f = face("Shivan Dragon", "{R}: Shivan Dragon gets +1/+0 until end of turn.")
        val n = Normalizer.normalize(f)

        n.lines shouldBe listOf("{R}: ~ gets +1/+0 until end of turn.")
        n.restore(n.lines) shouldBe f.oracleText
    }

    "the short name of a legendary card is abstracted too, longest match first" {
        val f = face(
            "Kenrith, the Returned King",
            "Kenrith, the Returned King is legendary. Kenrith enters tapped.",
        )
        val n = Normalizer.normalize(f)

        n.lines shouldBe listOf("~ is legendary. ~ enters tapped.")
        n.selfReferences shouldBe listOf("Kenrith, the Returned King", "Kenrith")
        // The inverse restores each occurrence's own surface form, not one canonical spelling.
        n.restore(n.lines) shouldBe f.oracleText
    }

    "a name occurring inside a longer word is left alone" {
        val f = face("Bear", "Bears you control get +1/+1. Bear attacks each combat if able.")
        val n = Normalizer.normalize(f)

        n.lines shouldBe listOf("Bears you control get +1/+1. ~ attacks each combat if able.")
        n.restore(n.lines) shouldBe f.oracleText
    }

    "a vanilla face normalizes to nothing and restores to nothing" {
        val f = face("Grizzly Bears", "")
        val n = Normalizer.normalize(f)

        n.isVanilla shouldBe true
        n.restore(n.lines) shouldBe ""
    }

    "multi-line text keeps its line structure through the round trip" {
        val f = face("Serra Angel", "Flying\nVigilance (Attacking doesn't cause this creature to tap.)")
        val n = Normalizer.normalize(f)

        n.lines shouldBe listOf("Flying", "Vigilance")
        n.restore(n.lines) shouldBe f.oracleText
    }

    "reminder text mentioning the card name survives, because it is removed before abstraction" {
        val f = face("Ashnod's Altar", "Ashnod's Altar taps. (Ashnod's Altar is an artifact.)")
        val n = Normalizer.normalize(f)

        n.lines shouldBe listOf("~ taps.")
        n.restore(n.lines) shouldBe f.oracleText
    }

    "the self-reference noun follows the printed type line" {
        Reminders.selfNoun("Creature — Angel") shouldBe "this creature"
        Reminders.selfNoun("Artifact") shouldBe "this artifact"
        Reminders.selfNoun("Instant") shouldBe "this spell"
        Reminders.selfNoun("Land") shouldBe "this land"
    }
})
