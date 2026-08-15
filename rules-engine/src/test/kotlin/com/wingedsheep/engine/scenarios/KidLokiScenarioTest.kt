package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Scenario tests for Kid Loki (Marvel Super Heroes #63).
 *
 * Kid Loki ({U}, 1/1, Legendary God Hero Villain):
 *   Each creature you control that you've put one or more +1/+1 counters on this turn has hexproof.
 *   Whenever you draw your second card each turn, put a +1/+1 counter on Kid Loki.
 *
 * Exercises the filter-level `StatePredicate.ReceivedCounterThisTurn` as a group static: the grant
 * follows the turn's counter *history*, not the counters currently on the board, and resets at
 * end-of-turn cleanup.
 */
class KidLokiScenarioTest : ScenarioTestBase() {

    private fun plusOneCounters(game: TestGame, name: String): Int {
        val id = game.findPermanent(name) ?: error("$name not on battlefield")
        return game.state.getEntity(id)?.get<CountersComponent>()
            ?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0
    }

    private fun hasHexproof(game: TestGame, name: String): Boolean {
        val id = game.findPermanent(name) ?: error("$name not on battlefield")
        return game.state.projectedState.hasKeyword(id, Keyword.HEXPROOF)
    }

    init {
        context("Kid Loki — second-draw counter and the counter-history hexproof grant") {

            test("no creature has hexproof before any counter is placed") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Kid Loki")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                withClue("Kid Loki has had no counter put on it this turn") {
                    hasHexproof(game, "Kid Loki") shouldBe false
                }
                withClue("Grizzly Bears has had no counter put on it this turn") {
                    hasHexproof(game, "Grizzly Bears") shouldBe false
                }
            }

            test("drawing a second card puts a +1/+1 counter on Kid Loki and gives it hexproof") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Kid Loki")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardInHand(1, "Divination")
                    .withLandsOnBattlefield(1, "Island", 3)
                    .withCardInLibrary(1, "Island")
                    .withCardInLibrary(1, "Island")
                    .withCardsDrawnThisTurn(1, 0)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                // Divination draws two cards; the 2nd crosses N=2 and fires the trigger once.
                game.castSpell(1, "Divination").error shouldBe null
                game.resolveStack()

                withClue("the second draw of the turn adds exactly one +1/+1 counter") {
                    plusOneCounters(game, "Kid Loki") shouldBe 1
                }
                withClue("Kid Loki put a +1/+1 counter on itself, so it protects itself") {
                    hasHexproof(game, "Kid Loki") shouldBe true
                }
                withClue("Grizzly Bears received no counter, so it stays unprotected") {
                    hasHexproof(game, "Grizzly Bears") shouldBe false
                }
            }

            test("hexproof lapses on the following turn, when the counter history is cleared") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Kid Loki")
                    .withCardInHand(1, "Divination")
                    .withLandsOnBattlefield(1, "Island", 3)
                    .withCardInLibrary(1, "Island")
                    .withCardInLibrary(1, "Island")
                    .withCardsDrawnThisTurn(1, 0)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Divination").error shouldBe null
                game.resolveStack()
                hasHexproof(game, "Kid Loki") shouldBe true

                // Advance into the opponent's turn: cleanup wipes the per-turn marker even though
                // the +1/+1 counter itself stays on the permanent.
                game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP)

                withClue("the +1/+1 counter is still there — only the turn history expired") {
                    plusOneCounters(game, "Kid Loki") shouldBe 1
                }
                withClue("nothing was put on Kid Loki *this* turn, so hexproof is gone") {
                    hasHexproof(game, "Kid Loki") shouldBe false
                }
            }

            test("a creature that entered with a +1/+1 counter this turn has hexproof (CR 122.6a)") {
                // District Mascot ({G}, 0/0) enters with a +1/+1 counter on it. CR 122.6a makes its
                // controller the player who put that counter on it, so it satisfies "you've put one
                // or more +1/+1 counters on this turn" from the moment it enters.
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Kid Loki")
                    .withCardInHand(1, "District Mascot")
                    .withLandsOnBattlefield(1, "Forest", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "District Mascot").error shouldBe null
                game.resolveStack()

                withClue("District Mascot entered with its +1/+1 counter") {
                    plusOneCounters(game, "District Mascot") shouldBe 1
                }
                withClue("counters it entered with were put on it by you this turn") {
                    hasHexproof(game, "District Mascot") shouldBe true
                }
                withClue("Kid Loki itself received nothing, so it is not protected") {
                    hasHexproof(game, "Kid Loki") shouldBe false
                }
            }

            test("creatures an opponent controls are never covered by the grant") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Kid Loki")
                    .withCardInHand(2, "District Mascot")
                    .withLandsOnBattlefield(2, "Forest", 2)
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(2, "District Mascot").error shouldBe null
                game.resolveStack()

                withClue("the opponent's Mascot entered with a +1/+1 counter") {
                    plusOneCounters(game, "District Mascot") shouldBe 1
                }
                withClue("the grant is scoped to creatures *you* control") {
                    hasHexproof(game, "District Mascot") shouldBe false
                }
            }

            test("a creature you made explore is covered by the counter it gets (CR 122.6)") {
                // Cenote Scout ({G}, 1/1) explores as it enters. A nonland reveal puts a +1/+1
                // counter on it, placed by the exploring player — so it is a creature you control
                // that you've put a +1/+1 counter on this turn. Explore records the placement
                // through the same marker the enters-with and resolution paths use.
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Kid Loki")
                    .withCardInHand(1, "Cenote Scout")
                    .withLandsOnBattlefield(1, "Forest", 1)
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Cenote Scout").error shouldBe null
                // Scout enters, its ETB explore reveals the nonland and pauses on the
                // top-or-graveyard choice after the counter has already been placed.
                game.resolveStack()
                game.getPendingDecision().shouldNotBeNull()
                game.answerYesNo(false).error shouldBe null
                game.resolveStack()

                withClue("the nonland reveal put a +1/+1 counter on the explorer") {
                    plusOneCounters(game, "Cenote Scout") shouldBe 1
                }
                withClue("you put that counter on it, so Kid Loki protects it") {
                    hasHexproof(game, "Cenote Scout") shouldBe true
                }
            }

            test("the granted hexproof actually stops an opponent's removal spell") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Kid Loki")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardInHand(1, "Divination")
                    .withLandsOnBattlefield(1, "Island", 3)
                    .withCardInLibrary(1, "Island")
                    .withCardInLibrary(1, "Island")
                    .withCardsDrawnThisTurn(1, 0)
                    .withCardsInHand(2, "Lightning Bolt", 2)
                    .withLandsOnBattlefield(2, "Mountain", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Divination").error shouldBe null
                game.resolveStack()

                val kidLoki = game.findPermanent("Kid Loki")!!
                val bears = game.findPermanent("Grizzly Bears")!!

                // Hand priority to the opponent so their instants are castable.
                if (game.state.priorityPlayerId != game.player2Id) game.passPriority()

                withClue("hexproof makes Kid Loki an illegal target for the opponent") {
                    game.castSpell(2, "Lightning Bolt", targetId = kidLoki).error shouldNotBe null
                }
                withClue("the bystander received no counter, so it is still targetable") {
                    game.castSpell(2, "Lightning Bolt", targetId = bears).error shouldBe null
                }
            }
        }
    }
}
