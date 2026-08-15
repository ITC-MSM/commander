package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.blb.cards.EarlyWinter
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Early Winter (BLB), mode 2: "Target opponent exiles an enchantment they control."
 *
 * Regression guard: this used to be modeled as "exile target enchantment an opponent
 * controls" — the caster picked the enchantment and hexproof would block it. The real
 * card targets the OPPONENT, who then chooses which of their enchantments to exile.
 */
class EarlyWinterTest : FunSpec({

    val testGlow: CardDefinition = card("Test Glow") {
        manaCost = "{W}"
        typeLine = "Enchantment"
    }

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(EarlyWinter, testGlow))
        return driver
    }

    test("the targeted opponent chooses which of their enchantments is exiled") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40))
        val active = driver.activePlayer!!
        val opponent = driver.getOpponent(active)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val glowA = driver.putPermanentOnBattlefield(opponent, "Test Glow")
        val glowB = driver.putPermanentOnBattlefield(opponent, "Test Glow")

        driver.giveMana(active, Color.BLACK, 1)
        driver.giveColorlessMana(active, 4)
        val winter = driver.putCardInHand(active, "Early Winter")
        driver.submitSuccess(
            CastSpell(
                playerId = active,
                cardId = winter,
                chosenModes = listOf(1),
                modeTargetsOrdered = listOf(listOf(ChosenTarget.Player(opponent))),
                targets = listOf(ChosenTarget.Player(opponent))
            )
        )
        driver.bothPass()

        // The OPPONENT (not the caster) picks among their two enchantments.
        val decision = driver.pendingDecision
        decision.shouldNotBeNull()
        decision.shouldBeInstanceOf<SelectCardsDecision>()
        decision.playerId shouldBe opponent
        decision.options shouldContainExactlyInAnyOrder listOf(glowA, glowB)

        driver.submitCardSelection(opponent, listOf(glowB))

        driver.state.getZone(opponent, Zone.EXILE).contains(glowB) shouldBe true
        driver.findPermanent(opponent, "Test Glow") shouldBe glowA
    }
})
