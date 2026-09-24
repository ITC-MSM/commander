package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseOptionDecision
import com.wingedsheep.engine.core.OptionChosenResponse
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Hunter's Axe (Reality Fracture #108) — {G} Artifact — Equipment:
 *   Equipped creature gets +2/+0 and has "Whenever this creature attacks, it gains your choice of
 *   trample or deathtouch until end of turn."
 *   Equip {2}
 */
class HuntersAxeScenarioTest : ScenarioTestBase() {

    private fun attackAndChoose(optionIndex: Int, expected: Keyword, other: Keyword) {
        val game = scenario()
            .withPlayers("Player1", "Player2")
            .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
            .withCardAttachedTo(1, "Hunter's Axe", "Grizzly Bears")
            .withCardInLibrary(1, "Forest")
            .withCardInLibrary(2, "Forest")
            .withActivePlayer(1)
            .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
            .build()

        val bears = game.findPermanent("Grizzly Bears")!!
        withClue("Grizzly Bears 2/2 + Hunter's Axe +2/+0 = 4/2") {
            game.state.projectedState.getPower(bears) shouldBe 4
            game.state.projectedState.getToughness(bears) shouldBe 2
        }

        game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
        game.declareAttackers(mapOf("Grizzly Bears" to 2)).error shouldBe null
        game.resolveStack()

        val decision = game.getPendingDecision()
        decision.shouldBeInstanceOf<ChooseOptionDecision>()
        decision.options shouldBe listOf("Trample", "Deathtouch")
        game.submitDecision(OptionChosenResponse(decision.id, optionIndex)).error shouldBe null
        game.resolveStack()

        game.state.projectedState.hasKeyword(bears, expected) shouldBe true
        game.state.projectedState.hasKeyword(bears, other) shouldBe false
    }

    init {
        test("attacking with the equipped creature grants trample when chosen") {
            attackAndChoose(0, Keyword.TRAMPLE, Keyword.DEATHTOUCH)
        }

        test("attacking with the equipped creature grants deathtouch when chosen") {
            attackAndChoose(1, Keyword.DEATHTOUCH, Keyword.TRAMPLE)
        }
    }
}
