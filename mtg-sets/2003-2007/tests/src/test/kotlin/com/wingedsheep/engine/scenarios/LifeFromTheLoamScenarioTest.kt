package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import io.kotest.matchers.shouldBe

class LifeFromTheLoamScenarioTest : ScenarioTestBase() {
    init {
        fun base() = scenario().withPlayers("P1", "P2")
            .withActivePlayer(1).inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)

        test("dredge returns this card and mills its printed number instead of one draw") {
            val game = base().withCardInGraveyard(1, "Life from the Loam")
                .withCardInHand(1, "Inspiration")
                .withLandsOnBattlefield(1, "Island", 4)
                .withCardInLibrary(1, "Forest").withCardInLibrary(1, "Forest")
                .withCardInLibrary(1, "Forest").withCardInLibrary(1, "Forest")
                .withCardInLibrary(1, "Forest").withCardInLibrary(1, "Forest").build()
            game.castSpellTargetingPlayer(1, "Inspiration", 1).error shouldBe null
            game.resolveStack()
            (game.state.pendingDecision as YesNoDecision).context.sourceName shouldBe "Life from the Loam"
            game.answerYesNo(true).error shouldBe null
            game.resolveStack()
            game.isInHand(1, "Life from the Loam") shouldBe true
            game.findCardsInGraveyard(1, "Forest").size shouldBe 3
            game.state.getLibrary(game.player1Id).size shouldBe 2
            game.state.getHand(game.player1Id).size shouldBe 2
        }

        for (count in 0..3) {
            test("returns exactly $count chosen lands") {
                val game = base().withCardInHand(1, "Life from the Loam")
                    .withLandsOnBattlefield(1, "Forest", 2)
                    .withCardInGraveyard(1, "Forest").withCardInGraveyard(1, "Island")
                    .withCardInGraveyard(1, "Swamp").withCardInGraveyard(1, "Grizzly Bears")
                    .build()
                val lands = listOf("Forest", "Island", "Swamp").map {
                    game.findCardsInGraveyard(1, it).single()
                }
                game.execute(CastSpell(game.player1Id, game.state.getHand(game.player1Id).single(),
                    lands.take(count).map { ChosenTarget.Card(it, game.player1Id, Zone.GRAVEYARD) }
                )).error shouldBe null
                game.resolveStack()
                game.state.getHand(game.player1Id).toSet() shouldBe lands.take(count).toSet()
                game.isInGraveyard(1, "Grizzly Bears") shouldBe true
            }
        }

        test("cannot target a creature or an opponent's land") {
            for (name in listOf("Grizzly Bears", "Island")) {
                val game = base().withCardInHand(1, "Life from the Loam")
                    .withLandsOnBattlefield(1, "Forest", 2)
                    .withCardInGraveyard(1, "Grizzly Bears").withCardInGraveyard(2, "Island").build()
                val owner = if (name == "Island") game.player2Id else game.player1Id
                val id = game.state.getGraveyard(owner).single()
                val result = game.execute(CastSpell(game.player1Id, game.state.getHand(game.player1Id).single(),
                    listOf(ChosenTarget.Card(id, owner, Zone.GRAVEYARD))))
                (result.error != null) shouldBe true
            }
        }

        test("still returns legal lands when another target is exiled in response") {
            val game = base().withCardInHand(1, "Life from the Loam")
                .withLandsOnBattlefield(1, "Forest", 2)
                .withCardInGraveyard(1, "Forest").withCardInGraveyard(1, "Island")
                .withCardInHand(2, "Cremate").withLandsOnBattlefield(2, "Swamp", 1)
                .withCardInLibrary(2, "Swamp").build()
            val forest = game.findCardsInGraveyard(1, "Forest").single()
            val island = game.findCardsInGraveyard(1, "Island").single()
            game.execute(CastSpell(game.player1Id, game.state.getHand(game.player1Id).single(),
                listOf(forest, island).map { ChosenTarget.Card(it, game.player1Id, Zone.GRAVEYARD) }
            )).error shouldBe null
            game.passPriority()
            game.castSpellTargetingGraveyardCard(2, "Cremate", 1, "Forest").error shouldBe null
            game.resolveStack()
            game.isInHand(1, "Island") shouldBe true
            game.isInHand(1, "Forest") shouldBe false
            game.state.getZone(com.wingedsheep.engine.state.ZoneKey(game.player1Id, Zone.EXILE))
                .contains(forest) shouldBe true
        }
    }
}
