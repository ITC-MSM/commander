package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.PlayLand
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Roiling Canopy (Reality Fracture #187) — Land:
 *   This land enters tapped.
 *   Whenever a Forest you control enters, if you control at least five other Forests, target
 *   creature you control gets +3/+3 until end of turn.
 *   {T}: Add {G}.
 *
 * "Other" is relative to the Forest that entered: the sixth Forest triggers it, the fifth doesn't.
 */
class RoilingCanopyScenarioTest : ScenarioTestBase() {

    private fun playForestWith(forestsAlready: Int): Pair<ScenarioTestBase.TestGame, Boolean> {
        val game = scenario()
            .withPlayers("Player1", "Player2")
            .withCardOnBattlefield(1, "Roiling Canopy")
            .withLandsOnBattlefield(1, "Forest", forestsAlready)
            .withCardOnBattlefield(1, "Grizzly Bears")
            .withCardInHand(1, "Forest")
            .withCardInLibrary(1, "Forest")
            .withCardInLibrary(2, "Forest")
            .withActivePlayer(1)
            .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
            .build()

        val forest = game.findCardsInHand(1, "Forest").first()
        game.execute(PlayLand(game.player1Id, forest)).error shouldBe null

        var triggered = game.state.stack.isNotEmpty()
        val pending = game.getPendingDecision()
        if (pending is ChooseTargetsDecision) {
            triggered = true
            game.selectTargets(listOf(game.findPermanent("Grizzly Bears")!!)).error shouldBe null
        }
        game.resolveStack()
        return game to triggered
    }

    init {
        test("a Forest entering alongside five other Forests pumps target creature you control") {
            val (game, triggered) = playForestWith(5)
            triggered shouldBe true
            val bears = game.findPermanent("Grizzly Bears")!!
            withClue("Grizzly Bears 2/2 gets +3/+3") {
                game.state.projectedState.getPower(bears) shouldBe 5
                game.state.projectedState.getToughness(bears) shouldBe 5
            }
        }

        test("the entering Forest leaving before resolution still leaves five other Forests") {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withCardOnBattlefield(1, "Roiling Canopy")
                .withLandsOnBattlefield(1, "Forest", 5)
                .withCardOnBattlefield(1, "Grizzly Bears")
                .withCardInHand(1, "Forest")
                .withCardInLibrary(1, "Forest")
                .withCardInLibrary(2, "Forest")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val forest = game.findCardsInHand(1, "Forest").first()
            game.execute(PlayLand(game.player1Id, forest)).error shouldBe null
            if (game.getPendingDecision() is ChooseTargetsDecision) {
                game.selectTargets(listOf(game.findPermanent("Grizzly Bears")!!)).error shouldBe null
            }
            game.state.stack.size shouldBe 1

            // The Forest that triggered the ability is destroyed in response.
            game.state = game.state.moveToZone(
                forest,
                ZoneKey(game.player1Id, Zone.BATTLEFIELD),
                ZoneKey(game.player1Id, Zone.GRAVEYARD)
            )
            game.resolveStack()

            val bears = game.findPermanent("Grizzly Bears")!!
            withClue("the intervening-if recheck counts the five Forests that remain as 'other' Forests") {
                game.state.projectedState.getPower(bears) shouldBe 5
            }
        }

        test("with only four other Forests the ability doesn't trigger") {
            val (game, triggered) = playForestWith(4)
            triggered shouldBe false
            val bears = game.findPermanent("Grizzly Bears")!!
            game.state.projectedState.getPower(bears) shouldBe 2
        }
    }
}
