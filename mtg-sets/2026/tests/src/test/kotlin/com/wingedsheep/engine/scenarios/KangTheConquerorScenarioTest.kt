package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.SelectManaSourcesDecision
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.player.SkipNextTurnComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldStartWith

/**
 * Kang the Conqueror (MSH #62) — {2}{U}{U} Legendary Creature — Human Villain, 4/5.
 *
 *   Flying
 *   Power-up — {5}{U}{U}{U}: Put a +1/+1 counter on Kang. Take an extra turn after this one.
 *   During that turn, power-up abilities can't be activated.
 *
 * The power-up plumbing itself (once-per-object, the pip-wise self discount) is covered by
 * [PowerUpKeywordScenarioTest], as is the extra-turn lockout as a mechanic. What is asserted here
 * is that *this card* wires the three clauses together: one counter, an extra turn for its
 * controller, and a lockout that reaches a power-up ability Kang does not control — Serpent
 * Specialist's, under the opponent — during that turn.
 *
 * The lockout is checked through a second permanent on purpose. Kang's own power-up is already
 * spent by `ActivationRestriction.Once`, so asserting only against him would pass even if the
 * lockout did nothing at all.
 */
class KangTheConquerorScenarioTest : ScenarioTestBase() {

    private val kangAbilityId
        get() = cardRegistry.getCard("Kang the Conqueror")!!.script.activatedAbilities[0].id
    private val specialistAbilityId
        get() = cardRegistry.getCard("Serpent Specialist")!!.script.activatedAbilities[0].id

    /** Kang the turn he lands, alongside an opposing power-up creature and mana for both sides. */
    private fun kangScenario() = scenario()
        .withPlayers("Player", "Opponent")
        .withCardOnBattlefield(1, "Kang the Conqueror", enteredThisTurn = true)
        .withCardOnBattlefield(2, "Serpent Specialist")
        .withLandsOnBattlefield(1, "Island", 8)
        .withLandsOnBattlefield(2, "Forest", 8)
        .withCardInLibrary(1, "Island")
        .withCardInLibrary(1, "Island")
        .withCardInLibrary(2, "Island")
        .withCardInLibrary(2, "Island")
        .withActivePlayer(1)
        .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)

    private fun TestGame.activateKang() {
        val kangId = findPermanent("Kang the Conqueror")!!
        execute(ActivateAbility(player1Id, kangId, kangAbilityId)).error shouldBe null
        if (getPendingDecision() is SelectManaSourcesDecision) submitManaSourcesAutoPay()
        resolveStack()
    }

    private fun TestGame.crossIntoNextTurn() {
        passUntilPhase(Phase.ENDING, Step.END)
        passUntilPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
    }

    init {
        context("Kang the Conqueror") {

            test("a 4/5 flier whose power-up is discounted to {3}{U} the turn he enters") {
                val game = kangScenario().build()

                val kangId = game.findPermanent("Kang the Conqueror")!!
                game.state.projectedState.getPower(kangId) shouldBe 4
                game.state.projectedState.getToughness(kangId) shouldBe 5
                withClue("Flying is printed on the card") {
                    game.state.projectedState.hasKeyword(kangId, Keyword.FLYING) shouldBe true
                }

                val action = game.getLegalActions(1).firstOrNull { it.description.startsWith("Power-up —") }
                withClue("the power-up must be offered") { action shouldNotBe null }
                withClue("{5}{U}{U}{U} reduced pip-wise by {2}{U}{U} is {3}{U}") {
                    action!!.description shouldStartWith "Power-up — {3}{U}:"
                }
            }

            test("activating puts a counter on Kang and hands him the next turn") {
                val game = kangScenario().build()
                val kangId = game.findPermanent("Kang the Conqueror")!!
                game.activateKang()

                withClue("one +1/+1 counter, so a 5/6") {
                    game.state.getEntity(kangId)?.get<CountersComponent>()
                        ?.getCount(CounterType.PLUS_ONE_PLUS_ONE) shouldBe 1
                    game.state.projectedState.getPower(kangId) shouldBe 5
                    game.state.projectedState.getToughness(kangId) shouldBe 6
                }
                withClue("the extra turn is modeled by the opponent skipping theirs") {
                    game.state.getEntity(game.player2Id)?.has<SkipNextTurnComponent>() shouldBe true
                }

                game.crossIntoNextTurn()
                withClue("so the next turn to begin is Kang's controller's extra turn") {
                    game.state.activePlayerId shouldBe game.player1Id
                }
            }

            test("during the extra turn the opponent's power-up can't be activated either") {
                val game = kangScenario().build()
                game.activateKang()
                game.crossIntoNextTurn()

                // The opponent holds priority during the turn taker's main phase once it is passed.
                game.passPriority()
                game.state.priorityPlayerId shouldBe game.player2Id

                withClue("Serpent Specialist's power-up is withheld by the enumerator") {
                    game.getLegalActions(2).firstOrNull {
                        it.description.startsWith("Power-up —")
                    } shouldBe null
                }
                val specialistId = game.findPermanent("Serpent Specialist")!!
                val rejected = game.execute(
                    ActivateAbility(game.player2Id, specialistId, specialistAbilityId)
                )
                withClue("and rejected by the handler for the lockout specifically") {
                    rejected.error shouldBe "Power-up abilities can't be activated this turn"
                }
            }

            test("the opponent's power-up works again on the turn after the extra one") {
                val game = kangScenario().build()
                game.activateKang()
                game.crossIntoNextTurn() // the extra turn
                game.crossIntoNextTurn() // the opponent's own turn

                game.state.activePlayerId shouldBe game.player2Id
                val specialistId = game.findPermanent("Serpent Specialist")!!
                val result = game.execute(
                    ActivateAbility(game.player2Id, specialistId, specialistAbilityId)
                )
                withClue("the lockout was scoped to the extra turn alone: ${result.error}") {
                    result.error shouldBe null
                }
                if (game.getPendingDecision() is SelectManaSourcesDecision) {
                    game.submitManaSourcesAutoPay()
                }
                game.resolveStack()
                withClue("Serpent Specialist's power-up resolves normally") {
                    game.state.getEntity(specialistId)?.get<CountersComponent>()
                        ?.getCount(CounterType.PLUS_ONE_PLUS_ONE) shouldNotBe 0
                }
            }
        }
    }
}
