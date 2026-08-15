package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Scenario tests for Vivien Reid (M19 #208, {3}{G}{G} planeswalker, loyalty 5).
 *
 *   +1: Look at the top four cards of your library. You may reveal a creature or land card from
 *       among them and put it into your hand. Put the rest on the bottom of your library in a
 *       random order.
 *   -3: Destroy target artifact, enchantment, or creature with flying.
 *   -8: You get an emblem with "Creatures you control get +2/+2 and have vigilance, trample,
 *       and indestructible."
 */
class VivienReidScenarioTest : ScenarioTestBase() {

    private fun loyalty(game: TestGame, id: EntityId): Int =
        game.state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.LOYALTY) ?: 0

    /** Seed a planeswalker's starting loyalty (withCardOnBattlefield doesn't stamp it). */
    private fun seedLoyalty(game: TestGame, id: EntityId, amount: Int) {
        game.state = game.state.updateEntity(id) { c ->
            c.with(CountersComponent().withAdded(CounterType.LOYALTY, amount))
        }
    }

    private fun ability(index: Int) =
        cardRegistry.getCard("Vivien Reid")!!.script.activatedAbilities[index]

    init {
        test("+1: reveal a creature card from the top four and put it into your hand") {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withCardOnBattlefield(1, "Vivien Reid")
                .withCardInLibrary(1, "Grizzly Bears")
                .withCardInLibrary(1, "Shock")
                .withCardInLibrary(1, "Shock")
                .withCardInLibrary(1, "Shock")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val vivien = game.findPermanent("Vivien Reid")!!
            seedLoyalty(game, vivien, 5)

            val bearInLibrary = game.state.getLibrary(game.player1Id).single { id ->
                game.state.getEntity(id)?.get<CardComponent>()?.name == "Grizzly Bears"
            }

            game.execute(
                ActivateAbility(playerId = game.player1Id, sourceId = vivien, abilityId = ability(0).id)
            ).error shouldBe null
            game.resolveStack()

            // Optional reveal: choose the creature card.
            withClue("the +1 pauses to let the player reveal a creature or land card") {
                game.hasPendingDecision() shouldBe true
            }
            game.selectCards(listOf(bearInLibrary))
            game.resolveStack()

            withClue("Vivien is at loyalty 6 after +1") { loyalty(game, vivien) shouldBe 6 }
            withClue("the revealed creature is now in hand") {
                game.isInHand(1, "Grizzly Bears") shouldBe true
            }
            withClue("the other three cards went to the bottom of the library") {
                game.state.getLibrary(game.player1Id).size shouldBe 3
            }
        }

        test("-3: destroy target creature with flying; a ground creature is unaffected") {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withCardOnBattlefield(1, "Vivien Reid")
                .withCardOnBattlefield(2, "Air Elemental")
                .withCardOnBattlefield(2, "Llanowar Elves")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val vivien = game.findPermanent("Vivien Reid")!!
            seedLoyalty(game, vivien, 5)
            val flyer = game.findPermanent("Air Elemental")!!

            game.execute(
                ActivateAbility(
                    playerId = game.player1Id,
                    sourceId = vivien,
                    abilityId = ability(1).id,
                    targets = listOf(ChosenTarget.Permanent(flyer))
                )
            ).error shouldBe null
            game.resolveStack()

            withClue("Vivien is at loyalty 2 after -3") { loyalty(game, vivien) shouldBe 2 }
            withClue("the flying creature is destroyed") {
                game.findPermanent("Air Elemental") shouldBe null
            }
            withClue("the ground creature (no flying) is not a legal target and survives") {
                game.findPermanent("Llanowar Elves") shouldNotBe null
            }
        }

        test("-3: the artifact clause of the union can also be destroyed") {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withCardOnBattlefield(1, "Vivien Reid")
                .withCardOnBattlefield(2, "Millstone")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val vivien = game.findPermanent("Vivien Reid")!!
            seedLoyalty(game, vivien, 5)
            val artifact = game.findPermanent("Millstone")!!

            game.execute(
                ActivateAbility(
                    playerId = game.player1Id,
                    sourceId = vivien,
                    abilityId = ability(1).id,
                    targets = listOf(ChosenTarget.Permanent(artifact))
                )
            ).error shouldBe null
            game.resolveStack()

            withClue("the targeted artifact is destroyed") {
                game.findPermanent("Millstone") shouldBe null
            }
        }

        test("-8: emblem grants +2/+2, vigilance, trample, and indestructible to your creatures") {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withCardOnBattlefield(1, "Vivien Reid")
                .withCardOnBattlefield(1, "Grizzly Bears")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val vivien = game.findPermanent("Vivien Reid")!!
            seedLoyalty(game, vivien, 8)
            val bear = game.findPermanent("Grizzly Bears")!!

            game.execute(
                ActivateAbility(playerId = game.player1Id, sourceId = vivien, abilityId = ability(2).id)
            ).error shouldBe null
            game.resolveStack()

            val projected = game.state.projectedState
            withClue("Grizzly Bears is a 4/4 from the emblem (+2/+2)") {
                projected.getPower(bear) shouldBe 4
                projected.getToughness(bear) shouldBe 4
            }
            withClue("…and has vigilance, trample, and indestructible") {
                projected.hasKeyword(bear, Keyword.VIGILANCE) shouldBe true
                projected.hasKeyword(bear, Keyword.TRAMPLE) shouldBe true
                projected.hasKeyword(bear, Keyword.INDESTRUCTIBLE) shouldBe true
            }
        }
    }
}
