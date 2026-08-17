package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.SelectManaSourcesDecision
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.AbilityId
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldStartWith

/**
 * Wonder Man, Hollywood Hero (MSH #160) — {3}{R}{R} Legendary Creature — Human Performer Hero, 4/4.
 *
 *   Flying
 *   Each power-up ability of permanents you control can be activated an additional time.
 *   Power-up — {5}{R}{R}: Put two +1/+1 counters on Wonder Man.
 *
 * The permission primitive itself ([com.wingedsheep.sdk.scripting.ExtraOnceOnlyActivations]) —
 * stacking, the `kind` axis, the plain-`Once` exclusion, the prohibition interaction — is covered
 * by [ExtraOnceOnlyActivationsScenarioTest]. What is asserted here is that *this card* wires its
 * three clauses together: the body and flying, a `{5}{R}{R}` power-up discounted to `{2}` the turn
 * he lands, and a permission that reaches both a teammate's power-up and his own.
 *
 * His own power-up is checked separately from the teammate's on purpose: "each power-up ability of
 * permanents you control" includes Wonder Man himself, and an implementation that scoped the
 * permission to *other* permanents would still pass a teammate-only test.
 */
class WonderManHollywoodHeroScenarioTest : ScenarioTestBase() {

    private val wonderManAbilityId
        get() = cardRegistry.getCard("Wonder Man, Hollywood Hero")!!.script.activatedAbilities[0].id

    /** Hercules is the cheapest other MSH power-up in green; a Forest funds his {4}{G}. */
    private val herculesAbilityId
        get() = cardRegistry.getCard("Hercules, Prince of Power")!!.script.activatedAbilities[0].id

    private fun wonderManScenario() = scenario()
        .withPlayers("Player", "Opponent")
        .withCardOnBattlefield(1, "Wonder Man, Hollywood Hero", enteredThisTurn = true)
        .withLandsOnBattlefield(1, "Mountain", 12)
        .withLandsOnBattlefield(1, "Forest", 12)
        .withActivePlayer(1)
        .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)

    private fun TestGame.actionFor(playerNumber: Int, abilityId: AbilityId) =
        getLegalActions(playerNumber).firstOrNull {
            it.action.let { a -> a is ActivateAbility && a.abilityId == abilityId }
        }

    private fun TestGame.activate(playerNumber: Int, sourceId: EntityId, abilityId: AbilityId): String? {
        val playerId = if (playerNumber == 1) player1Id else player2Id
        val error = execute(ActivateAbility(playerId, sourceId, abilityId)).error
        if (error != null) return error
        if (getPendingDecision() is SelectManaSourcesDecision) submitManaSourcesAutoPay()
        resolveStack()
        return null
    }

    private fun TestGame.countersOn(entityId: EntityId): Int =
        state.getEntity(entityId)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    init {
        context("Wonder Man, Hollywood Hero") {

            test("a 4/4 flier whose power-up is discounted to {2} the turn he enters") {
                val game = wonderManScenario().build()
                val wonderManId = game.findPermanent("Wonder Man, Hollywood Hero")!!

                game.state.projectedState.getPower(wonderManId) shouldBe 4
                game.state.projectedState.getToughness(wonderManId) shouldBe 4
                withClue("Flying is printed on the card") {
                    game.state.projectedState.hasKeyword(wonderManId, Keyword.FLYING) shouldBe true
                }
                withClue("{5}{R}{R} reduced pip-wise by {3}{R}{R} is {2}") {
                    game.actionFor(1, wonderManAbilityId)!!.description shouldStartWith "Power-up — {2}:"
                }

                game.activate(1, wonderManId, wonderManAbilityId) shouldBe null
                withClue("the ability puts two counters on him, not one") {
                    game.countersOn(wonderManId) shouldBe 2
                }
            }

            test("his own power-up may be activated a second time — 'each power-up ability' includes his") {
                val game = wonderManScenario().build()
                val wonderManId = game.findPermanent("Wonder Man, Hollywood Hero")!!

                game.activate(1, wonderManId, wonderManAbilityId) shouldBe null
                withClue("the enumerator must offer his spent power-up again") {
                    game.actionFor(1, wonderManAbilityId) shouldNotBe null
                }
                withClue("and the handler must accept it") {
                    game.activate(1, wonderManId, wonderManAbilityId) shouldBe null
                }
                game.countersOn(wonderManId) shouldBe 4

                withClue("one additional time, not unlimited: 1 printed + 1 extra") {
                    game.actionFor(1, wonderManAbilityId) shouldBe null
                }
                game.activate(1, wonderManId, wonderManAbilityId) shouldNotBe null
                game.countersOn(wonderManId) shouldBe 4
            }

            test("another creature's power-up gets the extra activation too") {
                val game = wonderManScenario()
                    .withCardOnBattlefield(1, "Hercules, Prince of Power")
                    .build()
                val herculesId = game.findPermanent("Hercules, Prince of Power")!!

                game.activate(1, herculesId, herculesAbilityId) shouldBe null
                withClue("Hercules did not enter this turn, so his {4}{G} is undiscounted but payable") {
                    game.countersOn(herculesId) shouldBe 1
                }
                withClue("Wonder Man's static covers permanents you control, not just himself") {
                    game.activate(1, herculesId, herculesAbilityId) shouldBe null
                }
                game.countersOn(herculesId) shouldBe 2

                game.activate(1, herculesId, herculesAbilityId) shouldNotBe null
                game.countersOn(herculesId) shouldBe 2
            }

            test("an opponent's power-up is untouched") {
                val game = wonderManScenario()
                    .withCardOnBattlefield(2, "Hercules, Prince of Power")
                    .withLandsOnBattlefield(2, "Forest", 12)
                    .build()
                val herculesId = game.findPermanent("Hercules, Prince of Power")!!

                game.passPriority()
                game.state.priorityPlayerId shouldBe game.player2Id
                game.activate(2, herculesId, herculesAbilityId) shouldBe null
                withClue("'permanents you control' is Wonder Man's controller, not the opponent") {
                    game.activate(2, herculesId, herculesAbilityId) shouldNotBe null
                }
                game.countersOn(herculesId) shouldBe 1
            }
        }
    }
}
