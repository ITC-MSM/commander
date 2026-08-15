package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.model.CardDefinition
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Host of the Hereafter (TDM #193).
 *
 * "{2}{B}{G} Creature — Zombie Warlock 2/2.
 *  This creature enters with two +1/+1 counters on it.
 *  Whenever this creature or another creature you control dies, if it had counters on it,
 *  put its counters on up to one target creature you control."
 *
 * Exercises the new `Conditions.TriggeringEntityHadCounters` intervening-if and
 * `Effects.MoveAllLastKnownCounters` on a "this or another creature you control dies" trigger.
 */
class HostOfTheHereafterScenarioTest : ScenarioTestBase() {

    init {
        cardRegistry.register(
            CardDefinition.creature(
                name = "Counterless Bear",
                manaCost = ManaCost.parse("{1}{G}"),
                subtypes = setOf(Subtype("Bear")),
                power = 2,
                toughness = 2
            )
        )

        fun plusOne(game: TestGame, id: com.wingedsheep.sdk.model.EntityId): Int =
            game.state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

        context("Host of the Hereafter") {

            test("Host enters with two +1/+1 counters") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Host of the Hereafter")
                    .withLandsOnBattlefield(1, "Swamp", 3)
                    .withLandsOnBattlefield(1, "Forest", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Host of the Hereafter").error shouldBe null
                game.resolveStack()

                val host = game.findPermanent("Host of the Hereafter")!!
                withClue("Host enters with two +1/+1 counters") {
                    plusOne(game, host) shouldBe 2
                }
            }

            test("when this creature dies with counters, they move to up to one target creature you control") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Host of the Hereafter")
                    .withCardOnBattlefield(1, "Counterless Bear", summoningSickness = false)
                    .withCardsInHand(1, "Lightning Bolt", 2)
                    .withLandsOnBattlefield(1, "Swamp", 3)
                    .withLandsOnBattlefield(1, "Forest", 1)
                    .withLandsOnBattlefield(1, "Mountain", 4)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                // Cast Host so the enters-with-two-counters replacement applies.
                game.castSpell(1, "Host of the Hereafter").error shouldBe null
                game.resolveStack()

                val host = game.findPermanent("Host of the Hereafter")!!
                val bear = game.findPermanent("Counterless Bear")!!
                withClue("Host has two counters before dying") { plusOne(game, host) shouldBe 2 }

                // Host is a 4/4 (2/2 base + two +1/+1). Two bolts (6 damage) kill it.
                game.castSpell(1, "Lightning Bolt", host)
                game.resolveStack()
                game.castSpell(1, "Lightning Bolt", host)
                game.resolveStack()

                withClue("Host is in the graveyard") { game.findPermanent("Host of the Hereafter") shouldBe null }

                // The dies trigger had counters → put them on the only valid target (the Bear).
                if (game.hasPendingDecision()) {
                    game.selectTargets(listOf(bear))
                    game.resolveStack()
                }

                withClue("Host's two +1/+1 counters moved onto the Bear") {
                    plusOne(game, bear) shouldBe 2
                }
            }

            test("a creature that died with no counters does not trigger the ability") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Host of the Hereafter", summoningSickness = false)
                    .withCardOnBattlefield(1, "Counterless Bear", summoningSickness = false)
                    .withCardInHand(1, "Lightning Bolt")
                    .withLandsOnBattlefield(1, "Mountain", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bear = game.findPermanent("Counterless Bear")!!

                // The Bear (a creature you control) has no counters; killing it must not
                // produce a counter-move trigger (intervening "if it had counters" is false).
                game.castSpell(1, "Lightning Bolt", bear)
                game.resolveStack()

                withClue("Bear is dead") { game.findPermanent("Counterless Bear") shouldBe null }
                withClue("No pending target decision — the intervening 'if it had counters' is false") {
                    game.hasPendingDecision() shouldBe false
                }
            }
        }
    }
}
