package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.sos.cards.FollowTheLumarets
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.TypeLine
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.CardScript
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.scripting.effects.GainLifeEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private val LifeGainTestSpell = CardDefinition(
    name = "Lumaret Healing Test",
    manaCost = ManaCost.parse("{W}"),
    typeLine = TypeLine.instant(),
    oracleText = "You gain 3 life.",
    script = CardScript.spell(effect = GainLifeEffect(3, EffectTarget.Controller))
)

/**
 * Follow the Lumarets — {1}{G} Sorcery.
 * Infusion — Look at the top four cards of your library. You may reveal a creature or land card and
 * put it into your hand. If you gained life this turn, you may instead reveal two creature and/or
 * land cards and put them into your hand. Put the rest on the bottom of your library in a random
 * order.
 *
 * Verifies the Infusion bonus: the per-reveal upper bound is 1 normally and 2 when life was gained
 * this turn (`DynamicAmount.Conditional(YouGainedLifeThisTurn, 2, 1)`).
 */
class FollowTheLumaretsScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + FollowTheLumarets + LifeGainTestSpell)
        return driver
    }

    test("without life gained this turn, at most one creature/land card may be taken") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Grizzly Bears" to 40))
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Top four (deeper-to-shallower puts): identify the four exact cards looked at.
        val c3 = driver.putCardOnTopOfLibrary(player, "Centaur Courser")
        val c4 = driver.putCardOnTopOfLibrary(player, "Centaur Courser")
        val c1 = driver.putCardOnTopOfLibrary(player, "Grizzly Bears")
        val c2 = driver.putCardOnTopOfLibrary(player, "Grizzly Bears")
        val lookedAt = setOf(c1, c2, c3, c4)

        val spell = driver.putCardInHand(player, "Follow the Lumarets")
        driver.giveMana(player, Color.GREEN, 1)
        driver.giveColorlessMana(player, 1)

        driver.castSpell(player, spell)
        driver.bothPass() // resolve → pauses on the reveal selection

        val decision = driver.pendingDecision as SelectCardsDecision
        decision.minSelections shouldBe 0
        decision.maxSelections shouldBe 1 // Infusion bonus not active

        driver.submitCardSelection(player, listOf(c1))
        driver.isPaused shouldBe false
        // Exactly the one chosen looked-at card reached hand; the others were bottomed.
        driver.getHand(player).count { it in lookedAt } shouldBe 1
        driver.getHand(player).contains(c1) shouldBe true
    }

    test("Infusion: with life gained this turn, up to two creature/land cards may be taken") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Grizzly Bears" to 40))
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Gain life this turn so the Infusion condition turns on.
        val healSpell = driver.putCardInHand(player, "Lumaret Healing Test")
        driver.giveMana(player, Color.WHITE, 1)
        driver.castSpell(player, healSpell)
        driver.bothPass()

        driver.putCardOnTopOfLibrary(player, "Centaur Courser")
        driver.putCardOnTopOfLibrary(player, "Centaur Courser")
        val c1 = driver.putCardOnTopOfLibrary(player, "Grizzly Bears")
        val c2 = driver.putCardOnTopOfLibrary(player, "Grizzly Bears")

        val spell = driver.putCardInHand(player, "Follow the Lumarets")
        driver.giveMana(player, Color.GREEN, 1)
        driver.giveColorlessMana(player, 1)

        driver.castSpell(player, spell)
        driver.bothPass()

        val decision = driver.pendingDecision as SelectCardsDecision
        decision.maxSelections shouldBe 2 // Infusion bonus active

        driver.submitCardSelection(player, listOf(c1, c2))
        driver.isPaused shouldBe false
        driver.getHand(player).contains(c1) shouldBe true
        driver.getHand(player).contains(c2) shouldBe true
    }
})
