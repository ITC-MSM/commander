package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.matchers.shouldBe

class StingcasterMageScenarioTest : ScenarioTestBase() {
    init {
        test("enters trigger grants flashback to a graveyard instant") {
            val game = scenario().withPlayers().withCardInHand(1, "Stingcaster Mage")
                .withCardInGraveyard(1, "Shock").withCardOnBattlefield(2, "Grizzly Bears")
                .withLandsOnBattlefield(1, "Mountain", 3)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()
            val shock = game.findCardsInGraveyard(1, "Shock").single()
            game.castSpell(1, "Stingcaster Mage").error shouldBe null
            game.resolveStack()
            game.selectTargets(listOf(shock)).error shouldBe null
            game.resolveStack()
            game.state.projectedState.hasKeyword(game.findPermanent("Stingcaster Mage")!!, Keyword.HASTE) shouldBe true
            val flashback = game.getLegalActions(1).map { it.action }.filterIsInstance<CastSpell>()
                .first { it.cardId == shock }
            game.execute(flashback.copy(targets = listOf(ChosenTarget.Permanent(game.findPermanent("Grizzly Bears")!!)))).error shouldBe null
            game.resolveStack()
            game.isInGraveyard(2, "Grizzly Bears") shouldBe true
            game.isInExile(1, "Shock") shouldBe true
        }
    }
}
