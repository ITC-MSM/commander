package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ColorChosenResponse
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.rav.cards.BatheInLight
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Bathe in Light (RAV #2) — "Radiance — Choose a color. Target creature and each other creature
 * that shares a color with it gain protection from the chosen color until end of turn."
 *
 * Two things are being pinned. The **chosen colour** must reach every grant, not just the target's
 * — the whole group is protected from one colour picked at resolution, and that colour has nothing
 * to do with the colour the group shares (ruling 2005-10-01). The **radiance group** is the target
 * plus every *other* creature sharing a colour with it, on both sides of the table; a colorless
 * target shares a colour with nothing, so it radiates to no one.
 */
class BatheInLightScenarioTest : FunSpec({

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + BatheInLight)
        d.initMirrorMatch(deck = Deck.of("Plains" to 40), skipMulligans = true, startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    fun GameTestDriver.hasProtectionFromRed(id: EntityId): Boolean =
        state.projectedState.hasKeyword(id, "PROTECTION_FROM_RED")

    /** Casts Bathe in Light at [target] and answers the colour prompt with red. */
    fun GameTestDriver.bathe(caster: EntityId, target: EntityId) {
        giveMana(caster, Color.WHITE, 2)
        val spell = putCardInHand(caster, "Bathe in Light")
        castSpellWithTargets(caster, spell, listOf(ChosenTarget.Permanent(target))).isSuccess shouldBe true
        var guard = 0
        while (stackSize > 0 && pendingDecision == null && guard++ < 10) bothPass()
        val decision = pendingDecision ?: error("Expected a colour-choice decision from Bathe in Light")
        submitDecision(caster, ColorChosenResponse(decision.id, Color.RED))
        guard = 0
        while (stackSize > 0 && guard++ < 10) bothPass()
    }

    test("the target and every other creature sharing a colour with it gain protection from the chosen colour") {
        val d = driver()
        val me = d.player1
        val opp = d.player2

        val lions = d.putCreatureOnBattlefield(me, "Savannah Lions")        // {W} 2/1 — the target
        val otherWhite = d.putCreatureOnBattlefield(opp, "Savannah Lions")  // the opponent's, still hit
        val bears = d.putCreatureOnBattlefield(opp, "Grizzly Bears")        // {1}{G} — shares nothing
        val golem = d.putCreatureOnBattlefield(opp, "Artifact Creature")    // colorless — shares nothing

        d.bathe(me, lions)

        withClue("the target itself is protected from the chosen colour") {
            d.hasProtectionFromRed(lions) shouldBe true
        }
        withClue("radiance reaches the opponent's white creature too") {
            d.hasProtectionFromRed(otherWhite) shouldBe true
        }
        withClue("creatures sharing no colour with the target are untouched") {
            d.hasProtectionFromRed(bears) shouldBe false
            d.hasProtectionFromRed(golem) shouldBe false
        }
    }

    test("a colorless target shares a colour with nothing, so only it is protected") {
        val d = driver()
        val me = d.player1
        val opp = d.player2

        val golem = d.putCreatureOnBattlefield(me, "Artifact Creature")     // the target
        val otherGolem = d.putCreatureOnBattlefield(opp, "Artifact Creature")
        val lions = d.putCreatureOnBattlefield(opp, "Savannah Lions")

        d.bathe(me, golem)

        withClue("the colorless target is still protected") {
            d.hasProtectionFromRed(golem) shouldBe true
        }
        withClue("colorless creatures don't share a colour even with each other") {
            d.hasProtectionFromRed(otherGolem) shouldBe false
        }
        d.hasProtectionFromRed(lions) shouldBe false
    }
})
