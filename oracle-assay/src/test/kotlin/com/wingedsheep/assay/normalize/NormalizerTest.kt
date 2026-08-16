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

    // An Equipment and an Aura print two words for one value: the static's affected set is
    // "whatever this is attached to", which says nothing about auras. The word is a function of the
    // type line, so it is normalization's, and the aura spelling is canonical because that is the
    // rule the grammar has.
    "the equipment attachment noun abstracts onto the aura's and comes back" {
        val f = face("Bonesplitter", "Equipped creature gets +2/+0.\nEquip {1}", "Artifact — Equipment")
        val n = Normalizer.normalize(f)

        n.lines shouldBe listOf("Enchanted creature gets +2/+0.", "Equip {1}")
        n.attachmentNouns shouldBe listOf("Equipped")
        n.restore(n.lines) shouldBe f.oracleText
    }

    "an aura's own spelling is recorded too, so restore is positional either way" {
        val f = face("Holy Strength", "Enchant creature\nEnchanted creature gets +1/+2.", "Enchantment — Aura")
        val n = Normalizer.normalize(f)

        n.lines shouldBe listOf("Enchant creature", "Enchanted creature gets +1/+2.")
        n.attachmentNouns shouldBe listOf("Enchanted")
        n.restore(n.lines) shouldBe f.oracleText
    }

    "mid-sentence occurrences keep their case, and each one is restored on its own" {
        val f = face(
            "Grafted Wargear",
            "Equipped creature gets +3/+2.\nWhenever equipped creature becomes untapped, sacrifice it.",
            "Artifact — Equipment",
        )
        val n = Normalizer.normalize(f)

        n.lines shouldBe listOf(
            "Enchanted creature gets +3/+2.",
            "Whenever enchanted creature becomes untapped, sacrifice it.",
        )
        n.attachmentNouns shouldBe listOf("Equipped", "equipped")
        n.restore(n.lines) shouldBe f.oracleText
    }

    // The boundary check is what keeps the two directions counting the same occurrences: a restore
    // that matched a plural the forward pass skipped would put every later word back one slot off.
    "the plural is not an attachment noun, in either direction" {
        val f = face("Whatever", "Equipped creatures get +1/+1.", "Enchantment")
        val n = Normalizer.normalize(f)

        n.lines shouldBe listOf("Equipped creatures get +1/+1.")
        n.attachmentNouns shouldBe emptyList()
        n.restore(n.lines) shouldBe f.oracleText
    }

    // CR 207.2c: an ability word has no rules meaning, so it is printed shape and lives here rather
    // than as a grammar rule wrapping every sentence the grammar can already read.
    "an ability word comes off the line and goes back on it" {
        val f = face(
            "Iridescent Vinelasher",
            "Landfall — Whenever a land you control enters, this creature deals 1 damage to target opponent.",
        )
        val n = Normalizer.normalize(f)

        n.lines shouldBe listOf("Whenever a land you control enters, ~ deals 1 damage to target opponent.")
        n.abilityWords shouldBe listOf("Landfall")
        n.restore(n.lines) shouldBe f.oracleText
    }

    "the word is recorded per line, so a card with one word and one plain line stays aligned" {
        val f = face("Whatever", "Flying\nThreshold — This creature gets +3/+0.")
        val n = Normalizer.normalize(f)

        n.lines shouldBe listOf("Flying", "~ gets +3/+0.")
        n.abilityWords shouldBe listOf(null, "Threshold")
        n.restore(n.lines) shouldBe f.oracleText
    }

    // The list is CR 207.2c's rather than a pattern, because CR 207.2d's *flavor* words have the
    // identical printed shape and are unbounded — and so does a Saga's chapter marker.
    "a prefix that is not an ability word is left where it stands" {
        val f = face("Whatever", "Bounty — Whenever this creature attacks, draw a card.")
        val n = Normalizer.normalize(f)

        n.lines shouldBe listOf("Bounty — Whenever ~ attacks, draw a card.")
        n.abilityWords shouldBe listOf(null)
        n.restore(n.lines) shouldBe f.oracleText
    }

    // A modal card's rows are one ability laid out over several printed lines. The joined line keeps
    // its own newlines, which is what makes the inverse free — `restore` already joins with "\n".
    "a bullet joins the line above it and comes back as its own row" {
        val f = face(
            "Abrade",
            "Choose one —\n• Abrade deals 3 damage to target creature.\n• Destroy target artifact.",
            typeLine = "Instant",
        )
        val n = Normalizer.normalize(f)

        n.lines shouldBe listOf(
            "Choose one —\n• ~ deals 3 damage to target creature.\n• Destroy target artifact."
        )
        n.restore(n.lines) shouldBe f.oracleText
    }

    // The join is positional and only downward, so a rider printed after the last mode is still its
    // own ability — and the per-line ability-word record stays aligned with what the grammar prints.
    "a line after the modes is not swallowed by them" {
        val f = face("Whatever", "Choose one —\n• Draw a card.\n• You gain 2 life.\nEntwine {2}", typeLine = "Instant")
        val n = Normalizer.normalize(f)

        n.lines shouldBe listOf("Choose one —\n• Draw a card.\n• You gain 2 life.", "Entwine {2}")
        n.abilityWords shouldBe listOf(null, null)
        n.restore(n.lines) shouldBe f.oracleText
    }

    "the self-reference noun follows the printed type line" {
        Reminders.selfNoun("Creature — Angel") shouldBe "this creature"
        Reminders.selfNoun("Artifact") shouldBe "this artifact"
        Reminders.selfNoun("Instant") shouldBe "this spell"
        Reminders.selfNoun("Land") shouldBe "this land"
    }
})
