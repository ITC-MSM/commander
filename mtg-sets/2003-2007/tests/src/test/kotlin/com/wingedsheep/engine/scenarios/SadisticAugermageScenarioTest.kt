package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Sadistic Augermage — Ravnica: City of Guilds #103, {2}{B} Creature — Human Wizard 3/1
 *
 * When this creature dies, each player puts a card from their hand on top of their library.
 *
 * The symmetric twin of Chimney Imp's targeted tuck, and what is under test is the *symmetry*:
 * both players get a selection, in APNAP order, and each card lands on top of the chooser's own
 * library rather than all of them landing on one. `Effects.EachPlayerPutsCardsOnTopOfLibrary` is
 * `EachPlayerDiscards` with the destination swapped, so the failure mode worth pinning is a
 * destination that forgot to rebind `Player.You` per iteration and tucked every card onto the
 * controller's library.
 *
 * Also pinned: it is a tuck, not a discard — nothing reaches a graveyard — and a player with an
 * empty hand is skipped rather than deadlocking the trigger.
 */
class SadisticAugermageScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        return driver
    }

    /** Bolt [target] dead and resolve everything it puts on the stack up to the next decision. */
    fun killWithBolt(driver: GameTestDriver, caster: EntityId, target: EntityId) {
        driver.giveMana(caster, Color.RED, 1)
        val bolt = driver.putCardInHand(caster, "Lightning Bolt")
        driver.castSpellWithTargets(caster, bolt, listOf(ChosenTarget.Permanent(target)))
        var guard = 0
        while (guard++ < 30 && driver.state.stack.isNotEmpty() && !driver.isPaused) {
            driver.bothPass()
        }
    }

    test("each player tucks their own chosen card on top of their own library") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 20, "Mountain" to 20), startingLife = 20)

        val controller = driver.activePlayer!!
        val opponent = driver.getOpponent(controller)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val augermage = driver.putCreatureOnBattlefield(controller, "Sadistic Augermage")

        // A distinctive card in each hand so each tuck is identifiable among the lands.
        val controllerPick = driver.putCardInHand(controller, "Centaur Courser")
        val opponentPick = driver.putCardInHand(opponent, "Grizzly Bears")

        val controllerLibraryBefore = driver.state.getLibrary(controller).size
        val opponentLibraryBefore = driver.state.getLibrary(opponent).size
        val opponentGraveyardBefore = driver.state.getGraveyard(opponent).size

        killWithBolt(driver, controller, augermage)

        // APNAP (CR 101.4): the active player — the Augermage's controller here — chooses first.
        val first = driver.pendingDecision
        first shouldNotBe null
        (first as SelectCardsDecision).playerId shouldBe controller
        driver.submitCardSelection(controller, listOf(controllerPick))

        // Then the opponent gets their own selection.
        val second = driver.pendingDecision
        second shouldNotBe null
        (second as SelectCardsDecision).playerId shouldBe opponent
        driver.submitCardSelection(opponent, listOf(opponentPick))

        // Each card is on top of *its own owner's* library — not both on the controller's.
        driver.state.getLibrary(controller).size shouldBe controllerLibraryBefore + 1
        driver.state.getLibrary(controller).first() shouldBe controllerPick
        driver.state.getLibrary(opponent).size shouldBe opponentLibraryBefore + 1
        driver.state.getLibrary(opponent).first() shouldBe opponentPick

        // A tuck, not a discard: neither chosen card reached a graveyard, and the opponent's
        // graveyard is untouched entirely.
        driver.state.getGraveyard(controller).contains(controllerPick) shouldBe false
        driver.state.getGraveyard(opponent).size shouldBe opponentGraveyardBefore
    }

    test("a player with an empty hand is skipped, and the other still tucks") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 20, "Mountain" to 20), startingLife = 20)

        val controller = driver.activePlayer!!
        val opponent = driver.getOpponent(controller)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val augermage = driver.putCreatureOnBattlefield(controller, "Sadistic Augermage")
        val controllerPick = driver.putCardInHand(controller, "Centaur Courser")

        // The opponent has nothing to put back — they are skipped, not stuck.
        driver.getHand(opponent).toList().forEach { driver.moveToGraveyard(it) }
        driver.getHandSize(opponent) shouldBe 0
        val controllerLibraryBefore = driver.state.getLibrary(controller).size
        val opponentLibraryBefore = driver.state.getLibrary(opponent).size

        killWithBolt(driver, controller, augermage)

        // Only the controller is asked, and only once.
        val decision = driver.pendingDecision
        decision shouldNotBe null
        (decision as SelectCardsDecision).playerId shouldBe controller
        driver.submitCardSelection(controller, listOf(controllerPick))

        (driver.pendingDecision as? SelectCardsDecision) shouldBe null
        driver.state.getLibrary(controller).size shouldBe controllerLibraryBefore + 1
        driver.state.getLibrary(controller).first() shouldBe controllerPick
        driver.state.getLibrary(opponent).size shouldBe opponentLibraryBefore
    }
})
