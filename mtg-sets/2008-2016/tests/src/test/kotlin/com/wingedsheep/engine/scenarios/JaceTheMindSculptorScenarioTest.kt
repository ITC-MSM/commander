package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.ReorderLibraryDecision
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.mtg.sets.definitions.wwk.cards.JaceTheMindSculptor
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Jace, the Mind Sculptor (WWK #31; {2}{U}{U}, Loyalty 3).
 *
 *   +2: Look at the top card of target player's library. You may put that card on the bottom of
 *       that player's library.
 *   0: Draw three cards, then put two cards from your hand on top of your library in any order.
 *   −1: Return target creature to its owner's hand.
 *   −12: Exile all cards from target player's library, then that player shuffles their hand into
 *        their library.
 *
 * Three of the four abilities read a *targeted* player's zones, so what each test is really
 * checking is that `Player.ContextPlayer(0)` reached the right player's library — fateseal moving
 * an opponent's card, the ultimate emptying an opponent's library and refilling it from their hand.
 */
class JaceTheMindSculptorScenarioTest : ScenarioTestBase() {

    private val plusTwo = JaceTheMindSculptor.activatedAbilities[0].id
    private val zero = JaceTheMindSculptor.activatedAbilities[1].id
    private val minusOne = JaceTheMindSculptor.activatedAbilities[2].id
    private val minusTwelve = JaceTheMindSculptor.activatedAbilities[3].id

    init {
        context("Jace, the Mind Sculptor") {

            test("+2 fateseals the target player: their top card goes to the bottom of their library") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Jace, the Mind Sculptor")
                    .withCardInLibrary(2, "Grizzly Bears")
                    .withCardInLibrary(2, "Centaur Courser")
                    .withCardInLibrary(2, "Llanowar Elves")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val jace = game.findPermanent("Jace, the Mind Sculptor")!!
                seedLoyalty(game, jace, 3)
                val libraryBefore = game.state.getLibrary(game.player2Id)
                val topCard = libraryBefore.first()

                game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = jace,
                        abilityId = plusTwo,
                        targets = listOf(ChosenTarget.Player(game.player2Id)),
                    )
                ).error shouldBe null
                game.resolveStack()

                // Exactly one card is offered: the top card of the *target's* library.
                val look = game.getPendingDecision() as SelectCardsDecision
                look.options shouldBe listOf(topCard)
                look.maxSelections shouldBe 1
                game.selectCards(listOf(topCard))

                val libraryAfter = game.state.getLibrary(game.player2Id)
                withClue("same cards, the top one now on the bottom") {
                    libraryAfter.size shouldBe libraryBefore.size
                    libraryAfter.last() shouldBe topCard
                    libraryAfter.first() shouldBe libraryBefore[1]
                }
                loyalty(game, jace) shouldBe 5
            }

            test("+2 may decline: the card stays on top") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Jace, the Mind Sculptor")
                    .withCardInLibrary(2, "Grizzly Bears")
                    .withCardInLibrary(2, "Centaur Courser")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val jace = game.findPermanent("Jace, the Mind Sculptor")!!
                seedLoyalty(game, jace, 3)
                val libraryBefore = game.state.getLibrary(game.player2Id)

                game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = jace,
                        abilityId = plusTwo,
                        targets = listOf(ChosenTarget.Player(game.player2Id)),
                    )
                ).error shouldBe null
                game.resolveStack()
                game.skipSelection()

                game.state.getLibrary(game.player2Id) shouldBe libraryBefore
            }

            test("0 draws three and puts two cards from hand back on top of the library") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Jace, the Mind Sculptor")
                    .withCardsInHand(1, "Grizzly Bears", 2)
                    .withCardInLibrary(1, "Centaur Courser")
                    .withCardInLibrary(1, "Llanowar Elves")
                    .withCardInLibrary(1, "Savannah Lions")
                    .withCardInLibrary(1, "Phantom Warrior")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val jace = game.findPermanent("Jace, the Mind Sculptor")!!
                seedLoyalty(game, jace, 3)
                val libraryBefore = game.state.getLibrary(game.player1Id).size

                game.execute(
                    ActivateAbility(playerId = game.player1Id, sourceId = jace, abilityId = zero)
                ).error shouldBe null
                game.resolveStack()

                val putBack = game.getPendingDecision() as SelectCardsDecision
                putBack.minSelections shouldBe 2
                putBack.maxSelections shouldBe 2
                game.selectCards(putBack.options.take(2))
                if (game.getPendingDecision() is ReorderLibraryDecision) game.keepLibraryOrder()

                // Drew three, put two back: net +1 card in hand, net -1 in library.
                game.handSize(1) shouldBe 3
                game.state.getLibrary(game.player1Id).size shouldBe (libraryBefore - 1)
                loyalty(game, jace) shouldBe 3
            }

            test("−1 returns target creature to its owner's hand") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Jace, the Mind Sculptor")
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val jace = game.findPermanent("Jace, the Mind Sculptor")!!
                seedLoyalty(game, jace, 3)
                val bears = game.findPermanent("Grizzly Bears")!!

                game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = jace,
                        abilityId = minusOne,
                        targets = listOf(ChosenTarget.Permanent(bears)),
                    )
                ).error shouldBe null
                game.resolveStack()

                game.findPermanent("Grizzly Bears") shouldBe null
                game.handSize(2) shouldBe 1
                loyalty(game, jace) shouldBe 2
            }

            test("−12 exiles the target's library, then shuffles their hand into it") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Jace, the Mind Sculptor")
                    .withCardsInHand(2, "Grizzly Bears", 2)
                    .withCardInLibrary(2, "Centaur Courser")
                    .withCardInLibrary(2, "Llanowar Elves")
                    .withCardInLibrary(2, "Savannah Lions")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val jace = game.findPermanent("Jace, the Mind Sculptor")!!
                seedLoyalty(game, jace, 12)

                game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = jace,
                        abilityId = minusTwelve,
                        targets = listOf(ChosenTarget.Player(game.player2Id)),
                    )
                ).error shouldBe null
                game.resolveStack()

                // The three library cards are exiled; the two hand cards become the new library.
                game.state.getExile(game.player2Id).size shouldBe 3
                game.state.getLibrary(game.player2Id).size shouldBe 2
                game.handSize(2) shouldBe 0
                loyalty(game, jace) shouldBe 0
            }

            test("−12 against an empty hand leaves the target with an empty library") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Jace, the Mind Sculptor")
                    .withCardInLibrary(2, "Centaur Courser")
                    .withCardInLibrary(2, "Llanowar Elves")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val jace = game.findPermanent("Jace, the Mind Sculptor")!!
                seedLoyalty(game, jace, 12)

                game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = jace,
                        abilityId = minusTwelve,
                        targets = listOf(ChosenTarget.Player(game.player2Id)),
                    )
                ).error shouldBe null
                game.resolveStack()

                // 2020-08-07 ruling: nothing is shuffled in, and the library stays empty — the
                // target loses only when they next try to draw.
                game.state.getExile(game.player2Id).size shouldBe 2
                game.state.getLibrary(game.player2Id).size shouldBe 0
                game.state.gameOver shouldBe false
            }
        }
    }
}

/** The scenario builder skips the enters-with-loyalty rider, so loyalty is seeded explicitly. */
private fun seedLoyalty(game: ScenarioTestBase.TestGame, id: EntityId, amount: Int) {
    game.state = game.state.updateEntity(id) { c ->
        c.with(CountersComponent().withAdded(CounterType.LOYALTY, amount))
    }
}

private fun loyalty(game: ScenarioTestBase.TestGame, id: EntityId): Int =
    game.state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.LOYALTY) ?: 0
