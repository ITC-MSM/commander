package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.SelectManaSourcesDecision
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Kaya, Geist Hunter (VOW #240, {1}{W}{B}, Loyalty 3).
 *
 *   +1: Creatures you control gain deathtouch until end of turn. Put a +1/+1 counter on up to one
 *       target creature token you control.
 *   −2: Until end of turn, if one or more tokens would be created under your control, twice that
 *       many of those tokens are created instead.
 *   −6: Exile all cards from all graveyards, then create a 1/1 white Spirit creature token with
 *       flying for each card exiled this way.
 *
 * The −2 is the load-bearing one: it is the first card to grant a *token-count* replacement effect
 * rather than print one, so it exercises the granted-replacement read path in
 * `TokenCreationReplacementHelper` end to end — including the three properties that fall out of
 * that channel and would each be wrong under a naive "static ability on Kaya" model:
 *
 *  - it stacks **multiplicatively** with a printed doubler (Doubling Season → ×4),
 *  - it **survives Kaya leaving the battlefield** (CR 611.2b — the ability has finished resolving),
 *  - it is **controller-scoped**, so an opponent's tokens are untouched,
 *  - and it **expires in the cleanup step**, not before and not after.
 */
class KayaGeistHunterScenarioTest : ScenarioTestBase() {

    init {
        context("the +1") {

            test("grants deathtouch to your creatures and grows the targeted token") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Kaya, Geist Hunter")
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withCardOnBattlefield(2, "Hill Giant", summoningSickness = false)
                    .withLandsOnBattlefield(1, "Plains", 2)
                    .withCardInHand(1, "Raise the Alarm")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                // A token to aim the counter at.
                castRaiseTheAlarm(game)
                val soldier = game.findPermanents("Soldier Token").first()

                val kaya = game.findPermanent("Kaya, Geist Hunter")!!
                activate(game, kaya, index = 0, targets = listOf(ChosenTarget.Permanent(soldier)))
                game.resolveStack()

                val bears = game.findPermanent("Grizzly Bears")!!
                withClue("your creatures gained deathtouch") {
                    game.state.projectedState.hasKeyword(bears, Keyword.DEATHTOUCH) shouldBe true
                    game.state.projectedState.hasKeyword(soldier, Keyword.DEATHTOUCH) shouldBe true
                }
                withClue("\"creatures you control\" does not reach the opponent's board") {
                    game.state.projectedState
                        .hasKeyword(game.findPermanent("Hill Giant")!!, Keyword.DEATHTOUCH) shouldBe false
                }
                withClue("the targeted token got the +1/+1 counter") {
                    counters(game, soldier, CounterType.PLUS_ONE_PLUS_ONE) shouldBe 1
                }
                withClue("+1 moved Kaya from 3 to 4 loyalty") { loyalty(game, kaya) shouldBe 4 }
            }

            test("resolves with no counter target — the token clause is \"up to one\"") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Kaya, Geist Hunter")
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val kaya = game.findPermanent("Kaya, Geist Hunter")!!
                activate(game, kaya, index = 0)
                game.resolveStack()

                withClue("the keyword half still happened") {
                    game.state.projectedState
                        .hasKeyword(game.findPermanent("Grizzly Bears")!!, Keyword.DEATHTOUCH) shouldBe true
                }
                withClue("+1 moved Kaya from 3 to 4 loyalty") { loyalty(game, kaya) shouldBe 4 }
            }
        }

        context("the −2 token doubler") {

            test("doubles a later token creation the same turn") {
                val game = kayaAndTwoPlains()

                minusTwo(game)
                castRaiseTheAlarm(game)

                withClue("two Soldiers doubled to four") {
                    game.findPermanents("Soldier Token").size shouldBe 4
                }
            }

            test("stacks multiplicatively with a printed doubler") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Kaya, Geist Hunter")
                    .withCardOnBattlefield(1, "Doubling Season")
                    .withLandsOnBattlefield(1, "Plains", 2)
                    .withCardInHand(1, "Raise the Alarm")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                minusTwo(game)
                castRaiseTheAlarm(game)

                withClue("2 × 2 (Doubling Season) × 2 (Kaya) = 8 Soldiers") {
                    game.findPermanents("Soldier Token").size shouldBe 8
                }
            }

            test("keeps doubling after Kaya has left the battlefield") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Kaya, Geist Hunter")
                    .withLandsOnBattlefield(1, "Plains", 2)
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withCardInHand(1, "Raise the Alarm")
                    .withCardInHand(1, "Lightning Bolt")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val kaya = game.findPermanent("Kaya, Geist Hunter")!!
                minusTwo(game)
                withClue("−2 left Kaya on 1 loyalty") { loyalty(game, kaya) shouldBe 1 }

                // Bolt her off the board; the replacement effect is a finished one-shot's product.
                game.castSpell(1, "Lightning Bolt", targetId = kaya).error shouldBe null
                if (game.getPendingDecision() is SelectManaSourcesDecision) game.submitManaSourcesAutoPay()
                game.resolveStack()
                game.checkStateBasedActions()
                withClue("Kaya is gone") { game.findPermanent("Kaya, Geist Hunter") shouldBe null }

                castRaiseTheAlarm(game)
                withClue("the grant outlives its source — still four Soldiers") {
                    game.findPermanents("Soldier Token").size shouldBe 4
                }
            }

            test("does not double an opponent's tokens") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Kaya, Geist Hunter")
                    .withLandsOnBattlefield(2, "Plains", 2)
                    .withCardInHand(2, "Raise the Alarm")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                minusTwo(game)

                game.passPriority()
                game.castSpell(2, "Raise the Alarm").error shouldBe null
                if (game.getPendingDecision() is SelectManaSourcesDecision) game.submitManaSourcesAutoPay()
                game.resolveStack()

                withClue("\"under your control\" is Kaya's controller, not the token maker") {
                    game.findPermanents("Soldier Token").size shouldBe 2
                }
            }

            test("wears off in the cleanup step") {
                val game = kayaAndTwoPlains()

                minusTwo(game)
                withClue("the grant is recorded while it lasts") {
                    game.state.grantedReplacementEffects.size shouldBe 1
                }

                game.passUntilPhase(Phase.POSTCOMBAT_MAIN, Step.POSTCOMBAT_MAIN)
                game.passUntilPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)

                withClue("\"until end of turn\" expired with the turn") {
                    game.state.grantedReplacementEffects shouldBe emptyList()
                }
            }
        }

        context("the −6") {

            test("exiles every graveyard and makes one flying Spirit per card exiled") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Kaya, Geist Hunter")
                    .withCardInGraveyard(1, "Grizzly Bears")
                    .withCardInGraveyard(1, "Lightning Bolt")
                    .withCardInGraveyard(2, "Hill Giant")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val kaya = game.findPermanent("Kaya, Geist Hunter")!!
                // 7, not 6: a Kaya who pays her last loyalty dies and lands in the very graveyard
                // the assertions below check.
                setLoyalty(game, kaya, 7)
                activate(game, kaya, index = 2)
                game.resolveStack()

                withClue("both graveyards are empty") {
                    game.graveyardSize(1) shouldBe 0
                    game.graveyardSize(2) shouldBe 0
                }
                withClue("noncreature cards are exiled too — this Kaya says \"all cards\"") {
                    game.isInExile(1, "Lightning Bolt") shouldBe true
                }
                withClue("three cards exiled, three Spirits") {
                    game.findPermanents("Spirit Token").size shouldBe 3
                }
                withClue("the Spirits fly") {
                    val spirit = game.findPermanents("Spirit Token").first()
                    game.state.projectedState.hasKeyword(spirit, Keyword.FLYING) shouldBe true
                }
            }
        }
    }

    /** Kaya on an otherwise empty board with the mana and card for a Raise the Alarm. */
    private fun kayaAndTwoPlains(): TestGame = scenario()
        .withPlayers("Player", "Opponent")
        .withCardOnBattlefield(1, "Kaya, Geist Hunter")
        .withLandsOnBattlefield(1, "Plains", 2)
        .withCardInHand(1, "Raise the Alarm")
        .withActivePlayer(1)
        .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
        .build()

    private fun minusTwo(game: TestGame) {
        val kaya = game.findPermanent("Kaya, Geist Hunter")!!
        activate(game, kaya, index = 1)
        game.resolveStack()
    }

    private fun castRaiseTheAlarm(game: TestGame) {
        game.castSpell(1, "Raise the Alarm").error shouldBe null
        if (game.getPendingDecision() is SelectManaSourcesDecision) game.submitManaSourcesAutoPay()
        game.resolveStack()
    }

    private fun activate(
        game: TestGame,
        source: EntityId,
        index: Int,
        targets: List<ChosenTarget> = emptyList(),
    ) {
        val ability = cardRegistry.getCard("Kaya, Geist Hunter")!!.script.activatedAbilities[index]
        game.execute(
            ActivateAbility(
                playerId = game.player1Id,
                sourceId = source,
                abilityId = ability.id,
                targets = targets,
            )
        ).error shouldBe null
    }

    private fun counters(game: TestGame, id: EntityId, type: CounterType): Int =
        game.state.getEntity(id)?.get<CountersComponent>()?.getCount(type) ?: 0

    private fun loyalty(game: TestGame, id: EntityId): Int = counters(game, id, CounterType.LOYALTY)

    private fun setLoyalty(game: TestGame, id: EntityId, amount: Int) {
        game.state = game.state.updateEntity(id) { c ->
            c.with(CountersComponent(mapOf(CounterType.LOYALTY to amount)))
        }
    }
}
