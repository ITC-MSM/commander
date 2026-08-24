package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Experimental Synthesizer (NEO) — "When this artifact enters **or leaves** the battlefield, exile
 * the top card of your library. Until end of turn, you may play that card." plus
 * "{2}{R}, Sacrifice this artifact: Create a 2/2 white Samurai creature token with vigilance."
 *
 * The interesting case is the two halves meeting: the activated ability sacrifices the artifact
 * **as a cost**, which is paid before the ability is even put on the stack — so the leave trigger
 * has to fire off a battlefield exit that happens during activation, not during resolution. That
 * is a shape this engine has got wrong before, and it is this card's most common line, so it is
 * pinned here rather than assumed.
 */
class ExperimentalSynthesizerScenarioTest : ScenarioTestBase() {

    init {
        test("the enter trigger impulses the top card") {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withCardInHand(1, "Experimental Synthesizer")
                .withLandsOnBattlefield(1, "Mountain", 1)
                .withCardInLibrary(1, "Hill Giant")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            game.castSpell(1, "Experimental Synthesizer").error shouldBe null
            game.resolveStack()

            withClue("The ETB exiles the top card of the library") {
                game.isInExile(1, "Hill Giant") shouldBe true
            }
        }

        test("sacrificing it to its own ability fires the leave trigger and makes the Samurai") {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withCardOnBattlefield(1, "Experimental Synthesizer")
                .withLandsOnBattlefield(1, "Mountain", 3)
                .withCardInLibrary(1, "Hill Giant")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val synth = game.findPermanent("Experimental Synthesizer")!!
            val abilityId = cardRegistry.requireCard("Experimental Synthesizer")
                .script.activatedAbilities.first().id

            game.execute(ActivateAbility(game.player1Id, synth, abilityId)).error shouldBe null
            game.resolveStack()

            withClue("The sacrifice is a cost, so the artifact is gone") {
                game.isInGraveyard(1, "Experimental Synthesizer") shouldBe true
            }
            withClue("The leave trigger still fires off a sacrifice paid as a cost") {
                game.isInExile(1, "Hill Giant") shouldBe true
            }
            withClue("…and the ability itself resolves into a Samurai token") {
                game.isOnBattlefield("Samurai") shouldBe true
            }
        }
    }
}
