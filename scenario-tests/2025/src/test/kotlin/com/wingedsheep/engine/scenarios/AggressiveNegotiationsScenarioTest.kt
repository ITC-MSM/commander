package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Aggressive Negotiations (TDM #70).
 *
 * {2}{B} Sorcery.
 *   "Target opponent reveals their hand. You choose a nonland card from it and exile that card.
 *    Put a +1/+1 counter on up to one target creature you control."
 *
 * Composed from existing primitives (RevealHand → Gather nonland → Select one → exile, then
 * AddCounters on an optional creature target). The two scenarios prove (1) the happy path where
 * a nonland card is exiled and a counter is placed, and (2) declining the optional creature
 * target still strips a card but places no counter.
 */
class AggressiveNegotiationsScenarioTest : ScenarioTestBase() {

    init {
        context("reveal, exile a nonland card, and put a +1/+1 counter") {

            test("exiles the chosen nonland and buffs your creature") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Aggressive Negotiations")
                    .withLandsOnBattlefield(1, "Swamp", 3)
                    .withCardOnBattlefield(1, "Glory Seeker")
                    .withCardInHand(2, "Hill Giant") // nonland — the only legal exile choice
                    .withCardInHand(2, "Swamp")       // land — not a legal choice
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val myCreature = game.findPermanent("Glory Seeker")!!
                val cardId = game.state.getHand(game.player1Id).first {
                    game.state.getEntity(it)?.get<CardComponent>()?.name == "Aggressive Negotiations"
                }

                // Target the opponent (index 0) and the optional creature you control (index 1).
                val cast = game.execute(
                    CastSpell(
                        game.player1Id,
                        cardId,
                        listOf(
                            ChosenTarget.Player(game.player2Id),
                            ChosenTarget.Permanent(myCreature),
                        ),
                    ),
                )
                withClue("Casting Aggressive Negotiations should succeed: ${cast.error}") {
                    cast.error shouldBe null
                }
                game.resolveStack()

                // The controller chooses the only nonland card to exile.
                if (game.hasPendingDecision()) {
                    val giant = game.state.getHand(game.player2Id).first {
                        game.state.getEntity(it)?.get<CardComponent>()?.name == "Hill Giant"
                    }
                    game.selectCards(listOf(giant))
                    game.resolveStack()
                }

                withClue("Hill Giant is exiled from the opponent's hand") {
                    game.state.getExile(game.player2Id).mapNotNull {
                        game.state.getEntity(it)?.get<CardComponent>()?.name
                    } shouldBe listOf("Hill Giant")
                }
                withClue("The land stays in the opponent's hand") {
                    game.state.getZone(game.player2Id, Zone.HAND).mapNotNull {
                        game.state.getEntity(it)?.get<CardComponent>()?.name
                    } shouldBe listOf("Swamp")
                }
                withClue("Glory Seeker gets a +1/+1 counter") {
                    val counters = game.state.getEntity(myCreature)
                        ?.get<CountersComponent>()
                        ?.counters?.get(CounterType.PLUS_ONE_PLUS_ONE) ?: 0
                    counters shouldBe 1
                }
            }

            test("declining the optional creature target still exiles a card, places no counter") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Aggressive Negotiations")
                    .withLandsOnBattlefield(1, "Swamp", 3)
                    .withCardOnBattlefield(1, "Glory Seeker")
                    .withCardInHand(2, "Hill Giant")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val myCreature = game.findPermanent("Glory Seeker")!!
                val cardId = game.state.getHand(game.player1Id).first {
                    game.state.getEntity(it)?.get<CardComponent>()?.name == "Aggressive Negotiations"
                }

                // Provide only the mandatory opponent target — decline the "up to one" creature.
                val cast = game.execute(
                    CastSpell(game.player1Id, cardId, listOf(ChosenTarget.Player(game.player2Id))),
                )
                withClue("Declining the optional target is legal: ${cast.error}") {
                    cast.error shouldBe null
                }
                game.resolveStack()
                if (game.hasPendingDecision()) {
                    val giant = game.state.getHand(game.player2Id).first {
                        game.state.getEntity(it)?.get<CardComponent>()?.name == "Hill Giant"
                    }
                    game.selectCards(listOf(giant))
                    game.resolveStack()
                }

                withClue("Hill Giant is still exiled") {
                    game.state.getExile(game.player2Id).size shouldBe 1
                }
                withClue("No counter is placed when the optional target is declined") {
                    val counters = game.state.getEntity(myCreature)
                        ?.get<CountersComponent>()
                        ?.counters?.get(CounterType.PLUS_ONE_PLUS_ONE) ?: 0
                    counters shouldBe 0
                }
            }
        }
    }
}
