package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.model.CardDefinition
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Sandstorm Salvager (BIG #19) — {2}{G} Creature — Human Artificer (1/1).
 *
 * "When this creature enters, create a 3/3 colorless Golem artifact creature token.
 *  {2}, {T}: Put a +1/+1 counter on each creature token you control. They gain trample
 *  until end of turn."
 *
 * Verifies the activated ability: the +1/+1 counter and the (end-of-turn) trample grant land
 * on every creature *token* you control and on nothing else — a non-token creature you control
 * is untouched, even though it's a creature.
 */
class SandstormSalvagerScenarioTest : ScenarioTestBase() {

    private val projector = StateProjector()

    private val salvagerAbilityId =
        cardRegistry.getCard("Sandstorm Salvager")!!.activatedAbilities.first().id

    private fun plusOneCounters(game: TestGame, id: com.wingedsheep.sdk.model.EntityId): Int =
        game.state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    init {
        // Two stand-in creatures we can drop onto the battlefield as tokens / non-tokens.
        cardRegistry.register(
            CardDefinition.creature(
                name = "Test Golem",
                manaCost = ManaCost.parse("{3}"),
                subtypes = setOf(Subtype("Golem")),
                power = 3,
                toughness = 3
            )
        )
        cardRegistry.register(
            CardDefinition.creature(
                name = "Test Bear",
                manaCost = ManaCost.parse("{1}{G}"),
                subtypes = setOf(Subtype("Bear")),
                power = 2,
                toughness = 2
            )
        )

        context("Sandstorm Salvager activated ability") {

            test("counters and trample land only on creature tokens you control") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Sandstorm Salvager", summoningSickness = false)
                    .withCardOnBattlefield(1, "Test Golem", summoningSickness = false, isToken = true)
                    .withCardOnBattlefield(1, "Test Bear", summoningSickness = false) // non-token creature
                    .withLandsOnBattlefield(1, "Forest", 2) // pay {2}
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val tokenGolem = game.findPermanent("Test Golem")!!
                val nonTokenBear = game.findPermanent("Test Bear")!!

                val activation = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = game.findPermanent("Sandstorm Salvager")!!,
                        abilityId = salvagerAbilityId,
                    )
                )
                withClue("Activating Sandstorm Salvager should succeed: ${activation.error}") {
                    activation.error shouldBe null
                }
                game.resolveStack()

                val projected = projector.project(game.state)

                withClue("The creature token gets a +1/+1 counter") {
                    plusOneCounters(game, tokenGolem) shouldBe 1
                }
                withClue("The non-token creature gets no counter") {
                    plusOneCounters(game, nonTokenBear) shouldBe 0
                }
                withClue("The creature token gains trample") {
                    projected.hasKeyword(tokenGolem, Keyword.TRAMPLE) shouldBe true
                }
                withClue("The non-token creature does not gain trample") {
                    projected.hasKeyword(nonTokenBear, Keyword.TRAMPLE) shouldBe false
                }
            }
        }
    }
}
