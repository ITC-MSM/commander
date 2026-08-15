package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.mtg.sets.definitions.fdn.cards.SquadRallier
import io.kotest.matchers.shouldBe

/**
 * Squad Rallier — "{3}{W} 3/4. {2}{W}: Look at the top four cards of your library. You may reveal a
 * creature card with power 2 or less from among them and put it into your hand. Put the rest on the
 * bottom of your library in a random order."
 */
class SquadRallierScenarioTest : ScenarioTestBase() {

    private val abilityId = SquadRallier.activatedAbilities.first().id

    init {
        test("dig reveals a creature with power 2 or less to hand") {
            val game = scenario()
                .withPlayers()
                .withCardOnBattlefield(1, "Squad Rallier")
                .withLandsOnBattlefield(1, "Plains", 3)
                .withCardInLibrary(1, "Plains")
                .withCardInLibrary(1, "Bear Cub")   // 2/2 — power 2, eligible
                .withCardInLibrary(1, "Plains")
                .build()

            val rallier = game.findPermanent("Squad Rallier")!!
            val bearCub = game.findCardsInLibrary(1, "Bear Cub").first()

            game.execute(
                ActivateAbility(playerId = game.player1Id, sourceId = rallier, abilityId = abilityId)
            ).error shouldBe null
            game.resolveStack()

            // The pipeline pauses to let us choose the (optional) creature to keep.
            game.hasPendingDecision() shouldBe true
            game.selectCards(listOf(bearCub))
            game.resolveStack()

            game.isInHand(1, "Bear Cub") shouldBe true
        }
    }
}
