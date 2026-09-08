package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.mtg.sets.definitions.lea.cards.HillGiant
import com.wingedsheep.mtg.sets.definitions.rav.cards.BelltowerSphinx
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Belltower Sphinx (RAV) — "Whenever a source deals damage to this creature, that source's
 * controller mills that many cards."
 *
 * Every assertion here is about **whose** library shrinks. That is the whole card, and it is the
 * half that used to be wrong in two independent ways: the trigger bound the damaged creature
 * rather than the damage source, and `Patterns.Library.mill` silently mapped any target it didn't
 * recognise to `Player.You`. Either bug alone mills the Sphinx's own controller — which looks
 * perfectly normal in a golden snapshot — so every test checks *both* libraries, never just that
 * someone milled.
 */
class BelltowerSphinxScenarioTest : ScenarioTestBase() {

    private val bolt = card("Bolt Test") {
        manaCost = "{0}"
        typeLine = "Instant"
        oracleText = "Bolt Test deals 3 damage to target creature."
        spell {
            val t = target("target creature", TargetCreature())
            effect = Effects.DealDamage(3, t)
        }
    }

    /** 7 damage kills the 2/5 outright — the last-known-information case. */
    private val quake = card("Quake Test") {
        manaCost = "{0}"
        typeLine = "Instant"
        oracleText = "Quake Test deals 7 damage to target creature."
        spell {
            val t = target("target creature", TargetCreature())
            effect = Effects.DealDamage(7, t)
        }
    }

    /**
     * The Sphinx belongs to player 1; the burner takes their own turn so they hold priority
     * without a handoff. Libraries are deep enough that no mill here empties one — losing to a
     * draw from an empty library would end the game before the assertions run.
     */
    private fun burnGame(burner: Int, vararg spells: String): TestGame {
        val builder = scenario()
            .withPlayers("Player", "Opponent")
            .withCardOnBattlefield(1, "Belltower Sphinx", summoningSickness = false)
            .withActivePlayer(burner)
            .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
        repeat(12) {
            builder.withCardInLibrary(1, "Island")
            builder.withCardInLibrary(2, "Island")
        }
        spells.forEach { builder.withCardInHand(burner, it) }
        return builder.build()
    }

    init {
        cardRegistry.register(BelltowerSphinx)
        cardRegistry.register(HillGiant)
        cardRegistry.register(bolt)
        cardRegistry.register(quake)

        context("Belltower Sphinx") {

            test("a burn spell mills its own caster, not the Sphinx's controller") {
                val game = burnGame(2, "Bolt Test")
                val sphinx = game.findPermanent("Belltower Sphinx")!!

                game.castSpell(2, "Bolt Test", targetId = sphinx).error shouldBe null
                game.resolveStack()

                withClue("the opponent cast the damage source, so the opponent mills 3") {
                    game.librarySize(2) shouldBe 12 - 3
                }
                withClue("the Sphinx's controller is untouched — the bug this card exposes") {
                    game.librarySize(1) shouldBe 12
                    game.graveyardSize(1) shouldBe 0
                }
                withClue("a 2/5 survives 3 damage") {
                    game.isOnBattlefield("Belltower Sphinx") shouldBe true
                }
            }

            test("the Sphinx's own controller mills when they are the one dealing the damage") {
                val game = burnGame(1, "Bolt Test")
                val sphinx = game.findPermanent("Belltower Sphinx")!!

                game.castSpell(1, "Bolt Test", targetId = sphinx).error shouldBe null
                game.resolveStack()

                withClue("'that source's controller' is the caster — here that really is player 1") {
                    game.librarySize(1) shouldBe 12 - 3
                }
                withClue("the opponent dealt nothing and mills nothing") {
                    game.librarySize(2) shouldBe 12
                }
            }

            test("lethal damage still mills — the trigger is detected off the damage event") {
                val game = burnGame(2, "Quake Test")
                val sphinx = game.findPermanent("Belltower Sphinx")!!

                game.castSpell(2, "Quake Test", targetId = sphinx).error shouldBe null
                game.resolveStack()

                withClue("state-based actions killed the Sphinx before the trigger resolved") {
                    game.isOnBattlefield("Belltower Sphinx") shouldBe false
                }
                withClue("CR 603.10 — the trigger fired anyway, and for the full 7") {
                    game.librarySize(2) shouldBe 12 - 7
                }
                withClue("still not the Sphinx's controller") {
                    game.librarySize(1) shouldBe 12
                }
            }

            test("two damage instances mill separately, each for its own amount") {
                val game = burnGame(2, "Bolt Test", "Bolt Test")
                val sphinx = game.findPermanent("Belltower Sphinx")!!

                game.castSpell(2, "Bolt Test", targetId = sphinx).error shouldBe null
                game.resolveStack()
                withClue("first instance: 3") { game.librarySize(2) shouldBe 12 - 3 }

                // Resolving the mill trigger leaves priority with whoever answered last, so the
                // caster has to be handed it back before the second Bolt is legal.
                if (game.state.priorityPlayerId != game.player2Id) game.passPriority()

                // The Sphinx is a 2/5 carrying 3 damage; the second Bolt is lethal, but its
                // trigger still fires (see the lethal case above).
                game.castSpell(2, "Bolt Test", targetId = sphinx).error shouldBe null
                game.resolveStack()

                withClue("each DamageDealtEvent mills its own amount — 3 then 3, never 6 at once") {
                    game.librarySize(2) shouldBe 12 - 6
                }
                withClue("the Sphinx's controller never milled across either instance") {
                    game.librarySize(1) shouldBe 12
                }
            }

            test("blocking mills the attacker's controller for the attacker's power") {
                val builder = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Belltower Sphinx", summoningSickness = false)
                    .withCardOnBattlefield(2, "Hill Giant", summoningSickness = false)
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                repeat(12) {
                    builder.withCardInLibrary(1, "Island")
                    builder.withCardInLibrary(2, "Island")
                }
                val game = builder.build()

                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Hill Giant" to 1)).error shouldBe null
                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_BLOCKERS)
                game.declareBlockers(mapOf("Belltower Sphinx" to listOf("Hill Giant")))
                    .error shouldBe null
                game.passUntilPhase(Phase.POSTCOMBAT_MAIN, Step.POSTCOMBAT_MAIN)

                withClue("the 3/3 attacker dealt the damage, so its controller mills 3") {
                    game.librarySize(2) shouldBe 12 - 3
                }
                withClue("the blocking Sphinx's controller mills nothing") {
                    game.librarySize(1) shouldBe 12
                }
            }
        }
    }
}
