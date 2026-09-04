package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.SelectManaSourcesDecision
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.mtg.sets.definitions.rav.cards.ScreechingGriffin
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Screeching Griffin (RAV #29) — {3}{W} 2/2 flying, "{R}: Target creature can't block this
 * creature this turn."
 *
 * The point of the card, and of this test, is that the restriction is **pairwise**. The blanket
 * `SetCantBlock` that [com.wingedsheep.sdk.scripting.effects.CantBlockEffect] projects without an
 * `attacker` would take the target out of combat entirely; the Griffin only bars it from blocking
 * *the Griffin*. So the second test is the load-bearing one: the same creature that was refused
 * against the Griffin must still be able to block the other attacker in the same declaration.
 *
 * Both attackers fly, because the Griffin does — a ground blocker would be refused by `FlyingRule`
 * and prove nothing about the new rule.
 */
class ScreechingGriffinScenarioTest : ScenarioTestBase() {

    private val cantBlockAbility = ScreechingGriffin.activatedAbilities.single().id

    private fun TestGame.drain() {
        var guard = 0
        while (guard++ < 15) {
            when (val decision = getPendingDecision()) {
                is SelectManaSourcesDecision -> submitManaSourcesAutoPay()
                null -> if (state.stack.isNotEmpty()) resolveStack() else break
                else -> error("unexpected decision $decision")
            }
        }
    }

    private fun board(): ScenarioBuilder = scenario()
        .withPlayers("Player1", "Player2")
        .withCardOnBattlefield(1, "Screeching Griffin", summoningSickness = false)
        .withCardOnBattlefield(1, "Storm Crow", summoningSickness = false)
        .withLandsOnBattlefield(1, "Mountain", 1)
        .withCardOnBattlefield(2, "Wind Drake")
        .withActivePlayer(1)
        .withPriorityPlayer(1)
        .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)

    /** Point the Griffin's ability at the Wind Drake and let it resolve. */
    private fun nameTheDrake(game: TestGame) {
        val griffin = game.findPermanent("Screeching Griffin").shouldNotBeNull()
        val drake = game.findPermanent("Wind Drake").shouldNotBeNull()
        val result = game.execute(
            ActivateAbility(
                playerId = game.player1Id,
                sourceId = griffin,
                abilityId = cantBlockAbility,
                targets = listOf(ChosenTarget.Permanent(drake)),
            )
        )
        withClue("activation should succeed: ${result.error}") { result.error shouldBe null }
        game.drain()
    }

    init {
        test("without the ability the Drake blocks the Griffin normally") {
            val game = board().build()

            game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
            game.declareAttackers(mapOf("Screeching Griffin" to 2)).error shouldBe null
            game.passUntilPhase(Phase.COMBAT, Step.DECLARE_BLOCKERS)

            game.declareBlockers(mapOf("Wind Drake" to listOf("Screeching Griffin")))
                .error shouldBe null
        }

        test("the named creature can't block the Griffin, but can still block the other attacker") {
            val game = board().build()

            nameTheDrake(game)

            game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
            game.declareAttackers(mapOf("Screeching Griffin" to 2, "Storm Crow" to 2))
                .error shouldBe null
            game.passUntilPhase(Phase.COMBAT, Step.DECLARE_BLOCKERS)

            withClue("the Drake was named, so blocking the Griffin must be rejected") {
                game.declareBlockers(mapOf("Wind Drake" to listOf("Screeching Griffin")))
                    .error shouldNotBe null
            }
            withClue("the restriction is pairwise — every other attacker is still fair game") {
                game.declareBlockers(mapOf("Wind Drake" to listOf("Storm Crow")))
                    .error shouldBe null
            }
        }

        test("a creature that wasn't named is unaffected") {
            val game = board()
                .withCardOnBattlefield(2, "Snapping Drake")
                .build()

            nameTheDrake(game)

            game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
            game.declareAttackers(mapOf("Screeching Griffin" to 2)).error shouldBe null
            game.passUntilPhase(Phase.COMBAT, Step.DECLARE_BLOCKERS)

            withClue("only the targeted creature is barred; the Snapping Drake still blocks") {
                game.declareBlockers(mapOf("Snapping Drake" to listOf("Screeching Griffin")))
                    .error shouldBe null
            }
        }
    }
}
