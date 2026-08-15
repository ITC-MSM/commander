package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.PlayLand
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.mtg.sets.definitions.sos.cards.TabletOfDiscovery
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Tablet of Discovery:
 *   {2}{R} Artifact — "When this artifact enters, mill a card. You may play that card this turn."
 *
 * Regression for the bug "When I play Tablet of Discovery, I'm unable to play a land": the milled
 * card lands in the graveyard with a generic may-play permission, and the CastFromZoneEnumerator
 * offers a PlayLand action for it — but PlayLandHandler used to reject playing a *land* from the
 * graveyard unless the permission was Crucible/Muldrotha-style. A milled land must be playable.
 */
class TabletOfDiscoveryScenarioTest : ScenarioTestBase() {

    init {
        cardRegistry.register(TabletOfDiscovery)

        test("a milled land can be played this turn from the graveyard") {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withCardInHand(1, "Tablet of Discovery")
                .withCardInLibrary(1, "Forest") // sole library card -> milled to graveyard
                .withLandsOnBattlefield(1, "Mountain", 3) // pays {2}{R}
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            game.castSpell(1, "Tablet of Discovery").error shouldBe null
            game.resolveStack() // Tablet resolves, enters, ETB mills the Forest

            withClue("The milled Forest is now in the graveyard with a may-play grant") {
                game.isInGraveyard(1, "Forest") shouldBe true
            }

            val forestId = game.findCardsInGraveyard(1, "Forest").single()
            withClue("Playing the milled land from the graveyard succeeds") {
                game.execute(PlayLand(game.player1Id, forestId)).error shouldBe null
            }
            game.isOnBattlefield("Forest") shouldBe true
        }
    }
}
