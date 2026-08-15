package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.handlers.continuations.entityIdToChosenTarget
import com.wingedsheep.engine.state.components.battlefield.SummoningSicknessComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.mtg.sets.definitions.avr.cards.NephaliaSmuggler
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class NephaliaSmugglerScenarioTest : ScenarioTestBase() {
    init {
        val abilityId = NephaliaSmuggler.activatedAbilities.first().id

        test("blinks another creature you control under your control") {
            val game = scenario()
                .withPlayers("P1", "P2")
                .withCardOnBattlefield(1, "Nephalia Smuggler", summoningSickness = false)
                .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                .withLandsOnBattlefield(1, "Island", 4)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val smuggler = game.findPermanent("Nephalia Smuggler")!!
            val bears = game.findPermanent("Grizzly Bears")!!
            game.state.getEntity(bears)!!.has<SummoningSicknessComponent>() shouldBe false

            val result = game.execute(
                ActivateAbility(
                    playerId = game.player1Id,
                    sourceId = smuggler,
                    abilityId = abilityId,
                    targets = listOf(entityIdToChosenTarget(game.state, bears)),
                ),
            )
            withClue("${result.error}") { result.error shouldBe null }
            game.resolveStack()

            game.findPermanent("Nephalia Smuggler") shouldNotBe null
            val blinked = game.findPermanent("Grizzly Bears")
            blinked shouldNotBe null
            // Fresh battlefield entry from exile → summoning sickness again.
            game.state.getEntity(blinked!!)!!.has<SummoningSicknessComponent>() shouldBe true
        }
    }
}
