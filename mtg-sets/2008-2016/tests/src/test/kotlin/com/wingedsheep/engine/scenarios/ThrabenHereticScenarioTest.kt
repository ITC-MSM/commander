package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.handlers.continuations.entityIdToChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.mtg.sets.definitions.dka.cards.ThrabenHeretic
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class ThrabenHereticScenarioTest : ScenarioTestBase() {
    init {
        val abilityId = ThrabenHeretic.activatedAbilities.first().id

        test("tap ability exiles a creature card from a graveyard") {
            val game = scenario()
                .withPlayers("Player", "Opponent")
                .withCardOnBattlefield(1, "Thraben Heretic", summoningSickness = false)
                .withCardInGraveyard(1, "Grizzly Bears")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val heretic = game.findPermanent("Thraben Heretic")!!
            val gyCard = game.state.getZone(game.player1Id, Zone.GRAVEYARD).first()

            val result = game.execute(
                ActivateAbility(
                    playerId = game.player1Id,
                    sourceId = heretic,
                    abilityId = abilityId,
                    targets = listOf(entityIdToChosenTarget(game.state, gyCard)),
                ),
            )
            withClue("${result.error}") { result.error shouldBe null }
            game.resolveStack()

            game.state.getZone(game.player1Id, Zone.GRAVEYARD).isEmpty() shouldBe true
            game.findPermanent("Thraben Heretic") shouldNotBe null
        }
    }
}
