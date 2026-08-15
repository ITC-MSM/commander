package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fin.cards.SephirothFabledSoldier
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Sephiroth, Fabled SOLDIER // Sephiroth, One-Winged Angel (FIN #115).
 *
 * Pins the card-specific assembly:
 *  - Front's "whenever another creature dies, target opponent loses 1 and you gain 1" drain fires on
 *    a foreign death (not on Sephiroth's own).
 *  - The drain's "if this is the fourth time this ability has resolved this turn, transform" clause
 *    flips Sephiroth to the back face only on the fourth resolution.
 *  - The Super Nova emblem created by that transform keeps draining on later deaths, even though the
 *    back face has no dies-trigger of its own.
 *
 * The underlying primitives (drain, SourceAbilityResolvedNTimes + IncrementAbilityResolutionCount,
 * TransformEffect, the global-triggered-ability emblem, SacrificeAnyNumber) are each proven in their
 * own tests; this fixture proves they compose into Sephiroth's printed behavior.
 */
class SephirothFabledSoldierScenarioTest : FunSpec({

    val projector = StateProjector()

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(SephirothFabledSoldier))
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), startingLife = 20)
        return driver
    }

    // Player 1 may not be active at game start (random turn order) — advance until it is.
    fun GameTestDriver.advanceToPlayer1(targetStep: Step) {
        passPriorityUntil(targetStep)
        var safety = 0
        while (activePlayer != player1 && safety < 50) {
            bothPass()
            passPriorityUntil(targetStep)
            safety++
        }
    }

    fun faceName(driver: GameTestDriver, id: EntityId): String =
        driver.state.getEntity(id)!!.get<CardComponent>()!!.name

    /**
     * Lightning Bolt [victim] dead, then walk the resulting Sephiroth "another creature dies" drain
     * trigger to resolution, choosing [opponent] as its target. Robust to the trigger pausing for a
     * target and to any follow-on resolution (the transform clause needs no decision).
     */
    fun killWithBolt(driver: GameTestDriver, caster: EntityId, victim: EntityId, opponent: EntityId) {
        val bolt = driver.putCardInHand(caster, "Lightning Bolt")
        driver.giveMana(caster, Color.RED, 1)
        driver.castSpell(caster, bolt, targets = listOf(victim)).isSuccess shouldBe true

        var guard = 0
        while (guard++ < 40 && (driver.state.stack.isNotEmpty() || driver.isPaused)) {
            val decision = driver.pendingDecision
            if (driver.isPaused && decision is ChooseTargetsDecision) {
                // Sephiroth's drain trigger — "target opponent".
                driver.submitTargetSelection(decision.playerId, listOf(opponent))
            } else if (driver.isPaused) {
                driver.autoResolveDecision()
            } else {
                driver.bothPass()
            }
        }
    }

    test("front drain fires on another creature's death; no transform before the fourth resolution") {
        val driver = createDriver()
        val me = driver.player1
        val opp = driver.player2

        val sephiroth = driver.putCreatureOnBattlefield(me, "Sephiroth, Fabled SOLDIER")
        val bear = driver.putCreatureOnBattlefield(me, "Grizzly Bears")

        driver.advanceToPlayer1(Step.PRECOMBAT_MAIN)

        val myLifeBefore = driver.getLifeTotal(me)
        val oppLifeBefore = driver.getLifeTotal(opp)

        killWithBolt(driver, me, bear, opp)

        // One death → one drain: opponent -1, me +1.
        driver.getLifeTotal(opp) shouldBe oppLifeBefore - 1
        driver.getLifeTotal(me) shouldBe myLifeBefore + 1
        // Only the first resolution — still the front face.
        faceName(driver, sephiroth) shouldBe "Sephiroth, Fabled SOLDIER"
    }

    test("transforms on the fourth resolution; the Super Nova emblem keeps draining afterward") {
        val driver = createDriver()
        val me = driver.player1
        val opp = driver.player2

        val sephiroth = driver.putCreatureOnBattlefield(me, "Sephiroth, Fabled SOLDIER")
        val fodder = (1..5).map { driver.putCreatureOnBattlefield(me, "Grizzly Bears") }

        driver.advanceToPlayer1(Step.PRECOMBAT_MAIN)

        val myLifeBefore = driver.getLifeTotal(me)
        val oppLifeBefore = driver.getLifeTotal(opp)

        // Kill four creatures — the fourth resolution of the drain transforms Sephiroth.
        for (i in 0 until 4) {
            faceName(driver, sephiroth) shouldBe "Sephiroth, Fabled SOLDIER"
            killWithBolt(driver, me, fodder[i], opp)
        }

        // Now the back face: a 5/5 flier.
        faceName(driver, sephiroth) shouldBe "Sephiroth, One-Winged Angel"
        val projected = projector.project(driver.state)
        projected.getPower(sephiroth) shouldBe 5
        projected.getToughness(sephiroth) shouldBe 5
        projected.hasKeyword(sephiroth, Keyword.FLYING) shouldBe true

        // Four drains so far: opponent -4, me +4.
        driver.getLifeTotal(opp) shouldBe oppLifeBefore - 4
        driver.getLifeTotal(me) shouldBe myLifeBefore + 4

        // A fifth death still drains — the back face has no dies-trigger of its own, so this proves
        // the Super Nova emblem is doing the work.
        killWithBolt(driver, me, fodder[4], opp)
        driver.getLifeTotal(opp) shouldBe oppLifeBefore - 5
        driver.getLifeTotal(me) shouldBe myLifeBefore + 5
    }
})
