package com.wingedsheep.ai.llm

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

class AiResponseParserTest : StringSpec({

    val parser = AiResponseParser()

    "parses explicit keep and mulligan responses" {
        parser.parseMulliganChoice("Keep this hand") shouldBe true
        parser.parseMulliganChoice("Take a mulligan") shouldBe false
    }

    "parses lettered mulligan choices" {
        parser.parseMulliganChoice("A") shouldBe true
        parser.parseMulliganChoice("[A]") shouldBe true
        parser.parseMulliganChoice("B") shouldBe false
        parser.parseMulliganChoice("[B]") shouldBe false
    }

    "preserves the boolean from the yes-no fallback" {
        parser.parseMulliganChoice("yes") shouldBe true
        parser.parseMulliganChoice("no") shouldBe false
    }

    "returns null for an unparseable mulligan response" {
        parser.parseMulliganChoice("Draw seven cards").shouldBeNull()
    }

    "does not let a restated option outrank the stated choice" {
        parser.parseMulliganChoice("Mulligan - I would not keep this hand").shouldBeNull()
        parser.parseMulliganChoice("No, do not keep it - mulligan") shouldBe false
        parser.parseMulliganChoice("Yes, keep - no mulligan needed") shouldBe true
    }

    "matches keep and mulligan as whole words" {
        parser.parseMulliganChoice("Some housekeeping first").shouldBeNull()
        parser.parseMulliganChoice("Keeping this hand") shouldBe true
    }
})
