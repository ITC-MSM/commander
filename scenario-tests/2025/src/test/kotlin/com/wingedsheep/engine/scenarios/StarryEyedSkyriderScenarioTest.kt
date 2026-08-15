package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Starry-Eyed Skyrider (TDM #25) — {2}{W} Human Scout, 1/3, Flying.
 *
 * "Whenever this creature attacks, another target creature you control gains flying until end of turn."
 * "Attacking tokens you control have flying."
 *
 * The first test exercises the attack trigger: Skyrider attacks, and a non-flying creature you
 * control (the only legal "another target creature") gains flying until end of turn. The second
 * test exercises the continuous static: a token you control gains flying once it's attacking, and
 * a token that is *not* attacking does not (the static is scoped to attacking tokens).
 */
class StarryEyedSkyriderScenarioTest : ScenarioTestBase() {

    init {
        context("Starry-Eyed Skyrider") {

            test("attack trigger grants flying to another target creature you control") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Starry-Eyed Skyrider", summoningSickness = false)
                    .withCardOnBattlefield(1, "Glory Seeker", summoningSickness = false) // 2/2, no flying
                    .withActivePlayer(1)
                    .inPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                    .build()

                val ally = game.findPermanent("Glory Seeker")!!
                withClue("Glory Seeker has no flying before Skyrider attacks") {
                    game.state.projectedState.hasKeyword(ally, Keyword.FLYING) shouldBe false
                }

                game.declareAttackers(mapOf("Starry-Eyed Skyrider" to 2))
                game.resolveStack()

                // "another target creature you control" — choose Glory Seeker if prompted.
                if (game.hasPendingDecision() && game.getPendingDecision() is ChooseTargetsDecision) {
                    game.selectTargets(listOf(ally))
                }
                game.resolveStack()

                withClue("Glory Seeker gains flying until end of turn from the attack trigger") {
                    game.state.projectedState.hasKeyword(ally, Keyword.FLYING) shouldBe true
                }
            }

            test("attacking tokens you control have flying; non-attacking tokens do not") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Starry-Eyed Skyrider", summoningSickness = false)
                    .withCardOnBattlefield(1, "Glory Seeker", summoningSickness = false, isToken = true)
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false, isToken = true)
                    .withActivePlayer(1)
                    .inPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                    .build()

                val attackingToken = game.findPermanent("Glory Seeker")!!
                val idleToken = game.findPermanent("Grizzly Bears")!!

                withClue("A token gains no flying from the static while it is not attacking") {
                    game.state.projectedState.hasKeyword(attackingToken, Keyword.FLYING) shouldBe false
                }

                // Attack with only the Glory Seeker token; leave the Grizzly Bears token back.
                game.declareAttackers(mapOf("Glory Seeker" to 2))
                game.resolveStack()
                // Resolve the Skyrider attack trigger if it fired (Skyrider itself isn't attacking here,
                // so the trigger shouldn't fire — but drain any pending decision defensively).
                if (game.hasPendingDecision() && game.getPendingDecision() is ChooseTargetsDecision) {
                    game.selectTargets(listOf(attackingToken))
                    game.resolveStack()
                }

                withClue("The attacking token you control has flying from the static") {
                    game.state.projectedState.hasKeyword(attackingToken, Keyword.FLYING) shouldBe true
                }
                withClue("The non-attacking token does not get flying from the static") {
                    game.state.projectedState.hasKeyword(idleToken, Keyword.FLYING) shouldBe false
                }
            }
        }
    }
}
