package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.matchers.shouldBe

class BestialIncursionScenarioTest : ScenarioTestBase() {
    init {
        test("flashback creates a trampling Beast and exiles the spell") {
            val game = scenario().withPlayers().withCardInGraveyard(1, "Bestial Incursion")
                .withLandsOnBattlefield(1, "Forest", 6)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()
            val card = game.findCardsInGraveyard(1, "Bestial Incursion").single()
            val flashback = game.getLegalActions(1).map { it.action }.filterIsInstance<CastSpell>().first { it.cardId == card }
            game.execute(flashback).error shouldBe null
            game.resolveStack()
            val beast = game.findPermanent("Beast Token")!!
            game.state.projectedState.getPower(beast) shouldBe 4
            game.state.projectedState.getToughness(beast) shouldBe 4
            game.state.projectedState.hasKeyword(beast, Keyword.TRAMPLE) shouldBe true
            game.isInExile(1, "Bestial Incursion") shouldBe true
        }
    }
}
