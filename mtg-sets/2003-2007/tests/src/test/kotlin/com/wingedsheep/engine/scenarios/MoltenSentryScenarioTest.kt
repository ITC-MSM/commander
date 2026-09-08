package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CoinFlipEvent
import com.wingedsheep.engine.core.SelectManaSourcesDecision
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.mtg.sets.definitions.rav.cards.MoltenSentry
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe

/**
 * Molten Sentry (RAV) — "As this creature enters, flip a coin. If the coin comes up heads, this
 * creature enters as a 5/2 creature with haste. If it comes up tails, this creature enters as a
 * 2/5 creature with defender."
 *
 * The card is printed `*`/`*`, so the *only* thing that gives the Sentry a size is the entry
 * replacement. Every assertion therefore reads the **projected** stats and keywords and pairs them
 * with the coin flip that actually happened, rather than seeding a particular outcome: a seed→face
 * mapping is an implementation detail of the RNG, while "whatever the coin said, the body matches
 * it" is the card.
 *
 * The last test runs the entry many times over and insists on seeing both faces — that is what
 * catches a branch that silently never fires (an `OnEnterRunEffect` dropped on some entry path, or
 * a `lostEffect` that never runs), which a single-outcome test cannot.
 */
class MoltenSentryScenarioTest : ScenarioTestBase() {

    private val stateProjector = StateProjector()

    /** Heads = the flip was won. Recorded off the emitted [CoinFlipEvent], not guessed. */
    private data class Entry(val heads: Boolean, val power: Int?, val toughness: Int?, val keywords: Set<String>)

    private fun playSentry(seed: Long): Entry {
        val game = scenario()
            .withPlayers("Alice", "Bob")
            .withCardInHand(1, "Molten Sentry")
            .withLandsOnBattlefield(1, "Mountain", 4)
            .withActivePlayer(1)
            .withRngSeed(seed)
            .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
            .build()

        val events = mutableListOf<com.wingedsheep.engine.core.GameEvent>()
        events += game.castSpell(1, "Molten Sentry").also { it.error shouldBe null }.events

        var guard = 0
        while (guard++ < 15) {
            when (game.getPendingDecision()) {
                is SelectManaSourcesDecision -> events += game.submitManaSourcesAutoPay().events
                null -> if (game.state.stack.isNotEmpty()) {
                    events += game.resolveStack().flatMap { it.events }
                } else break
                else -> error("unexpected decision ${game.getPendingDecision()}")
            }
        }

        val sentry = game.findPermanent("Molten Sentry")
            ?: error("Molten Sentry never reached the battlefield")
        val projected = stateProjector.project(game.state)
        val flips = events.filterIsInstance<CoinFlipEvent>()
        withClue("exactly one coin is flipped, and it is flipped once — not once per branch") {
            flips.size shouldBe 1
        }
        return Entry(
            heads = flips.single().won,
            power = projected.getPower(sentry),
            toughness = projected.getToughness(sentry),
            keywords = projected.getKeywords(sentry),
        )
    }

    init {
        cardRegistry.register(MoltenSentry)

        context("Molten Sentry") {

            test("the body always matches the coin that was flipped") {
                val entry = playSentry(seed = 7L)

                if (entry.heads) {
                    withClue("heads: a 5/2 with haste") {
                        entry.power shouldBe 5
                        entry.toughness shouldBe 2
                        entry.keywords shouldContain Keyword.HASTE.name
                    }
                    withClue("heads must not also pick up the tails rider") {
                        entry.keywords.contains(Keyword.DEFENDER.name) shouldBe false
                    }
                } else {
                    withClue("tails: a 2/5 with defender") {
                        entry.power shouldBe 2
                        entry.toughness shouldBe 5
                        entry.keywords shouldContain Keyword.DEFENDER.name
                    }
                    withClue("tails must not also pick up the heads rider") {
                        entry.keywords.contains(Keyword.HASTE.name) shouldBe false
                    }
                }
            }

            test("a printed */* Sentry is never left at 0/0 — it survives its own entry") {
                val entry = playSentry(seed = 99L)

                withClue("the entry replacement runs inline with the entry, before SBAs see an 0/0") {
                    (entry.toughness ?: 0) shouldBe if (entry.heads) 2 else 5
                }
            }

            test("both faces actually occur across repeated entries, each internally consistent") {
                val seen = mutableSetOf<Boolean>()
                for (seed in 1L..40L) {
                    val entry = playSentry(seed)
                    seen += entry.heads
                    withClue("seed $seed: stats must agree with the flip every single time") {
                        entry.power to entry.toughness shouldBe if (entry.heads) 5 to 2 else 2 to 5
                    }
                    if (seen.size == 2) break
                }
                withClue("a branch that never fires is the failure this card is prone to") {
                    seen shouldBe setOf(true, false)
                }
            }
        }
    }
}
