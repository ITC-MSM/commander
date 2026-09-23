package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.matchers.shouldBe

/**
 * Overwrite the Multiverse (Reality Fracture #59) — {4}{B}{B} Sorcery:
 *   Exile all creatures. Empower Jace X, where X is the number of creatures exiled this way.
 *
 * X counts creatures on both sides; with no creatures the 0-loyalty Jace token empower Jace
 * creates is put into the graveyard by state-based actions (CR 704.5i).
 */
class OverwriteTheMultiverseScenarioTest : ScenarioTestBase() {
    init {
        test("exiles every creature and empowers Jace by the number exiled") {
            val game = scenario().withPlayers()
                .withCardInHand(1, "Overwrite the Multiverse")
                .withLandsOnBattlefield(1, "Swamp", 6)
                .withCardOnBattlefield(1, "Grizzly Bears")
                .withCardOnBattlefield(2, "Grizzly Bears")
                .withCardOnBattlefield(2, "Hill Giant")
                .withCardInLibrary(1, "Swamp")
                .withCardInLibrary(2, "Swamp")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()

            game.castSpell(1, "Overwrite the Multiverse").error shouldBe null
            game.resolveStack()

            game.isOnBattlefield("Grizzly Bears") shouldBe false
            game.isOnBattlefield("Hill Giant") shouldBe false
            game.isInExile(2, "Hill Giant") shouldBe true
            val jace = game.findPermanents("Jace").single()
            game.state.getEntity(jace)?.get<CountersComponent>()?.getCount(CounterType.LOYALTY) shouldBe 3
        }

        test("with no creatures the Jace token is created at 0 loyalty and dies") {
            val game = scenario().withPlayers()
                .withCardInHand(1, "Overwrite the Multiverse")
                .withLandsOnBattlefield(1, "Swamp", 6)
                .withCardInLibrary(1, "Swamp")
                .withCardInLibrary(2, "Swamp")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()

            game.castSpell(1, "Overwrite the Multiverse").error shouldBe null
            game.resolveStack()

            game.findPermanents("Jace") shouldBe emptyList()
        }
    }
}
