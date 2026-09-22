package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.matchers.shouldBe

class SimulacrumShaperScenarioTest : ScenarioTestBase() {
    init {
        for (accept in listOf(true, false)) {
            test("optional basic land search accept=$accept") {
                val game = scenario().withPlayers().withCardInHand(1, "Simulacrum Shaper")
                    .withCardInLibrary(1, "Island").withCardInLibrary(1, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Forest", 3)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()
                val island = game.findCardsInLibrary(1, "Island").single()
                game.castSpell(1, "Simulacrum Shaper").error shouldBe null
                game.resolveStack()
                game.answerYesNo(accept).error shouldBe null
                if (accept) game.selectCards(listOf(island)).error shouldBe null
                game.resolveStack()
                game.isOnBattlefield("Island") shouldBe accept
                if (accept) game.state.getEntity(island)!!.has<TappedComponent>() shouldBe true
                game.hasPendingDecision() shouldBe false
            }
        }
        test("dying draws a card") {
            val game = scenario().withPlayers().withCardOnBattlefield(1, "Simulacrum Shaper")
                .withCardInLibrary(1, "Island").withCardInHand(2, "Shock")
                .withLandsOnBattlefield(2, "Mountain", 1).withActivePlayer(2)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()
            game.castSpell(2, "Shock", game.findPermanent("Simulacrum Shaper")!!).error shouldBe null
            game.resolveStack()
            game.isInHand(1, "Island") shouldBe true
        }
    }
}
