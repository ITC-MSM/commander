package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lrw.cards.Thoughtseize
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Tests for Thoughtseize.
 *
 * Thoughtseize
 * {B}
 * Sorcery
 * Target player reveals their hand. You choose a nonland card from it. That player discards that
 * card. You lose 2 life.
 *
 * The two things worth pinning: the caster (not the target) picks the card, and the life loss is
 * unconditional — per the 2020-08-07 ruling you lose 2 even when there is nothing to take.
 */
class ThoughtseizeScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(Thoughtseize))
        return driver
    }

    test("the caster chooses a nonland card and the target discards it, at the cost of 2 life") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 20), startingLife = 20)
        val caster = driver.activePlayer!!
        val victim = driver.getOpponent(caster)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putLandOnBattlefield(caster, "Swamp")
        val bear = driver.putCardInHand(victim, "Grizzly Bears")
        driver.putCardInHand(victim, "Swamp")

        val thoughtseize = driver.putCardInHand(caster, "Thoughtseize")
        driver.castSpell(caster, thoughtseize, listOf(victim)).isSuccess shouldBe true
        driver.bothPass()

        val decision = driver.pendingDecision
        decision.shouldBeInstanceOf<SelectCardsDecision>()
        // The *caster* chooses, and the land in the revealed hand is not a legal pick.
        decision.playerId shouldBe caster

        driver.submitCardSelection(caster, listOf(bear)).isSuccess shouldBe true

        driver.getGraveyardCardNames(victim) shouldContain "Grizzly Bears"
        driver.getLifeTotal(caster) shouldBe 18
        driver.getLifeTotal(victim) shouldBe 20
    }

    test("an all-land hand yields nothing but the caster still loses 2 life") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 20), startingLife = 20)
        val caster = driver.activePlayer!!
        val victim = driver.getOpponent(caster)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putLandOnBattlefield(caster, "Swamp")
        driver.putCardInHand(victim, "Swamp")
        driver.putCardInHand(victim, "Forest")
        val victimHandSize = driver.getHandSize(victim)

        val thoughtseize = driver.putCardInHand(caster, "Thoughtseize")
        driver.castSpell(caster, thoughtseize, listOf(victim)).isSuccess shouldBe true
        driver.bothPass()

        // Nothing to take: the hand is untouched.
        driver.getHandSize(victim) shouldBe victimHandSize
        driver.getGraveyardCardNames(victim).any { it != "Swamp" && it != "Forest" } shouldBe false

        driver.getLifeTotal(caster) shouldBe 18
    }

    test("Thoughtseize can target its own caster") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 20), startingLife = 20)
        val caster = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putLandOnBattlefield(caster, "Swamp")
        val bear = driver.putCardInHand(caster, "Grizzly Bears")

        val thoughtseize = driver.putCardInHand(caster, "Thoughtseize")
        driver.castSpell(caster, thoughtseize, listOf(caster)).isSuccess shouldBe true
        driver.bothPass()

        val decision = driver.pendingDecision
        decision.shouldBeInstanceOf<SelectCardsDecision>()
        driver.submitCardSelection(caster, listOf(bear)).isSuccess shouldBe true

        driver.getGraveyardCardNames(caster) shouldContain "Grizzly Bears"
        driver.getLifeTotal(caster) shouldBe 18
    }
})
