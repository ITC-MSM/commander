package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.mtg.sets.definitions.lea.cards.GrizzlyBears
import com.wingedsheep.mtg.sets.definitions.mrd.cards.MesmericOrb
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.scripting.targets.TargetPermanent
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Mesmeric Orb (MRD) — "Whenever a permanent becomes untapped, that permanent's controller mills a
 * card."
 *
 * A regression test for a bug this card shipped with. The definition always read
 * `Patterns.Library.mill(1, EffectTarget.ControllerOfTriggeringEntity)` — the right thing — but
 * `mill` mapped every target it did not explicitly recognise to `Player.You`, and
 * `ControllerOfTriggeringEntity` was not among the four it recognised. So the Orb's *own*
 * controller milled on every untap, whoever the permanent belonged to. Nothing caught it: the
 * golden snapshot serialises the intended `EffectTarget`, the card still milled exactly one card
 * per untap, and there was no scenario test.
 *
 * "That permanent's controller" is the entire card, so the assertions are about which library
 * shrinks — and always about both, since milling the wrong player is invisible if you only check
 * that a mill happened.
 */
class MesmericOrbScenarioTest : ScenarioTestBase() {

    private val unwind = card("Unwind Test") {
        manaCost = "{0}"
        typeLine = "Instant"
        oracleText = "Untap target permanent."
        spell {
            val t = target("target permanent", TargetPermanent())
            effect = Effects.Untap(t)
        }
    }

    /** The Orb belongs to player 1 throughout; only the untapping permanent's owner varies. */
    private fun game(untapperOwner: Int, tappedPermanentOwner: Int): TestGame {
        val builder = scenario()
            .withPlayers("Player", "Opponent")
            .withCardOnBattlefield(1, "Mesmeric Orb")
            .withCardOnBattlefield(tappedPermanentOwner, "Grizzly Bears", tapped = true)
            .withCardInHand(untapperOwner, "Unwind Test")
            .withActivePlayer(untapperOwner)
            .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
        repeat(12) {
            builder.withCardInLibrary(1, "Island")
            builder.withCardInLibrary(2, "Island")
        }
        return builder.build()
    }

    init {
        cardRegistry.register(MesmericOrb)
        cardRegistry.register(GrizzlyBears)
        cardRegistry.register(unwind)

        context("Mesmeric Orb") {

            test("an opponent's permanent untapping mills the opponent, not the Orb's controller") {
                val game = game(untapperOwner = 2, tappedPermanentOwner = 2)
                val bears = game.findPermanent("Grizzly Bears")!!

                game.castSpell(2, "Unwind Test", targetId = bears).error shouldBe null
                game.resolveStack()

                withClue("the untapped permanent is the opponent's, so the opponent mills 1") {
                    game.librarySize(2) shouldBe 12 - 1
                }
                withClue("the Orb's controller mills nothing — the shipped bug milled here instead") {
                    game.librarySize(1) shouldBe 12
                    game.graveyardSize(1) shouldBe 0
                }
            }

            test("the Orb's controller mills when it is their own permanent untapping") {
                val game = game(untapperOwner = 1, tappedPermanentOwner = 1)
                val bears = game.findPermanent("Grizzly Bears")!!

                game.castSpell(1, "Unwind Test", targetId = bears).error shouldBe null
                game.resolveStack()

                withClue("their own permanent untapped, so they really do mill 1") {
                    game.librarySize(1) shouldBe 12 - 1
                }
                withClue("the opponent is untouched") {
                    game.librarySize(2) shouldBe 12
                }
            }

            test("the untapping permanent's controller mills even when an opponent untaps it") {
                // Player 2 casts the untap on player 1's permanent: the *permanent's* controller
                // mills, not the caster's. This is the case that distinguishes
                // ControllerOfTriggeringEntity from every "whoever did it" reading.
                val game = game(untapperOwner = 2, tappedPermanentOwner = 1)
                val bears = game.findPermanent("Grizzly Bears")!!

                game.castSpell(2, "Unwind Test", targetId = bears).error shouldBe null
                game.resolveStack()

                withClue("player 1 controls the untapped permanent, so player 1 mills") {
                    game.librarySize(1) shouldBe 12 - 1
                }
                withClue("the caster is irrelevant to the card's text") {
                    game.librarySize(2) shouldBe 12
                }
            }
        }
    }
}
