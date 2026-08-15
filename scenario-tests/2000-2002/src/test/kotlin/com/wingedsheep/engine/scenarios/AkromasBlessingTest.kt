package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseColorDecision
import com.wingedsheep.engine.core.ColorChosenResponse
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Tests for Akroma's Blessing — the group-protection path of the
 * `ChooseColorThen(ForEachInGroup(GrantProtectionFromChosenColor(Self)))`
 * composition that replaced the old `ChooseColorAndGrantProtectionToGroupEffect`
 * monolith.
 *
 * Akroma's Blessing: {2}{W} Instant
 * Choose a color. Creatures you control gain protection from the chosen color
 * until end of turn.
 */
class AkromasBlessingTest : FunSpec({

    val projector = StateProjector()

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        return driver
    }

    test("Akroma's Blessing grants protection from the chosen color to every creature you control") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Plains" to 40),
            startingLife = 20
        )

        val player1 = driver.activePlayer!!
        val player2 = driver.getOpponent(player1)

        // Two creatures controlled by player1, one by the opponent.
        val mine1 = driver.putCreatureOnBattlefield(player1, "Grizzly Bears")
        val mine2 = driver.putCreatureOnBattlefield(player1, "Centaur Courser")
        val theirs = driver.putCreatureOnBattlefield(player2, "Grizzly Bears")

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Cast Akroma's Blessing ({2}{W}).
        val blessing = driver.putCardInHand(player1, "Akroma's Blessing")
        driver.giveMana(player1, Color.WHITE, 3)
        driver.castSpell(player1, blessing)

        // Resolve the spell, which pauses for the color choice.
        driver.bothPass()
        driver.isPaused shouldBe true
        driver.pendingDecision.shouldBeInstanceOf<ChooseColorDecision>()

        val decision = driver.pendingDecision as ChooseColorDecision
        driver.submitDecision(player1, ColorChosenResponse(decision.id, Color.RED))

        // Both of player1's creatures gain protection from red; the opponent's does not.
        val projected = projector.project(driver.state)
        projected.hasKeyword(mine1, "PROTECTION_FROM_RED") shouldBe true
        projected.hasKeyword(mine2, "PROTECTION_FROM_RED") shouldBe true
        projected.hasKeyword(theirs, "PROTECTION_FROM_RED") shouldBe false
    }

    test("Akroma's Blessing protection wears off at end of turn") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Plains" to 40),
            startingLife = 20
        )

        val player1 = driver.activePlayer!!

        val mine = driver.putCreatureOnBattlefield(player1, "Grizzly Bears")

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val blessing = driver.putCardInHand(player1, "Akroma's Blessing")
        driver.giveMana(player1, Color.WHITE, 3)
        driver.castSpell(player1, blessing)
        driver.bothPass()

        val decision = driver.pendingDecision as ChooseColorDecision
        driver.submitDecision(player1, ColorChosenResponse(decision.id, Color.RED))

        projector.project(driver.state).hasKeyword(mine, "PROTECTION_FROM_RED") shouldBe true

        // Advance to the next turn's upkeep — the until-end-of-turn grant should be gone.
        driver.passPriorityUntil(Step.UPKEEP)

        projector.project(driver.state).hasKeyword(mine, "PROTECTION_FROM_RED") shouldBe false
    }
})
