package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

/**
 * Chandra, Torch of Defiance (KLD #110, {2}{R}{R}, Loyalty 4).
 *
 *   +1: Exile the top card of your library. You may cast that card. If you don't, Chandra deals 2
 *       damage to each opponent.
 *   +1: Add {R}{R}.
 *   −3: Chandra deals 4 damage to target creature.
 *   −7: You get an emblem with "Whenever you cast a spell, this emblem deals 5 damage to any target."
 */
class ChandraTorchOfDefianceScenarioTest : ScenarioTestBase() {

    init {
        context("the exile +1") {

            test("casting the exiled card during resolution, paying its cost, deals no damage") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Chandra, Torch of Defiance")
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Forest", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val chandra = game.findPermanent("Chandra, Torch of Defiance")!!
                activate(game, chandra, index = 0)
                game.resolveStack()

                withClue("the \"may cast\" prompt is offered during resolution") {
                    game.hasPendingDecision().shouldBeTrue()
                }
                game.answerYesNo(true)
                if (game.hasPendingDecision()) game.submitManaSourcesAutoPay()
                game.resolveStack()

                withClue("a creature card cast at sorcery speed mid-resolution — timing is ignored") {
                    game.isOnBattlefield("Grizzly Bears").shouldBeTrue()
                }
                withClue("the cost was paid") {
                    game.findPermanents("Forest").all { game.state.getEntity(it)!!.has<com.wingedsheep.engine.state.components.battlefield.TappedComponent>() }
                        .shouldBeTrue()
                }
                withClue("cast, so no damage") { game.getLifeTotal(2) shouldBe 20 }
                withClue("+1 moved Chandra from 4 to 5 loyalty") { loyalty(game, chandra) shouldBe 5 }
            }

            test("declining to cast deals 2 damage to each opponent and leaves the card in exile") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Chandra, Torch of Defiance")
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Forest", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val chandra = game.findPermanent("Chandra, Torch of Defiance")!!
                activate(game, chandra, index = 0)
                game.resolveStack()
                game.answerYesNo(false)
                game.resolveStack()

                game.getLifeTotal(2) shouldBe 18
                game.isInExile(1, "Grizzly Bears").shouldBeTrue()
                withClue("the card can't be cast later in the turn") {
                    game.getLegalActions(1).any {
                        it.actionType == "CastSpell" && it.description.contains("Grizzly Bears")
                    }.shouldBeFalse()
                }
            }

            test("agreeing to cast a card you can't pay for still deals the 2 damage") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Chandra, Torch of Defiance")
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val chandra = game.findPermanent("Chandra, Torch of Defiance")!!
                activate(game, chandra, index = 0)
                game.resolveStack()
                if (game.hasPendingDecision()) game.answerYesNo(true)
                game.resolveStack()

                game.isOnBattlefield("Grizzly Bears").shouldBeFalse()
                game.isInExile(1, "Grizzly Bears").shouldBeTrue()
                game.getLifeTotal(2) shouldBe 18
            }

            test("a land card can't be cast, so it always deals the 2 damage") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Chandra, Torch of Defiance")
                    .withCardInLibrary(1, "Mountain")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val chandra = game.findPermanent("Chandra, Torch of Defiance")!!
                activate(game, chandra, index = 0)
                game.resolveStack()

                withClue("no prompt for a land") { game.hasPendingDecision().shouldBeFalse() }
                game.isInExile(1, "Mountain").shouldBeTrue()
                game.getLifeTotal(2) shouldBe 18
            }
        }

        context("the mana +1") {
            test("adds {R}{R}") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Chandra, Torch of Defiance")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val chandra = game.findPermanent("Chandra, Torch of Defiance")!!
                activate(game, chandra, index = 1)
                game.resolveStack()

                game.state.getEntity(game.player1Id)?.get<ManaPoolComponent>()?.red shouldBe 2
                loyalty(game, chandra) shouldBe 5
            }
        }

        context("the −3") {
            test("deals 4 damage to target creature") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Chandra, Torch of Defiance")
                    .withCardOnBattlefield(2, "Hill Giant")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val chandra = game.findPermanent("Chandra, Torch of Defiance")!!
                val giant = game.findPermanent("Hill Giant")!!
                activate(game, chandra, index = 2, targets = listOf(ChosenTarget.Permanent(giant)))
                game.resolveStack()

                game.isOnBattlefield("Hill Giant").shouldBeFalse()
                loyalty(game, chandra) shouldBe 1
            }
        }

        context("the −7") {
            test("the emblem deals 5 damage whenever you cast a spell, and outlives Chandra") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Chandra, Torch of Defiance")
                    .withCardInHand(1, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Forest", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val chandra = game.findPermanent("Chandra, Torch of Defiance")!!
                setLoyalty(game, chandra, 7)
                activate(game, chandra, index = 3)
                game.resolveStack()

                withClue("Chandra went to 0 loyalty; the emblem remains") {
                    game.findPermanent("Chandra, Torch of Defiance") shouldBe null
                    game.state.globalGrantedTriggeredAbilities.size shouldBe 1
                }

                game.castSpell(1, "Grizzly Bears").error shouldBe null
                if (game.hasPendingDecision()) game.submitManaSourcesAutoPay()
                withClue("the emblem trigger asks for any target") {
                    game.hasPendingDecision().shouldBeTrue()
                }
                game.selectTargets(listOf(game.player2Id))
                game.resolveStack()

                game.getLifeTotal(2) shouldBe 15
                game.isOnBattlefield("Grizzly Bears").shouldBeTrue()
            }
        }
    }

    private fun activate(
        game: TestGame,
        source: EntityId,
        index: Int,
        targets: List<ChosenTarget> = emptyList(),
    ) {
        val ability = cardRegistry.getCard("Chandra, Torch of Defiance")!!.script.activatedAbilities[index]
        game.execute(
            ActivateAbility(
                playerId = game.player1Id,
                sourceId = source,
                abilityId = ability.id,
                targets = targets,
            )
        ).error shouldBe null
    }

    private fun loyalty(game: TestGame, id: EntityId): Int =
        game.state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.LOYALTY) ?: 0

    private fun setLoyalty(game: TestGame, id: EntityId, amount: Int) {
        game.state = game.state.updateEntity(id) { c ->
            c.with(CountersComponent(mapOf(CounterType.LOYALTY to amount)))
        }
    }
}
