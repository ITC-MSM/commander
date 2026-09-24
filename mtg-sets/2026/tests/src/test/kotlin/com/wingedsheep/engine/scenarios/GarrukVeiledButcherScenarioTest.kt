package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.scripting.AbilityCost
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Garruk, Veiled Butcher (FRA #229).
 *
 * - "If a creature an opponent controls would die, exile it instead."
 * - +2: Up to one target creature gets -4/-1 until your next turn.
 * - −2: Each player sacrifices a creature of their choice. If you sacrificed a creature this way,
 *   create a 4/4 green Beast creature token with trample.
 * - −3: Each opponent discards two cards. For each opponent who didn't discard two nonland cards
 *   this way, you draw a card.
 */
class GarrukVeiledButcherScenarioTest : ScenarioTestBase() {
    init {
        val abilities = cardRegistry.getCard("Garruk, Veiled Butcher")!!.script.activatedAbilities
        fun loyalty(change: Int) = abilities.single { (it.cost as? AbilityCost.Loyalty)?.change == change }.id

        fun base() = scenario().withPlayers()
            .withCardOnBattlefield(1, "Garruk, Veiled Butcher")
            .withCardInLibrary(1, "Swamp")
            .withCardInLibrary(1, "Swamp")
            .withCardInLibrary(2, "Swamp")
            .withActivePlayer(1)
            .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)

        test("an opponent's creature that would die is exiled instead; yours still dies") {
            val game = base()
                .withCardOnBattlefield(1, "Grizzly Bears")
                .withCardOnBattlefield(2, "Hill Giant")
                .withCardsInHand(1, "Lightning Bolt", 2)
                .withLandsOnBattlefield(1, "Mountain", 2)
                .build()

            game.castSpell(1, "Lightning Bolt", game.findPermanent("Hill Giant")!!).error shouldBe null
            game.resolveStack()
            game.isInExile(2, "Hill Giant") shouldBe true
            game.isInGraveyard(2, "Hill Giant") shouldBe false

            game.castSpell(1, "Lightning Bolt", game.findPermanent("Grizzly Bears")!!).error shouldBe null
            game.resolveStack()
            game.isInGraveyard(1, "Grizzly Bears") shouldBe true
        }

        test("+2 gives up to one target creature -4/-1") {
            val game = base().withCardOnBattlefield(2, "Serra Angel").build()
            val garruk = game.findPermanent("Garruk, Veiled Butcher")!!
            val angel = game.findPermanent("Serra Angel")!!

            game.execute(
                ActivateAbility(game.player1Id, garruk, loyalty(2), targets = listOf(ChosenTarget.Permanent(angel)))
            ).error shouldBe null
            game.resolveStack()

            game.state.projectedState.getPower(angel) shouldBe 0
            game.state.projectedState.getToughness(angel) shouldBe 3
        }

        test("+2 can be activated with no target") {
            val game = base().build()
            val garruk = game.findPermanent("Garruk, Veiled Butcher")!!
            game.execute(ActivateAbility(game.player1Id, garruk, loyalty(2))).error shouldBe null
            game.resolveStack()
            game.isOnBattlefield("Garruk, Veiled Butcher") shouldBe true
        }

        test("−2: each player sacrifices a creature; you get a Beast; the opponent's is exiled") {
            val game = base()
                .withCardOnBattlefield(1, "Grizzly Bears")
                .withCardOnBattlefield(2, "Hill Giant")
                .build()
            val garruk = game.findPermanent("Garruk, Veiled Butcher")!!

            game.execute(ActivateAbility(game.player1Id, garruk, loyalty(-2))).error shouldBe null
            game.resolveStack()

            game.isInGraveyard(1, "Grizzly Bears") shouldBe true
            game.isInExile(2, "Hill Giant") shouldBe true
            val beast = game.findPermanent("Beast Token")!!
            game.state.projectedState.getPower(beast) shouldBe 4
            game.state.projectedState.getToughness(beast) shouldBe 4
            game.state.projectedState.hasKeyword(beast, Keyword.TRAMPLE) shouldBe true
        }

        test("−2 without a creature of your own makes no Beast") {
            val game = base().withCardOnBattlefield(2, "Hill Giant").build()
            val garruk = game.findPermanent("Garruk, Veiled Butcher")!!

            game.execute(ActivateAbility(game.player1Id, garruk, loyalty(-2))).error shouldBe null
            game.resolveStack()

            game.isInExile(2, "Hill Giant") shouldBe true
            game.findPermanent("Beast Token") shouldBe null
        }

        fun discardGame() = base()
            .withCardInHand(2, "Lightning Bolt")
            .withCardInHand(2, "Grizzly Bears")
            .withCardInHand(2, "Forest")
            .build()

        test("−3: opponent discarding two nonland cards means no draw") {
            val game = discardGame()
            val garruk = game.findPermanent("Garruk, Veiled Butcher")!!
            val handBefore = game.handSize(1)

            game.execute(ActivateAbility(game.player1Id, garruk, loyalty(-3))).error shouldBe null
            game.resolveStack()
            withClue("the opponent chooses the discards") {
                game.getPendingDecision()!!.playerId shouldBe game.player2Id
            }
            game.selectCards(
                game.findCardsInHand(2, "Lightning Bolt") + game.findCardsInHand(2, "Grizzly Bears")
            ).error shouldBe null
            game.resolveStack()

            game.handSize(2) shouldBe 1
            game.handSize(1) shouldBe handBefore
        }

        test("−3: opponent discarding a land means you draw a card") {
            val game = discardGame()
            val garruk = game.findPermanent("Garruk, Veiled Butcher")!!
            val handBefore = game.handSize(1)

            game.execute(ActivateAbility(game.player1Id, garruk, loyalty(-3))).error shouldBe null
            game.resolveStack()
            game.selectCards(
                game.findCardsInHand(2, "Lightning Bolt") + game.findCardsInHand(2, "Forest")
            ).error shouldBe null
            game.resolveStack()

            game.handSize(2) shouldBe 1
            game.handSize(1) shouldBe handBefore + 1
        }

        test("−3: an opponent with fewer than two cards in hand means you draw a card") {
            val game = base().withCardInHand(2, "Lightning Bolt").build()
            val garruk = game.findPermanent("Garruk, Veiled Butcher")!!
            val handBefore = game.handSize(1)

            game.execute(ActivateAbility(game.player1Id, garruk, loyalty(-3))).error shouldBe null
            game.resolveStack()
            if (game.hasPendingDecision()) {
                game.selectCards(game.findCardsInHand(2, "Lightning Bolt")).error shouldBe null
                game.resolveStack()
            }

            game.handSize(2) shouldBe 0
            game.handSize(1) shouldBe handBefore + 1
        }
    }
}
