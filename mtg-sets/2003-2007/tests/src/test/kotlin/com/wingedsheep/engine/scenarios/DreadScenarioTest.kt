package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.LibraryShuffledEvent
import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.matchers.shouldBe

class DreadScenarioTest : ScenarioTestBase() {
    init {
        fun base() = scenario().withPlayers("P1", "P2")
            .withActivePlayer(1).inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)

        test("destroyed Dread shuffles into its owner's library") {
            val game = base().withCardOnBattlefield(1, "Dread")
                .withCardInHand(1, "Terminate")
                .withLandsOnBattlefield(1, "Swamp", 1)
                .withLandsOnBattlefield(1, "Mountain", 1).build()
            game.castSpell(1, "Terminate", game.findPermanent("Dread")!!).error shouldBe null
            game.resolveStack()
            game.findCardsInLibrary(1, "Dread").size shouldBe 1
            game.isInGraveyard(1, "Dread") shouldBe false
        }

        test("Lignify cannot suppress the ability that triggers from the graveyard") {
            val game = base().withCardOnBattlefield(1, "Dread")
                .withCardInHand(1, "Lignify").withCardInHand(1, "Terminate")
                .withLandsOnBattlefield(1, "Forest", 2)
                .withLandsOnBattlefield(1, "Swamp", 1)
                .withLandsOnBattlefield(1, "Mountain", 1).build()
            val dread = game.findPermanent("Dread")!!
            game.castSpell(1, "Lignify", dread).error shouldBe null
            game.resolveStack()
            game.castSpell(1, "Terminate", dread).error shouldBe null
            game.resolveStack()
            game.findCardsInLibrary(1, "Dread").size shouldBe 1
        }

        test("milling Dread triggers its shuffle") {
            val game = base().withCardInLibrary(1, "Dread")
                .withCardInHand(1, "Tome Scour")
                .withLandsOnBattlefield(1, "Island", 1).build()
            game.castSpellTargetingPlayer(1, "Tome Scour", 1).error shouldBe null
            game.resolveStack()
            game.findCardsInLibrary(1, "Dread").size shouldBe 1
        }

        test("discarding Dread triggers its shuffle") {
            val game = base().withCardInHand(1, "Dread").withCardInHand(1, "Mind Rot")
                .withLandsOnBattlefield(1, "Swamp", 3).build()
            game.castSpellTargetingPlayer(1, "Mind Rot", 1).error shouldBe null
            game.resolveStack()
            game.findCardsInLibrary(1, "Dread").size shouldBe 1
        }

        test("countering Dread triggers the shuffle from the stack") {
            val game = base().withCardInHand(1, "Dread").withCardInHand(2, "Counterspell")
                .withLandsOnBattlefield(1, "Swamp", 6)
                .withLandsOnBattlefield(2, "Island", 2).build()
            game.castSpell(1, "Dread").error shouldBe null
            game.passPriority().error shouldBe null
            game.castSpellTargetingStackSpell(2, "Counterspell", "Dread").error shouldBe null
            game.resolveStack()
            game.findCardsInLibrary(1, "Dread").size shouldBe 1
        }

        test("exiling Dread in response still shuffles but leaves Dread exiled") {
            val game = base().withCardOnBattlefield(1, "Dread")
                .withCardInHand(1, "Terminate").withCardInHand(1, "Cremate")
                .withLandsOnBattlefield(1, "Swamp", 2)
                .withLandsOnBattlefield(1, "Mountain", 1)
                .withCardInLibrary(1, "Forest").withCardInLibrary(1, "Island").build()
            game.castSpell(1, "Terminate", game.findPermanent("Dread")!!).error shouldBe null
            game.passPriority().error shouldBe null
            game.passPriority().error shouldBe null
            game.isInGraveyard(1, "Dread") shouldBe true
            game.state.stack.size shouldBe 1
            game.castSpellTargetingGraveyardCard(1, "Cremate", 1, "Dread").error shouldBe null
            val results = game.resolveStack()
            game.findCardsInLibrary(1, "Dread").size shouldBe 0
            game.isInGraveyard(1, "Dread") shouldBe false
            results.flatMap { it.events }.filterIsInstance<LibraryShuffledEvent>().size shouldBe 1
        }

        test("a graveyard replacement prevents the shuffle trigger") {
            val game = base().withCardOnBattlefield(1, "Dread")
                .withCardOnBattlefield(2, "Rest in Peace")
                .withCardInHand(1, "Terminate")
                .withLandsOnBattlefield(1, "Swamp", 1)
                .withLandsOnBattlefield(1, "Mountain", 1).build()
            game.castSpell(1, "Terminate", game.findPermanent("Dread")!!).error shouldBe null
            val results = game.resolveStack()
            game.findCardsInLibrary(1, "Dread").size shouldBe 0
            results.flatMap { it.events }.filterIsInstance<LibraryShuffledEvent>().size shouldBe 0
        }

        test("creature damage to you destroys the source without targeting") {
            val game = base().withCardOnBattlefield(1, "Dread")
                .withCardOnBattlefield(1, "Prodigal Sorcerer").build()
            game.execute(ActivateAbility(
                game.player1Id, game.findPermanent("Prodigal Sorcerer")!!,
                cardRegistry.getCard("Prodigal Sorcerer")!!.activatedAbilities.single().id,
                targets = listOf(ChosenTarget.Player(game.player1Id))
            )).error shouldBe null
            game.resolveStack()
            game.getLifeTotal(1) shouldBe 19
            game.isInGraveyard(1, "Prodigal Sorcerer") shouldBe true
        }

        test("creature damage to another player does not retaliate") {
            val game = base().withCardOnBattlefield(1, "Dread")
                .withCardOnBattlefield(1, "Prodigal Sorcerer").build()
            game.execute(ActivateAbility(
                game.player1Id, game.findPermanent("Prodigal Sorcerer")!!,
                cardRegistry.getCard("Prodigal Sorcerer")!!.activatedAbilities.single().id,
                targets = listOf(ChosenTarget.Player(game.player2Id))
            )).error shouldBe null
            game.resolveStack()
            game.getLifeTotal(2) shouldBe 19
            game.isInGraveyard(1, "Prodigal Sorcerer") shouldBe false
        }

        test("spell damage does not trigger retaliation") {
            val game = base().withCardOnBattlefield(1, "Dread")
                .withCardInHand(1, "Lightning Bolt")
                .withLandsOnBattlefield(1, "Mountain", 1).build()
            game.castSpellTargetingPlayer(1, "Lightning Bolt", 1).error shouldBe null
            game.resolveStack()
            game.getLifeTotal(1) shouldBe 17
            game.isInGraveyard(1, "Dread") shouldBe false
        }
    }
}
