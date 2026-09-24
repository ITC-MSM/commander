package com.wingedsheep.sdk.model

import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.TypeLine
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.conditions.Compare
import com.wingedsheep.sdk.scripting.conditions.ComparisonOperator
import com.wingedsheep.sdk.scripting.conditions.NotCondition
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.effects.DrawCardsEffect
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.serialization.CardSerialization
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotContain

/**
 * [CardDefinition.graveyardThreshold] and [CardDefinition.deliriumThreshold] — the numbers the
 * client's threshold / delirium progress badges count toward. Derived from the typed script; the
 * corpus-wide parity against the JSON walk they replaced was checked once when they were introduced.
 */
class GraveyardThresholdsTest : DescribeSpec({

    fun creature(script: CardScript = CardScript.EMPTY, backFace: CardDefinition? = null) =
        CardDefinition.creature(
            name = "Test Beast",
            manaCost = ManaCost.parse("{2}{G}"),
            subtypes = setOf(Subtype("Beast")),
            power = 2,
            toughness = 2,
        ).copy(script = script, backFace = backFace)

    fun instant(script: CardScript) =
        CardDefinition(name = "Test Spell", manaCost = ManaCost.parse("{1}{B}"), typeLine = TypeLine.instant(), script = script)

    fun conditionalStatic(condition: com.wingedsheep.sdk.scripting.conditions.Condition) =
        ConditionalStaticAbility(ModifyStats(1, 1), condition)

    fun draw(n: Int) = DrawCardsEffect(DynamicAmount.Fixed(n))

    describe("graveyardThreshold") {
        it("reads a static gated on cards in your graveyard, inside and/or composition") {
            creature(CardScript(staticAbilities = listOf(conditionalStatic(Conditions.CardsInGraveyardAtLeast(7)))))
                .graveyardThreshold shouldBe 7
            creature(
                CardScript(
                    staticAbilities = listOf(
                        conditionalStatic(Conditions.All(Conditions.CardsInGraveyardAtLeast(7), Conditions.CardsInGraveyardAtLeast(5)))
                    )
                )
            ).graveyardThreshold shouldBe 5
        }

        it("counts a strict comparison from one above, and ignores a negated gate") {
            val gt = Compare(DynamicAmount.Count(com.wingedsheep.sdk.scripting.references.Player.You, com.wingedsheep.sdk.core.Zone.GRAVEYARD), ComparisonOperator.GT, DynamicAmount.Fixed(6))
            creature(CardScript(staticAbilities = listOf(conditionalStatic(gt)))).graveyardThreshold shouldBe 7
            creature(CardScript(staticAbilities = listOf(conditionalStatic(NotCondition(gt))))).graveyardThreshold shouldBe null
        }

        it("is null on a card with no such gate") {
            creature().graveyardThreshold shouldBe null
        }
    }

    describe("deliriumThreshold") {
        it("finds delirium inside a spell effect") {
            instant(CardScript(spellEffect = ConditionalEffect(Conditions.Delirium(), draw(2), draw(1))))
                .deliriumThreshold shouldBe 4
        }

        it("finds delirium on the back face") {
            val back = creature(CardScript(staticAbilities = listOf(conditionalStatic(Conditions.Delirium()))))
            creature(backFace = back).deliriumThreshold shouldBe 4
        }

        it("is null on a card that doesn't care, and on a raw graveyard-size gate") {
            creature().deliriumThreshold shouldBe null
            creature(CardScript(staticAbilities = listOf(conditionalStatic(Conditions.CardsInGraveyardAtLeast(4)))))
                .deliriumThreshold shouldBe null
        }

        it("is derived data, never part of the card's JSON") {
            val card = instant(CardScript(spellEffect = ConditionalEffect(Conditions.Delirium(), draw(2))))
            card.deliriumThreshold shouldBe 4
            val json = CardSerialization.json.encodeToString(CardDefinition.serializer(), card)
            json shouldNotContain "deliriumThreshold"
            json shouldNotContain "graveyardThreshold"
        }
    }
})
