package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectManaSourcesDecision
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Beast, Erudite Aerialist (Marvel Super Heroes #206).
 *
 * Beast ({3}{G/U}, 3/3, Legendary Mutant Scientist Hero):
 *   As long as you've put one or more +1/+1 counters on Beast this turn, he has flying.
 *   Whenever Beast deals combat damage to a player, draw a card.
 *
 * The conditional static is the *source-scoped* view of the counter-history predicate —
 * `Conditions.SourceReceivedCounterThisTurn` is `SourceMatches` over
 * `StatePredicate.ReceivedCounterThisTurn`, evaluated during state projection rather than at
 * resolution. It exercises both narrowing axes (kind and placer) and the "history, not presence"
 * reading that makes the grant outlive the counters themselves.
 *
 * Battlegrowth ({G} instant, "Put a +1/+1 counter on target creature") is the placement vehicle:
 * it targets any creature, so the same card drives both the you-placed and the opponent-placed
 * cases.
 */
class BeastEruditeAerialistScenarioTest : ScenarioTestBase() {

    private val beastName = "Beast, Erudite Aerialist"

    private fun hasFlying(game: TestGame): Boolean {
        val id = game.findPermanent(beastName) ?: error("$beastName not on battlefield")
        return game.state.projectedState.hasKeyword(id, Keyword.FLYING)
    }

    private fun plusOneCounters(game: TestGame): Int {
        val id = game.findPermanent(beastName) ?: error("$beastName not on battlefield")
        return game.state.getEntity(id)?.get<CountersComponent>()
            ?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0
    }

    /** Cast Battlegrowth from [caster]'s hand at Beast and let it resolve. */
    private fun growBeast(game: TestGame, caster: Int) {
        val beast = game.findPermanent(beastName) ?: error("$beastName not on battlefield")
        game.castSpell(caster, "Battlegrowth", targetId = beast).error shouldBe null
        if (game.getPendingDecision() is SelectManaSourcesDecision) game.submitManaSourcesAutoPay()
        game.resolveStack()
    }

    init {
        context("Beast, Erudite Aerialist — counter-history flying") {

            test("no flying before any counter is put on him") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, beastName)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                withClue("nothing has been put on Beast this turn") {
                    hasFlying(game) shouldBe false
                }
            }

            test("putting a +1/+1 counter on Beast gives him flying") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, beastName)
                    .withCardInHand(1, "Battlegrowth")
                    .withLandsOnBattlefield(1, "Forest", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                hasFlying(game) shouldBe false
                growBeast(game, caster = 1)

                withClue("Battlegrowth put the counter on") {
                    plusOneCounters(game) shouldBe 1
                }
                withClue("you put a +1/+1 counter on Beast this turn") {
                    hasFlying(game) shouldBe true
                }
            }

            test("flying survives the counters being removed — the grant reads history, not presence") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, beastName)
                    .withCardInHand(1, "Battlegrowth")
                    .withLandsOnBattlefield(1, "Forest", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                growBeast(game, caster = 1)
                hasFlying(game) shouldBe true

                // Strip the counters themselves; the per-turn placement marker stays.
                val beast = game.findPermanent(beastName)!!
                game.state = game.state.updateEntity(beast) { it.without<CountersComponent>() }

                withClue("no +1/+1 counter is on Beast any more") {
                    plusOneCounters(game) shouldBe 0
                }
                withClue("the card asks what you *put on* him this turn, not what is on him now") {
                    hasFlying(game) shouldBe true
                }
            }

            test("flying lapses next turn although the +1/+1 counter remains") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, beastName)
                    .withCardInHand(1, "Battlegrowth")
                    .withLandsOnBattlefield(1, "Forest", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                growBeast(game, caster = 1)
                hasFlying(game) shouldBe true

                game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP)

                withClue("the counter itself is untouched — only the turn history expired") {
                    plusOneCounters(game) shouldBe 1
                }
                withClue("nothing was put on Beast *this* turn") {
                    hasFlying(game) shouldBe false
                }
            }

            test("a counter an opponent put on Beast does not turn flying on") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, beastName)
                    .withCardInHand(2, "Battlegrowth")
                    .withLandsOnBattlefield(2, "Forest", 1)
                    .withActivePlayer(1)
                    .withPriorityPlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                growBeast(game, caster = 2)

                withClue("the opponent's Battlegrowth still put a counter on Beast") {
                    plusOneCounters(game) shouldBe 1
                }
                withClue("the grant reads '**you've** put', and you did not") {
                    hasFlying(game) shouldBe false
                }
            }
        }
    }
}
