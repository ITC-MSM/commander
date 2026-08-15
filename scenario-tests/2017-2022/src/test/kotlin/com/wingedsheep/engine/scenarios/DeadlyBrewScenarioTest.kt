package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.stx.cards.DeadlyBrew
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Engine coverage for Deadly Brew (STX #176; reprinted in Foundations #654).
 *
 * "Each player sacrifices a creature or planeswalker of their choice. If you sacrificed a
 * permanent this way, you may return another permanent card from your graveyard to your
 * hand."
 *
 * Same shape as Rise of the Witch-king; the two behaviours worth pinning are the ones this
 * card changes: the edict hits creatures *or* planeswalkers, and the recursion returns the
 * chosen card to hand (guarded by the intervening "if you sacrificed" clause).
 */
class DeadlyBrewScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + DeadlyBrew)
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun GameTestDriver.handNames(playerId: com.wingedsheep.sdk.model.EntityId): List<String> =
        getHand(playerId).mapNotNull { state.getEntity(it)?.get<CardComponent>()?.name }

    test("each player sacrifices; you may return another permanent card to your hand") {
        val driver = createDriver()
        val p1 = driver.player1
        val p2 = driver.player2

        // Both players control exactly one creature, so each edict auto-sacrifices it.
        driver.putCreatureOnBattlefield(p1, "Centaur Courser")
        driver.putCreatureOnBattlefield(p2, "Centaur Courser")
        // A distinct permanent card already in p1's graveyard, eligible to return.
        val savannah = driver.putCardInGraveyard(p1, "Savannah Lions")

        val brew = driver.putCardInHand(p1, "Deadly Brew")
        driver.giveMana(p1, Color.BLACK, 1)
        driver.giveMana(p1, Color.GREEN, 1)

        driver.castSpell(p1, brew)
        driver.bothPass() // resolve: both auto-sacrifice, then pause for the optional return

        // Both creatures are gone.
        driver.findPermanent(p1, "Centaur Courser") shouldBe null
        driver.findPermanent(p2, "Centaur Courser") shouldBe null

        // p1 sacrificed a permanent, so the "you may return" selection is offered.
        val decision = driver.pendingDecision
        decision.shouldBeInstanceOf<SelectCardsDecision>()
        decision.playerId shouldBe p1
        driver.submitCardSelection(p1, listOf(savannah))

        // The chosen permanent card moved from graveyard to hand.
        driver.handNames(p1) shouldContain "Savannah Lions"
        driver.getGraveyardCardNames(p1) shouldNotContain "Savannah Lions"
    }

    test("if you sacrificed nothing, you get no return (intervening-if clause)") {
        val driver = createDriver()
        val p1 = driver.player1
        val p2 = driver.player2

        // p1 controls no creature or planeswalker, so p1 sacrifices nothing.
        driver.putCreatureOnBattlefield(p2, "Centaur Courser")
        driver.putCardInGraveyard(p1, "Savannah Lions")

        val brew = driver.putCardInHand(p1, "Deadly Brew")
        driver.giveMana(p1, Color.BLACK, 1)
        driver.giveMana(p1, Color.GREEN, 1)

        driver.castSpell(p1, brew)
        driver.bothPass()

        // p2's creature was sacrificed, but p1 sacrificed nothing.
        driver.findPermanent(p2, "Centaur Courser") shouldBe null

        // No return selection is raised, and the graveyard card stays put.
        driver.pendingDecision shouldBe null
        driver.getGraveyardCardNames(p1) shouldContain "Savannah Lions"
    }
})
