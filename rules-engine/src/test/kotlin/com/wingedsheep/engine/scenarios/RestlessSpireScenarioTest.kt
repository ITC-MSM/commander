package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.ReorderLibraryDecision
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.core.SelectManaSourcesDecision
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Restless Spire (WOE #260) — the Izzet creature-land.
 * "{U}{R}: Until end of turn, this land becomes a 2/1 blue and red Elemental creature with
 *  'During your turn, this creature has first strike.' It's still a land."
 * "Whenever this land attacks, scry 1."
 *
 * The animate ability is a composite: the body, plus a first-strike grant gated on
 * [com.wingedsheep.sdk.dsl.Conditions.IsYourTurn]. Within the animation's own end-of-turn window
 * "during your turn" cannot change answer, so the continuous conditional collapses to a one-shot
 * test at resolution — animate on your turn and the body has first strike; animate on an
 * opponent's turn (ambushing a blocker at instant speed) and it never does. Both directions are
 * pinned here, plus the intrinsic attack trigger.
 */
class RestlessSpireScenarioTest : ScenarioTestBase() {

    private val animateAbilityId by lazy {
        // [0] {T}: add {U}, [1] {T}: add {R}, [2] {U}{R}: become a creature.
        cardRegistry.getCard("Restless Spire")!!.activatedAbilities[2].id
    }

    init {
        fun board(activePlayer: Int) = scenario()
            .withPlayers("Player1", "Player2")
            .withCardOnBattlefield(1, "Restless Spire", summoningSickness = false)
            .withLandsOnBattlefield(1, "Island", 1)
            .withLandsOnBattlefield(1, "Mountain", 1)
            // Scry needs something to look at — the scenario builder starts with empty libraries.
            .withCardInLibrary(1, "Forest")
            .withCardInLibrary(1, "Island")
            .withActivePlayer(activePlayer)
            .withPriorityPlayer(1)
            .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
            .build()

        fun TestGame.animate() {
            val result = execute(
                ActivateAbility(
                    playerId = player1Id,
                    sourceId = findPermanent("Restless Spire")!!,
                    abilityId = animateAbilityId
                )
            )
            withClue("animate activation failed: ${result.error}") { result.error shouldBe null }
            if (getPendingDecision() is SelectManaSourcesDecision) submitManaSourcesAutoPay()
            resolveStack()
        }

        test("animating on your turn yields a 2/1 Elemental with first strike that is still a land") {
            val game = board(activePlayer = 1)
            game.animate()

            val spire = game.findPermanent("Restless Spire")!!
            val projected = game.state.projectedState
            projected.isCreature(spire) shouldBe true
            withClue("\"It's still a land\"") { projected.hasType(spire, "LAND") shouldBe true }
            projected.getPower(spire) shouldBe 2
            projected.getToughness(spire) shouldBe 1
            withClue("the quoted ability is live because it is your turn") {
                projected.hasKeyword(spire, Keyword.FIRST_STRIKE) shouldBe true
            }
        }

        test("animating during an opponent's turn yields the body without first strike") {
            val game = board(activePlayer = 2)
            game.animate()

            val spire = game.findPermanent("Restless Spire")!!
            val projected = game.state.projectedState
            withClue("the body still arrives") {
                projected.isCreature(spire) shouldBe true
                projected.getPower(spire) shouldBe 2
                projected.getToughness(spire) shouldBe 1
            }
            withClue("\"During your turn\" is false on an opponent's turn") {
                projected.hasKeyword(spire, Keyword.FIRST_STRIKE) shouldBe false
            }
        }

        test("attacking with the animated land scrys 1") {
            val game = board(activePlayer = 1)
            game.animate()

            game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
            withClue("declare attackers failed") {
                game.declareAttackers(mapOf("Restless Spire" to 2)).error shouldBe null
            }

            var sawScry = false
            var guard = 0
            game.resolveStack()
            while (game.getPendingDecision() != null && guard++ < 8) {
                when (game.getPendingDecision()) {
                    is SelectCardsDecision -> { sawScry = true; game.skipSelection() }
                    is ReorderLibraryDecision -> game.keepLibraryOrder()
                    else -> break
                }
                game.resolveStack()
            }

            withClue("\"Whenever this land attacks, scry 1\" fired") { sawScry shouldBe true }
        }
    }
}
