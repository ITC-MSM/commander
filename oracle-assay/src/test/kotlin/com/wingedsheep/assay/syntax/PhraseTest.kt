package com.wingedsheep.assay.syntax

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * The kernel's own contract, tested without any Magic vocabulary: templates, both directions, the
 * ambiguity classification, and the guards that keep a corpus run from crashing or hanging.
 */
class PhraseTest : StringSpec({

    val cardinal = token("a number", Regex("""0|[1-9][0-9]*"""), { it.toInt() }, { it.toString() })

    "a template rule parses and prints the same surface form" {
        val draw = phrase<Int>("draw {n} cards") {
            slot("n", cardinal)
            build { it.int("n") }
            match { bind("n" to it) }
        }

        (draw.parseText("draw 3 cards") as ParseOutcome.Accepted).value shouldBe 3
        draw.unparse(3) shouldBe "draw 3 cards"
    }

    "a partial parse does not count: only whole-span readings are accepted" {
        val draw = phrase<Int>("draw {n} cards") {
            slot("n", cardinal)
            build { it.int("n") }
            match { bind("n" to it) }
        }

        draw.parseText("draw 3 cards and more").shouldBeInstanceOf<ParseOutcome.Declined>()
    }

    "a decline reports the token it died on" {
        val draw = phrase<Int>("draw {n} cards") {
            slot("n", cardinal)
            build { it.int("n") }
            match { bind("n" to it) }
        }

        val declined = draw.parseText("draw three cards").shouldBeInstanceOf<ParseOutcome.Declined>()
        declined.deadToken("draw three cards") shouldBe "three"
        declined.expected shouldBe listOf("a number")
    }

    "two rules spelling one meaning are redundancy, not ambiguity" {
        val a = constant("a card", 1)
        val b = constant("a card", 1)
        val either = oneOf("either", a, b)

        val accepted = either.parseText("a card").shouldBeInstanceOf<ParseOutcome.Accepted<Int>>()
        accepted.value shouldBe 1
        accepted.redundantReadings shouldBe 1
    }

    "two rules with two meanings for one text is a hard error, and neither is picked" {
        val either = oneOf("either", constant("a card", 1), constant("a card", 2))

        val ambiguous = either.parseText("a card").shouldBeInstanceOf<ParseOutcome.Ambiguous<Int>>()
        ambiguous.readings shouldBe listOf(1, 2)
    }

    "an alternate spelling parses and never prints" {
        val canonical = constant("a card", 1)
        val alt = alternate(constant("one card", 1))
        val either = oneOf("either", canonical, alt)

        (either.parseText("one card") as ParseOutcome.Accepted).value shouldBe 1
        either.unparse(1) shouldBe "a card"
    }

    "a canonical rule cannot be declared without both directions" {
        shouldThrow<IllegalArgumentException> {
            phrase<Int>("draw {n} cards") {
                slot("n", cardinal)
                build { it.int("n") }
            }
        }
    }

    "a template referencing an unregistered slot is rejected at construction" {
        shouldThrow<IllegalArgumentException> {
            phrase<Int>("draw {n} cards") {
                build { 0 }
                match { bind() }
            }
        }
    }

    "brace runs that are not slot names stay literal, so mana symbols need no escaping" {
        val ward = phrase<Int>("ward {2} for {n}") {
            slot("n", cardinal)
            build { it.int("n") }
            match { bind("n" to it) }
        }

        (ward.parseText("ward {2} for 3") as ParseOutcome.Accepted).value shouldBe 3
        ward.unparse(3) shouldBe "ward {2} for 3"
    }

    "a separated run returns every prefix, and the whole-span filter picks the full one" {
        val list = separated("numbers", cardinal, ", ")

        (list.parseText("1, 2, 3") as ParseOutcome.Accepted).value shouldBe listOf(1, 2, 3)
        list.unparse(listOf(1, 2, 3)) shouldBe "1, 2, 3"
    }

    "a separated run with min 2 refuses a single element in both directions" {
        val list = separated("numbers", cardinal, "; ", min = 2)

        list.parseText("1").shouldBeInstanceOf<ParseOutcome.Declined>()
        list.unparse(listOf(1)) shouldBe null
        (list.parseText("1; 2") as ParseOutcome.Accepted).value shouldBe listOf(1, 2)
    }

    "a leaf that would print something it cannot read back refuses to print at all" {
        // `write` drops the leading zero the pattern would refuse to read, so the self-check fires.
        val padded = token("a padded number", Regex("""[0-9]{2}"""), { it.toInt() }, { "0$it" })
        padded.unparse(5) shouldBe "05"
        padded.unparse(123) shouldBe null
    }

    "a leaf never throws: a malformed value declines" {
        val strict = token("a number", Regex("""\d+"""), { it.toInt() }, { it.toString() })
        strict.parseText("99999999999999999999").shouldBeInstanceOf<ParseOutcome.Declined>()
    }

    "left recursion is surfaced as a decline rather than a stack overflow" {
        lateinit var recursive: Phrase<Int>
        val holder = object : Phrase<Int>() {
            override val name = "recursive"
            override fun parseHere(ctx: ParseContext, from: Int) = recursive.parseAt(ctx, from)
            override fun unparse(value: Int): String? = null
        }
        recursive = holder

        val declined = holder.parseText("anything").shouldBeInstanceOf<ParseOutcome.Declined>()
        declined.reason shouldBe DeclineReason.LEFT_RECURSION
    }

    "sentence case is applied at the line boundary, not inside the grammar" {
        val keyword = separated("keywords", oneOf("kw", constant("flying", "F"), constant("first strike", "S")), ", ")

        (keyword.parseLine("Flying, first strike") as ParseOutcome.Accepted).value shouldBe listOf("F", "S")
        keyword.printLine(listOf("F", "S")) shouldBe "Flying, first strike"
    }

    "a line whose first letter is lowercase declines rather than being silently repaired" {
        val keyword = constant("flying", "F")
        keyword.parseLine("flying").shouldBeInstanceOf<ParseOutcome.Declined>()
    }

    "a line starting with a symbol passes through sentence case untouched" {
        SentenceCase.decapitalize("{T}, {Q}") shouldBe "{T}, {Q}"
        SentenceCase.capitalize("{T}, {Q}") shouldBe "{T}, {Q}"
    }

    // An activated ability's effect clause is a sentence start too, which is what lets the mid-
    // sentence Steps templates be slotted after a cost colon instead of being restated capitalized.
    "an ability cost's colon starts a sentence, in both directions" {
        SentenceCase.decapitalize("{T}: Add {C}.") shouldBe "{T}: add {C}."
        SentenceCase.capitalize("{T}: add {C}.") shouldBe "{T}: Add {C}."
        SentenceCase.decapitalize("Creatures you control have \"{T}: Add {G}.\"") shouldBe
            "creatures you control have \"{T}: add {G}.\""
    }

    "a lowercase clause after a cost colon declines, for the same reason a lowercase line does" {
        SentenceCase.decapitalize("• Setting: a land") shouldBe null
    }

    // `shape` exists for the explorer, which walks the wired grammar from its root entry point. It
    // is read-only and never consulted while parsing or printing, so what these assert is that
    // every combinator describes itself — a new one that forgot to would silently make a whole
    // subtree of the grammar invisible rather than failing anywhere.

    "every combinator describes its own structure" {
        val flying = constant("flying", "F")
        val strike = constant("first strike", "S")

        phrase<Int>("draw {n} cards") { slot("n", cardinal); build { it.int("n") }; match { bind("n" to it) } }
            .shape.shouldBeInstanceOf<RuleShape.Template>().template shouldBe "draw {n} cards"
        oneOf("kw", flying, strike).shape.shouldBeInstanceOf<RuleShape.Choice>().alternatives shouldBe
            listOf(flying, strike)
        separated("kws", flying, "; ", min = 2).shape.shouldBeInstanceOf<RuleShape.Run>().separator shouldBe "; "
        alternate(flying).shape.shouldBeInstanceOf<RuleShape.Alternate>().inner shouldBe flying
        cardinal.shape.shouldBeInstanceOf<RuleShape.Leaf>().pattern shouldBe """0|[1-9][0-9]*"""
    }

    "the structure walk reaches every rule a phrase is built from" {
        val leaf = constant("flying", "F")
        val run = separated("kws", leaf, ", ")
        val line = phrase<List<String>>("{kws}") {
            slot("kws", run)
            build { it.value<List<String>>("kws") }
            match { bind("kws" to it) }
        }
        val root = oneOf("root", line)

        val seen = mutableSetOf<Phrase<*>>()
        fun walk(p: Phrase<*>) { if (seen.add(p)) p.shape.children.forEach(::walk) }
        walk(root)

        seen shouldBe setOf(root, line, run, leaf)
    }
})
