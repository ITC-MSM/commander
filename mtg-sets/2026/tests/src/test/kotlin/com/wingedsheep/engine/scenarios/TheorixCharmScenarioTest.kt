package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.matchers.shouldBe

class TheorixCharmScenarioTest : ScenarioTestBase() {
    init {
        test("gives a creature -2/-2") {
            val game = scenario().withPlayers()
                .withCardInHand(1, "Theorix Charm")
                .withCardOnBattlefield(2, "Grizzly Bears")
                .withLandsOnBattlefield(1, "Island", 1)
                .withLandsOnBattlefield(1, "Swamp", 1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()
            game.castSpellWithMode(1, "Theorix Charm", 1, game.findPermanent("Grizzly Bears")!!).error shouldBe null
            game.resolveStack()
            game.isInGraveyard(2, "Grizzly Bears") shouldBe true
        }

        test("mills three cards, then draws a card") {
            val game = scenario().withPlayers()
                .withCardInHand(1, "Theorix Charm")
                .withCardInLibrary(1, "Forest")
                .withCardInLibrary(1, "Forest")
                .withCardInLibrary(1, "Forest")
                .withCardInLibrary(1, "Island")
                .withCardInLibrary(1, "Island")
                .withLandsOnBattlefield(1, "Island", 1)
                .withLandsOnBattlefield(1, "Swamp", 1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()
            game.castSpellWithMode(1, "Theorix Charm", 2).error shouldBe null
            game.resolveStack()
            // Three milled cards plus the resolved charm.
            game.graveyardSize(1) shouldBe 4
            game.handSize(1) shouldBe 1
            game.librarySize(1) shouldBe 1
        }
    }
}
