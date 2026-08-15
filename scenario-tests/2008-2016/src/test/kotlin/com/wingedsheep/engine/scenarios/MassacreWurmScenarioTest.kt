package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.mbs.cards.MassacreWurm
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Massacre Wurm (MBS #46) — {3}{B}{B}{B} Creature — Phyrexian Wurm 6/5.
 *
 *   When this creature enters, creatures your opponents control get -2/-2 until end of turn.
 *   Whenever a creature an opponent controls dies, that player loses 2 life.
 *
 * Exercises the enters group debuff (per-creature -2/-2 over opponents' creatures), the
 * death-drain trigger (including when the debuff itself kills the creature), that "that player"
 * resolves to the dead creature's controller, and that your own creatures don't drain you.
 */
class MassacreWurmScenarioTest : FunSpec({

    val projector = StateProjector()

    fun resolveStack(driver: GameTestDriver) {
        var guard = 0
        while (guard++ < 40 && driver.state.stack.isNotEmpty() && !driver.isPaused) driver.bothPass()
    }

    fun newGame(): Pair<GameTestDriver, EntityId> {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(MassacreWurm))
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver to driver.activePlayer!!
    }

    fun GameTestDriver.castWurm(you: EntityId): EntityId {
        val card = putCardInHand(you, "Massacre Wurm")
        giveMana(you, Color.BLACK, 3)
        giveColorlessMana(you, 3)
        castSpell(you, card).isSuccess shouldBe true
        bothPass()
        return card
    }

    test("enters gives opponents' creatures -2/-2 until end of turn; a survivor does not drain") {
        val (driver, you) = newGame()
        val opponent = driver.state.turnOrder.first { it != you }
        val giant = driver.putCreatureOnBattlefield(opponent, "Hill Giant") // 3/3 -> 1/1
        val lifeBefore = driver.getLifeTotal(opponent)

        driver.castWurm(you)
        resolveStack(driver)

        val projected = projector.project(driver.state)
        projected.getPower(giant) shouldBe 1
        projected.getToughness(giant) shouldBe 1
        // The creature survived, so its controller loses no life.
        driver.getLifeTotal(opponent) shouldBe lifeBefore
    }

    test("the enters debuff kills a 2/2 and that death makes its controller lose 2 life") {
        val (driver, you) = newGame()
        val opponent = driver.state.turnOrder.first { it != you }
        val bears = driver.putCreatureOnBattlefield(opponent, "Grizzly Bears") // 2/2 -> 0/0, dies
        val lifeBefore = driver.getLifeTotal(opponent)

        driver.castWurm(you)
        resolveStack(driver)

        driver.state.getBattlefield().contains(bears) shouldBe false
        driver.getLifeTotal(opponent) shouldBe lifeBefore - 2
    }

    test("a later opponent-creature death also drains that player, but your own creature's death does not") {
        val (driver, you) = newGame()
        val opponent = driver.state.turnOrder.first { it != you }

        // Wurm resolves with no opponent creatures out, so the ETB does nothing.
        driver.castWurm(you)
        resolveStack(driver)

        val oppLifeBefore = driver.getLifeTotal(opponent)
        val youLifeBefore = driver.getLifeTotal(you)

        // Kill a fresh opponent creature -> that player loses 2 life.
        driver.putCreatureOnBattlefield(opponent, "Hill Giant")
        val giant = driver.findPermanent(opponent, "Hill Giant")!!
        val bolt1 = driver.putCardInHand(you, "Lightning Bolt")
        driver.giveMana(you, Color.RED, 1)
        driver.castSpell(you, bolt1, listOf(giant))
        resolveStack(driver)
        driver.getLifeTotal(opponent) shouldBe oppLifeBefore - 2

        // Kill your own creature -> no drain (the ability only watches opponents' creatures).
        driver.putCreatureOnBattlefield(you, "Hill Giant")
        val ownGiant = driver.findPermanent(you, "Hill Giant")!!
        val bolt2 = driver.putCardInHand(you, "Lightning Bolt")
        driver.giveMana(you, Color.RED, 1)
        driver.castSpell(you, bolt2, listOf(ownGiant))
        resolveStack(driver)
        driver.getLifeTotal(you) shouldBe youLifeBefore
    }
})
