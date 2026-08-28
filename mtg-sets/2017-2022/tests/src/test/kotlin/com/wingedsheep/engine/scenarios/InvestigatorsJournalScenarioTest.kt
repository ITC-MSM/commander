package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Investigator's Journal (VOW #258) — {2} Artifact — Book Clue.
 *
 *   This artifact enters with a number of suspect counters on it equal to the greatest number of
 *   creatures a player controls.
 *
 * The whole point of the enters-with amount is that it is a **per-player maximum**, not a table
 * total, so the boards below are deliberately lopsided: a `Player.Each` count of the same board
 * would give 5 where the card gives 3, and a `Player.You` count would give 2. Only
 * `GreatestAmongPlayers` reads 3.
 */
class InvestigatorsJournalScenarioTest : ScenarioTestBase() {

    init {
        context("Investigator's Journal") {

            test("enters with counters equal to the largest single player's creature count") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Investigator's Journal")
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withCardOnBattlefield(1, "Hill Giant", summoningSickness = false)
                    .withCardOnBattlefield(2, "Grizzly Bears", summoningSickness = false)
                    .withCardOnBattlefield(2, "Hill Giant", summoningSickness = false)
                    .withCardOnBattlefield(2, "Storm Crow", summoningSickness = false)
                    .withLandsOnBattlefield(1, "Plains", 4)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val card = game.findCardsInHand(1, "Investigator's Journal").first()
                game.execute(CastSpell(game.player1Id, card, emptyList())).error shouldBe null
                game.resolveStack()

                val journal = game.findPermanent("Investigator's Journal")!!
                withClue("opponent's 3 creatures beat my 2, and 3 is not the table's 5") {
                    game.state.getEntity(journal)?.get<CountersComponent>()
                    ?.getCount(CounterType.SUSPECT) ?: 0 shouldBe 3
                }
            }

            test("an empty board gives it no counters") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Investigator's Journal")
                    .withLandsOnBattlefield(1, "Plains", 4)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val card = game.findCardsInHand(1, "Investigator's Journal").first()
                game.execute(CastSpell(game.player1Id, card, emptyList())).error shouldBe null
                game.resolveStack()

                val journal = game.findPermanent("Investigator's Journal")!!
                withClue("no creatures anywhere is 0, not a crash and not a default of 1") {
                    game.state.getEntity(journal)?.get<CountersComponent>()
                    ?.getCount(CounterType.SUSPECT) ?: 0 shouldBe 0
                }
            }

            test("the {2}, {T}, remove-a-counter ability spends one counter to draw") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Investigator's Journal")
                    .withCardOnBattlefield(2, "Grizzly Bears", summoningSickness = false)
                    .withCardOnBattlefield(2, "Hill Giant", summoningSickness = false)
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Plains", 6)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val card = game.findCardsInHand(1, "Investigator's Journal").first()
                game.execute(CastSpell(game.player1Id, card, emptyList())).error shouldBe null
                game.resolveStack()

                val journal = game.findPermanent("Investigator's Journal")!!
                game.state.getEntity(journal)?.get<CountersComponent>()
                    ?.getCount(CounterType.SUSPECT) ?: 0 shouldBe 2

                val handBefore = game.handSize(1)
                val abilityId = cardRegistry.getCard("Investigator's Journal")!!
                    .script.activatedAbilities[0].id

                game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = journal,
                        abilityId = abilityId,
                    )
                ).error shouldBe null
                game.resolveStack()

                withClue("one counter paid, one card drawn, the Journal still on the battlefield") {
                    game.state.getEntity(journal)?.get<CountersComponent>()
                    ?.getCount(CounterType.SUSPECT) ?: 0 shouldBe 1
                    game.handSize(1) shouldBe handBefore + 1
                    game.isOnBattlefield("Investigator's Journal") shouldBe true
                }
            }

            test("the {2}, Sacrifice ability draws without needing a counter") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Investigator's Journal", summoningSickness = false)
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Plains", 4)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                // Placed directly, so the enters-with replacement never ran and it carries none —
                // which is the point: this mode's cost is the Journal itself, not a counter.
                val journal = game.findPermanent("Investigator's Journal")!!
                game.state.getEntity(journal)?.get<CountersComponent>()
                    ?.getCount(CounterType.SUSPECT) ?: 0 shouldBe 0

                val handBefore = game.handSize(1)
                val abilityId = cardRegistry.getCard("Investigator's Journal")!!
                    .script.activatedAbilities[1].id

                game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = journal,
                        abilityId = abilityId,
                    )
                ).error shouldBe null
                game.resolveStack()

                withClue("the sacrifice mode draws a card and puts the Journal away") {
                    game.handSize(1) shouldBe handBefore + 1
                    game.isOnBattlefield("Investigator's Journal") shouldBe false
                }
            }
        }
    }
}
