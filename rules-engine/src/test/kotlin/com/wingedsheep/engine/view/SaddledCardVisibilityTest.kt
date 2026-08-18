package com.wingedsheep.engine.view

import com.wingedsheep.engine.core.SaddleMount
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.KeywordAbility
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * A Mount's saddle state as the clients see it (CR 702.171).
 *
 * Saddled is a designation with no rules meaning of its own — every Mount payoff is gated on it,
 * it is granted only by a resolved saddle ability, and it disappears at cleanup without any event
 * of its own. A saddled Mount and an unsaddled one are otherwise identical permanents, so
 * [ClientStateTransformer] surfaces [ClientCard.isSaddled] alongside the printed
 * [ClientCard.saddleRequirement]. Both are public: whether a Mount is switched on, and how much
 * power its controller must spend to switch it on, are exactly what the other players block
 * around. Mirrors [DashedCardVisibilityTest] and [WarpedCardVisibilityTest].
 */
class SaddledCardVisibilityTest : FunSpec({

    val testMount = card("Test Mount") {
        manaCost = "{2}"
        typeLine = "Creature — Mount"
        power = 1
        toughness = 4
        oracleText = "Saddle 2\nWhenever Test Mount attacks while saddled, draw a card."
        keywordAbility(KeywordAbility.saddle(2))
        triggeredAbility {
            trigger = Triggers.Attacks
            triggerRestriction = Conditions.SourceIsSaddled
            effect = Effects.DrawCards(1)
        }
    }

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + testMount)
        d.initMirrorMatch(Deck.of("Forest" to 40), skipMulligans = true, startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    fun GameTestDriver.viewOf(viewer: EntityId, id: EntityId) =
        ClientStateTransformer(cardRegistry).transform(state, viewer).cards[id].shouldNotBeNull()

    test("an unsaddled Mount exposes its Saddle N and reads as not saddled") {
        val d = driver()
        val mount = d.putCreatureOnBattlefield(d.player1, "Test Mount")

        d.viewOf(d.player1, mount).saddleRequirement shouldBe 2
        d.viewOf(d.player1, mount).isSaddled shouldBe false
    }

    test("a creature without saddle exposes no saddle requirement") {
        val d = driver()
        val bear = d.putCreatureOnBattlefield(d.player1, "Grizzly Bears")

        d.viewOf(d.player1, bear).saddleRequirement shouldBe null
        d.viewOf(d.player1, bear).isSaddled shouldBe false
    }

    test("a resolved saddle ability flags isSaddled for both players") {
        val d = driver()
        val mount = d.putCreatureOnBattlefield(d.player1, "Test Mount")
        val bear = d.putCreatureOnBattlefield(d.player1, "Grizzly Bears") // power 2

        d.submitSuccess(SaddleMount(d.player1, mount, listOf(bear)))
        d.bothPass()

        d.viewOf(d.player1, mount).isSaddled shouldBe true
        // Public — the opponent needs it to decide how to block.
        d.viewOf(d.player2, mount).isSaddled shouldBe true
        d.viewOf(d.player2, mount).saddleRequirement shouldBe 2
    }

    test("the flag clears with the designation at end of turn, but the requirement stays") {
        val d = driver()
        val mount = d.putCreatureOnBattlefield(d.player1, "Test Mount")
        val bear = d.putCreatureOnBattlefield(d.player1, "Grizzly Bears")

        d.submitSuccess(SaddleMount(d.player1, mount, listOf(bear)))
        d.bothPass()
        d.viewOf(d.player1, mount).isSaddled shouldBe true

        d.passPriorityUntil(Step.POSTCOMBAT_MAIN)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        d.viewOf(d.player1, mount).isSaddled shouldBe false
        // Still a Mount — the requirement is printed, not conferred.
        d.viewOf(d.player1, mount).saddleRequirement shouldBe 2
    }
})
