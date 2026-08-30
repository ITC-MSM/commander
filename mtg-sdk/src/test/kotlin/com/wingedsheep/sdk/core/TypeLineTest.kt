package com.wingedsheep.sdk.core

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class TypeLineTest : DescribeSpec({

    describe("TypeLine.parse") {
        it("preserves hyphens inside subtype names") {
            val parsed = TypeLine.parse("Artifact Creature — Assembly-Worker")

            parsed.cardTypes shouldBe setOf(CardType.ARTIFACT, CardType.CREATURE)
            parsed.subtypes shouldBe setOf(Subtype("Assembly-Worker"))
        }

        it("preserves another hyphenated subtype") {
            val parsed = TypeLine.parse("Land — Urza's Power-Plant")

            parsed.cardTypes shouldBe setOf(CardType.LAND)
            parsed.subtypes shouldBe setOf(
                Subtype("Urza's"),
                Subtype("Power-Plant"),
            )
        }

        it("still accepts an ASCII hyphen as a spaced type-subtype separator") {
            val parsed = TypeLine.parse("Artifact Creature - Assembly-Worker")

            parsed.cardTypes shouldBe setOf(CardType.ARTIFACT, CardType.CREATURE)
            parsed.subtypes shouldBe setOf(Subtype("Assembly-Worker"))
        }
    }
})
