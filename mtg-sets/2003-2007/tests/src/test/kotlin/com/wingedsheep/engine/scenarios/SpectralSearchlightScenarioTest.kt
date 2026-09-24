package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.ChooseColorDecision
import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.ColorChosenResponse
import com.wingedsheep.engine.core.TargetsResponse
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.mtg.sets.definitions.rav.cards.SpectralSearchlight
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Spectral Searchlight (RAV #271) — "{T}: Choose a player. That player adds one mana of any color
 * they choose."
 *
 * A mana ability (nothing is targeted) whose two choices both belong to resolution: the activator
 * picks the player, and *that* player picks the color and gets the mana. Pinned here: the player
 * prompt offers everyone, the color prompt goes to the chosen player, the mana lands in the chosen
 * player's pool, choosing yourself works (ruling), a color the activator tries to pre-supply is
 * ignored, the ability never goes on the stack, and auto-pay never taps it.
 */
class SpectralSearchlightScenarioTest : ScenarioTestBase() {

    private fun board(withLionsInHand: Boolean = false): TestGame {
        val builder = scenario()
            .withPlayers("Activator", "Opponent")
            .withCardOnBattlefield(1, "Spectral Searchlight")
            .withCardInLibrary(1, "Plains")
            .withCardInLibrary(2, "Plains")
            .withActivePlayer(1)
            .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
        if (withLionsInHand) builder.withCardInHand(1, "Savannah Lions")
        return builder.build()
    }

    private fun TestGame.activate(preChosenColor: Color? = null) = execute(
        ActivateAbility(
            playerId = player1Id,
            sourceId = findPermanent("Spectral Searchlight")!!,
            abilityId = SpectralSearchlight.activatedAbilities.first { it.isManaAbility }.id,
            manaColorChoice = preChosenColor
        )
    )

    private fun TestGame.choosePlayer(player: EntityId) {
        val decision = getPendingDecision().shouldBeInstanceOf<ChooseTargetsDecision>()
        withClue("the activator chooses the player") { decision.playerId shouldBe player1Id }
        withClue("any player may be chosen, including the activator") {
            decision.legalTargets[0]!!.shouldContainExactlyInAnyOrder(player1Id, player2Id)
        }
        submitDecision(TargetsResponse(decision.id, mapOf(0 to listOf(player)))).error shouldBe null
    }

    private fun TestGame.pool(player: EntityId): ManaPoolComponent =
        state.getEntity(player)?.get<ManaPoolComponent>() ?: ManaPoolComponent()

    init {
        test("the chosen opponent picks the color and gets the mana") {
            val game = board()
            game.activate().error shouldBe null
            game.choosePlayer(game.player2Id)

            val colorDecision = game.getPendingDecision().shouldBeInstanceOf<ChooseColorDecision>()
            withClue("the color is chosen by the chosen player, not the activator") {
                colorDecision.playerId shouldBe game.player2Id
            }
            game.submitDecision(ColorChosenResponse(colorDecision.id, Color.RED)).error shouldBe null

            withClue("the red mana is in the opponent's pool") {
                game.pool(game.player2Id).red shouldBe 1
                game.pool(game.player2Id).total shouldBe 1
            }
            withClue("the activator gets nothing") { game.pool(game.player1Id).total shouldBe 0 }
            withClue("the Searchlight is tapped") {
                game.state.getEntity(game.findPermanent("Spectral Searchlight")!!)!!.has<TappedComponent>() shouldBe true
            }
            withClue("a mana ability never uses the stack") { game.state.stack.size shouldBe 0 }
            game.getPendingDecision() shouldBe null
        }

        test("choosing isn't targeting: a hexproof opponent and a shrouded you can both be chosen") {
            val game = board()
            game.state = game.state
                .updateEntity(game.player2Id) {
                    it.with(com.wingedsheep.engine.state.components.player.PlayerHexproofComponent())
                }
                .updateEntity(game.player1Id) {
                    it.with(com.wingedsheep.engine.state.components.player.PlayerShroudComponent())
                }
            game.activate().error shouldBe null
            // choosePlayer asserts both players are offered.
            game.choosePlayer(game.player2Id)

            val colorDecision = game.getPendingDecision().shouldBeInstanceOf<ChooseColorDecision>()
            colorDecision.playerId shouldBe game.player2Id
            game.submitDecision(ColorChosenResponse(colorDecision.id, Color.RED)).error shouldBe null
            game.pool(game.player2Id).red shouldBe 1
        }

        test("choosing yourself lets you pick the color") {
            val game = board()
            game.activate().error shouldBe null
            game.choosePlayer(game.player1Id)

            val colorDecision = game.getPendingDecision().shouldBeInstanceOf<ChooseColorDecision>()
            colorDecision.playerId shouldBe game.player1Id
            game.submitDecision(ColorChosenResponse(colorDecision.id, Color.GREEN)).error shouldBe null

            game.pool(game.player1Id).green shouldBe 1
            game.pool(game.player2Id).total shouldBe 0
        }

        test("a color pre-supplied by the activator is ignored — the chosen player still decides") {
            val game = board()
            game.activate(preChosenColor = Color.BLACK).error shouldBe null
            game.choosePlayer(game.player2Id)

            val colorDecision = game.getPendingDecision().shouldBeInstanceOf<ChooseColorDecision>()
            colorDecision.playerId shouldBe game.player2Id
            game.submitDecision(ColorChosenResponse(colorDecision.id, Color.BLUE)).error shouldBe null

            game.pool(game.player2Id).blue shouldBe 1
            game.pool(game.player2Id).black shouldBe 0
        }

        test("the legal action doesn't ask the activator for a color up front") {
            val game = board()
            val action = game.getLegalActions(1).single {
                val a = it.action
                a is ActivateAbility && a.sourceId == game.findPermanent("Spectral Searchlight")
            }
            action.requiresManaColorChoice shouldBe false
        }

        test("auto-pay never taps it — the player and color are real choices") {
            val game = board(withLionsInHand = true)
            val lions = game.findCardsInHand(1, "Savannah Lions").single()
            val cast = game.getLegalActions(1).firstOrNull {
                val a = it.action
                a is CastSpell && a.cardId == lions
            }
            withClue("with only the Searchlight, Savannah Lions isn't affordable by auto-pay") {
                (cast == null || !cast.isAffordable) shouldBe true
            }

            // Activating it by hand for yourself floats the {W} that pays for the Lions.
            game.activate().error shouldBe null
            game.choosePlayer(game.player1Id)
            val colorDecision = game.getPendingDecision().shouldBeInstanceOf<ChooseColorDecision>()
            game.submitDecision(ColorChosenResponse(colorDecision.id, Color.WHITE)).error shouldBe null
            game.castSpell(1, "Savannah Lions").error shouldBe null
            game.state.stack.size shouldNotBe 0
        }
    }
}
