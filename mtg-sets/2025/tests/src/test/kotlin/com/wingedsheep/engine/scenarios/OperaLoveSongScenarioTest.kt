package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fin.cards.OperaLoveSong
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Opera Love Song — {1}{R} Instant, modal "Choose one —".
 *
 * Mode 1: exile the top two cards of your library; you may play them until your next end step.
 * Mode 2: one or two target creatures each get +2/+0 until end of turn.
 */
class OperaLoveSongScenarioTest : FunSpec({

    val projector = StateProjector()

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(OperaLoveSong)
        return driver
    }

    test("Mode 1 — impulse exiles the top two cards and grants permission to play them") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 30), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val me = driver.activePlayer!!
        driver.putCardOnTopOfLibrary(me, "Mountain")
        driver.putCardOnTopOfLibrary(me, "Mountain")

        val spell = driver.putCardInHand(me, "Opera Love Song")
        driver.giveMana(me, Color.RED, 2)
        driver.submit(CastSpell(playerId = me, cardId = spell, chosenModes = listOf(0))).isSuccess shouldBe true
        driver.bothPass()

        // Two cards exiled, both flagged playable.
        val exiled = driver.getExile(me)
        exiled.size shouldBe 2
        exiled.forEach { card ->
            driver.state.mayPlayPermissions.any { card in it.cardIds } shouldBe true
        }
    }

    test("Mode 2 — two target creatures each get +2/+0 until end of turn") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 30), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val me = driver.activePlayer!!
        val a = driver.putCreatureOnBattlefield(me, "Centaur Courser") // 3/3
        val b = driver.putCreatureOnBattlefield(me, "Centaur Courser") // 3/3

        val spell = driver.putCardInHand(me, "Opera Love Song")
        driver.giveMana(me, Color.RED, 2)

        val targets = listOf(ChosenTarget.Permanent(a), ChosenTarget.Permanent(b))
        driver.submit(
            CastSpell(
                playerId = me,
                cardId = spell,
                targets = targets,
                chosenModes = listOf(1),
                modeTargetsOrdered = listOf(targets)
            )
        ).isSuccess shouldBe true
        driver.bothPass()

        // Each gets +2/+0.
        projector.getProjectedPower(driver.state, a) shouldBe 5
        projector.getProjectedToughness(driver.state, a) shouldBe 3
        projector.getProjectedPower(driver.state, b) shouldBe 5
        projector.getProjectedToughness(driver.state, b) shouldBe 3
    }

    test("Mode 2 — works with a single target") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 30), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val me = driver.activePlayer!!
        val a = driver.putCreatureOnBattlefield(me, "Centaur Courser")

        val spell = driver.putCardInHand(me, "Opera Love Song")
        driver.giveMana(me, Color.RED, 2)

        val targets = listOf(ChosenTarget.Permanent(a))
        driver.submit(
            CastSpell(
                playerId = me,
                cardId = spell,
                targets = targets,
                chosenModes = listOf(1),
                modeTargetsOrdered = listOf(targets)
            )
        ).isSuccess shouldBe true
        driver.bothPass()

        projector.getProjectedPower(driver.state, a) shouldBe 5
        projector.getProjectedToughness(driver.state, a) shouldBe 3
    }
})
