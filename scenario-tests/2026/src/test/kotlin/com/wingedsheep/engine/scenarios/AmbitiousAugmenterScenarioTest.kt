package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Ambitious Augmenter (Secrets of Strixhaven #140).
 *
 * Ambitious Augmenter ({G}, 1/1, Turtle Wizard):
 *   Increment (Whenever you cast a spell, if the amount of mana you spent is greater than this
 *     creature's power or toughness, put a +1/+1 counter on this creature.)
 *   When this creature dies, if it had one or more counters on it, create a 0/0 green and blue
 *     Fractal creature token, then put this creature's counters on that token.
 *
 * Exercises the composed dies trigger: create the 0/0 Fractal token, then move all of the dying
 * creature's last-known counters onto that just-created token via PipelineTarget(CREATED_TOKENS, 0),
 * gated by the `TriggeringEntityHadCounters` intervening-if.
 */
class AmbitiousAugmenterScenarioTest : ScenarioTestBase() {

    private fun plusOneCounters(game: TestGame, id: EntityId): Int =
        game.state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    init {
        context("Ambitious Augmenter — Increment + dies-makes-Fractal-with-counters") {

            test("dying with counters creates a Fractal token and moves the counters onto it") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Ambitious Augmenter")
                    .withCardInHand(1, "Grizzly Bears") // {1}{G} = 2 mana spent > 1 P/T -> increment
                    .withCardInHand(1, "Lightning Bolt")
                    .withLandsOnBattlefield(1, "Forest", 2)
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val augmenter = game.findPermanent("Ambitious Augmenter")!!

                // Cast a 2-mana spell: 2 mana spent > power/toughness 1 -> Increment puts a +1/+1 counter.
                game.castSpell(1, "Grizzly Bears").error shouldBe null
                game.resolveStack() // Increment trigger
                game.resolveStack() // Grizzly Bears

                withClue("Increment placed one +1/+1 counter (2 mana > 1 P/T)") {
                    plusOneCounters(game, augmenter) shouldBe 1
                }

                // Augmenter is now a 2/2 (1/1 + one +1/+1). A 3-damage bolt kills it.
                // Casting the bolt itself fires an Increment trigger (no-op: 1 mana is not > 2),
                // which must resolve before the bolt, which then kills the augmenter and queues the
                // dies trigger. Resolve the whole chain.
                game.castSpell(1, "Lightning Bolt", augmenter).error shouldBe null
                game.resolveStack() // Increment trigger from casting the bolt (no counter)
                game.resolveStack() // bolt resolves, augmenter dies, dies-trigger goes on stack
                game.resolveStack() // dies trigger: create Fractal, then move counters onto it

                withClue("Augmenter is in the graveyard") {
                    game.findPermanent("Ambitious Augmenter") shouldBe null
                }

                val fractal = game.findPermanent("Fractal Token")
                withClue("A Fractal token should have been created on death") {
                    (fractal != null) shouldBe true
                }
                withClue("The dying creature's single +1/+1 counter moved onto the Fractal token") {
                    plusOneCounters(game, fractal!!) shouldBe 1
                }
            }

            test("dying with no counters does not create a Fractal token") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Ambitious Augmenter")
                    .withCardInHand(1, "Lightning Bolt")
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val augmenter = game.findPermanent("Ambitious Augmenter")!!

                // The 1/1 has no counters; one bolt kills it. Intervening "if it had counters" is false.
                game.castSpell(1, "Lightning Bolt", augmenter)
                game.resolveStack()

                withClue("Augmenter is dead") { game.findPermanent("Ambitious Augmenter") shouldBe null }
                withClue("No counters -> no Fractal token created on death") {
                    (game.findPermanent("Fractal Token") == null) shouldBe true
                }
            }
        }
    }
}
