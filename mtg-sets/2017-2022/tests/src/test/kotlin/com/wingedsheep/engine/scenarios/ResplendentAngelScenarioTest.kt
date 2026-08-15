package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.ControllerComponent
import com.wingedsheep.engine.state.components.identity.TokenComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Resplendent Angel (M19 #34, reprinted in LCI #32).
 *
 * "{1}{W}{W} Creature — Angel 3/3
 *  Flying
 *  At the beginning of each end step, if you gained 5 or more life this turn, create a 4/4 white
 *  Angel creature token with flying and vigilance.
 *  {3}{W}{W}{W}: Until end of turn, this creature gets +2/+2 and gains lifelink."
 *
 * The token trigger is an intervening-"if": it only fires when the controller gained 5+ life this
 * turn, and re-checks on resolution. Covers both branches plus the lifelink pump.
 */
class ResplendentAngelScenarioTest : ScenarioTestBase() {

    // Minimal sorceries that gain the caster life, used to drive the "gained N life this turn" tracker.
    private val gainFiveLife = card("Test Gain Five") {
        manaCost = "{W}"
        typeLine = "Sorcery"
        spell { effect = Effects.GainLife(5) }
    }
    private val gainThreeLife = card("Test Gain Three") {
        manaCost = "{W}"
        typeLine = "Sorcery"
        spell { effect = Effects.GainLife(3) }
    }

    init {
        cardRegistry.register(gainFiveLife)
        cardRegistry.register(gainThreeLife)

        context("Resplendent Angel") {

            test("gaining 5 life this turn creates a 4/4 flying, vigilance Angel token at the end step") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Resplendent Angel", summoningSickness = false)
                    .withCardInHand(1, "Test Gain Five")
                    .withLandsOnBattlefield(1, "Plains", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val tokensBefore = creatureTokens(game.state, game.player1Id)

                val cast = game.castSpell(1, "Test Gain Five")
                withClue("Casting the life-gain spell should succeed: ${cast.error}") {
                    cast.error shouldBe null
                }
                game.resolveStack()

                game.passUntilPhase(Phase.ENDING, Step.END)
                game.resolveStack()

                val newTokens = creatureTokens(game.state, game.player1Id) - tokensBefore
                withClue("Gaining 5 life should create exactly one Angel token at the end step") {
                    newTokens.size shouldBe 1
                }

                val token = newTokens.first()
                val card = game.state.getEntity(token)!!.get<CardComponent>()!!
                withClue("Token is a 4/4 white Angel") {
                    game.state.projectedState.getPower(token) shouldBe 4
                    game.state.projectedState.getToughness(token) shouldBe 4
                    card.typeLine.subtypes.map { it.value } shouldBe listOf("Angel")
                }
                withClue("Token has flying and vigilance") {
                    game.state.projectedState.hasKeyword(token, Keyword.FLYING) shouldBe true
                    game.state.projectedState.hasKeyword(token, Keyword.VIGILANCE) shouldBe true
                }
            }

            test("gaining only 3 life this turn does not create a token (intervening-if fails)") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Resplendent Angel", summoningSickness = false)
                    .withCardInHand(1, "Test Gain Three")
                    .withLandsOnBattlefield(1, "Plains", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val tokensBefore = creatureTokens(game.state, game.player1Id)

                game.castSpell(1, "Test Gain Three")
                game.resolveStack()

                game.passUntilPhase(Phase.ENDING, Step.END)
                game.resolveStack()

                val newTokens = creatureTokens(game.state, game.player1Id) - tokensBefore
                withClue("Gaining only 3 life should not create a token") {
                    newTokens.size shouldBe 0
                }
            }

            test("{3}{W}{W}{W}: Resplendent Angel gets +2/+2 and gains lifelink until end of turn") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Resplendent Angel", summoningSickness = false)
                    .withLandsOnBattlefield(1, "Plains", 6) // pays {3}{W}{W}{W}
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val angel = game.findPermanent("Resplendent Angel")!!
                val abilityId = cardRegistry.getCard("Resplendent Angel")!!
                    .script.activatedAbilities[0].id

                val activate = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = angel,
                        abilityId = abilityId,
                    )
                )
                withClue("Activating the pump ability should succeed: ${activate.error}") {
                    activate.error shouldBe null
                }
                if (game.getPendingDecision() is com.wingedsheep.engine.core.SelectManaSourcesDecision) {
                    game.submitManaSourcesAutoPay()
                }
                game.resolveStack()

                withClue("Resplendent Angel is a 5/5 with lifelink after the pump") {
                    game.state.projectedState.getPower(angel) shouldBe 5
                    game.state.projectedState.getToughness(angel) shouldBe 5
                    game.state.projectedState.hasKeyword(angel, Keyword.LIFELINK) shouldBe true
                }
            }
        }
    }
}

private fun creatureTokens(state: GameState, player: EntityId): Set<EntityId> =
    state.getBattlefield().filter {
        val e = state.getEntity(it) ?: return@filter false
        e.has<TokenComponent>() &&
            e.get<ControllerComponent>()?.playerId == player &&
            e.get<CardComponent>()?.typeLine?.isCreature == true
    }.toSet()
