package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Reproduction test for the Wartime Protestors (TLA #160) bug.
 *
 * Oracle text:
 *  "Haste
 *   Whenever another Ally you control enters, put a +1/+1 counter on THAT creature and it gains
 *   haste until end of turn."
 *
 * "That creature" is the Ally that entered — not Wartime Protestors. The auto-generated card
 * mis-wired both the counter and the haste grant to `EffectTarget.Self`, so in play the counter
 * (and haste) landed on Wartime Protestors itself while the newly-arrived Ally got nothing.
 *
 * This test casts another Ally with Wartime Protestors already in play and asserts the counter +
 * haste land on the entering Ally, and that Wartime Protestors stays a vanilla 4/4.
 */
class WartimeProtestorsScenarioTest : ScenarioTestBase() {

    init {
        context("Wartime Protestors Ally-enters trigger") {

            test("counter and haste go to the entering Ally, not to Wartime Protestors") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardOnBattlefield(1, "Wartime Protestors")
                    // Rebellious Captives is a clean 2/2 Ally with no ETB effect of its own.
                    .withCardInHand(1, "Rebellious Captives")
                    .withLandsOnBattlefield(1, "Forest", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val protestors = game.findPermanent("Wartime Protestors")!!

                // Cast the second Ally; resolve it onto the battlefield, then resolve the
                // Wartime Protestors ETB trigger it puts on the stack.
                game.castSpell(1, "Rebellious Captives").error shouldBe null
                game.resolveStack()

                val captives = game.findPermanent("Rebellious Captives")!!
                val projected = game.state.projectedState

                withClue("the entering Ally (Rebellious Captives) got the +1/+1 counter: base 2/2 -> 3/3") {
                    projected.getPower(captives) shouldBe 3
                    projected.getToughness(captives) shouldBe 3
                }

                withClue("the entering Ally gained haste until end of turn") {
                    projected.hasKeyword(captives, Keyword.HASTE) shouldBe true
                }

                withClue("Wartime Protestors did NOT get the counter — it stays a vanilla 4/4") {
                    projected.getPower(protestors) shouldBe 4
                    projected.getToughness(protestors) shouldBe 4
                }
            }
        }
    }
}
