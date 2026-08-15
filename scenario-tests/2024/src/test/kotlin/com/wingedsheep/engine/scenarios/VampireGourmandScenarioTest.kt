package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Vampire Gourmand ({1}{B}, 2/2 Vampire) — whenever it attacks, you may sacrifice another
 * creature. If you do, draw a card and this creature can't be blocked this turn.
 *
 * The optional sacrifice is modeled (like Beetle-Headed Merchants) as a targeted "another
 * creature" requirement on the attack trigger, so with no other creature in play the trigger
 * has no legal target and nothing happens — the deterministic edge case verified here.
 */
class VampireGourmandScenarioTest : ScenarioTestBase() {

    init {
        context("Vampire Gourmand") {

            test("with no other creature to sacrifice, attacking draws nothing and grants no evasion") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Vampire Gourmand")
                    .withCardInLibrary(1, "Swamp")
                    .withActivePlayer(1)
                    .build()

                val handBefore = game.handSize(1)
                val gourmand = game.findPermanent("Vampire Gourmand")!!

                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Vampire Gourmand" to 2)).error shouldBe null
                game.resolveStack()

                withClue("no other creature → no card drawn") {
                    game.handSize(1) shouldBe handBefore
                }
                withClue("no sacrifice → no can't-be-blocked grant") {
                    game.state.projectedState.hasKeyword(gourmand, AbilityFlag.CANT_BE_BLOCKED.name) shouldBe false
                }
            }
        }
    }
}
