package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.SelectManaSourcesDecision
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import io.kotest.matchers.shouldBe

/**
 * Yoshimaru, Beloved Companion (FRA #209) — {2}{W} Legendary Creature — Dog 2/2.
 *
 *   If one or more +1/+1 counters would be put on a creature you control, that many plus one
 *   +1/+1 counters are put on it instead.
 *   {6}: Put a +1/+1 counter on target legendary creature.
 *
 * The {6} ability only targets legendary creatures; the replacement adds one to a counter placed
 * on your own creature, and does nothing for an opponent's.
 */
class YoshimaruBelovedCompanionScenarioTest : ScenarioTestBase() {

    private fun plusOnes(game: TestGame, id: EntityId): Int =
        game.state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    private fun activate(game: TestGame, target: EntityId) {
        val yoshimaru = game.findPermanent("Yoshimaru, Beloved Companion")!!
        val ability = cardRegistry.requireCard("Yoshimaru, Beloved Companion").activatedAbilities.single().id
        game.execute(
            ActivateAbility(
                playerId = game.player1Id,
                sourceId = yoshimaru,
                abilityId = ability,
                targets = listOf(ChosenTarget.Permanent(target)),
            )
        ).error shouldBe null
        if (game.getPendingDecision() is SelectManaSourcesDecision) game.submitManaSourcesAutoPay()
        game.resolveStack()
    }

    init {
        test("the ability on itself puts two counters on it — the replacement adds one") {
            val game = scenario().withPlayers()
                .withCardOnBattlefield(1, "Yoshimaru, Beloved Companion")
                .withLandsOnBattlefield(1, "Plains", 6)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()

            val yoshimaru = game.findPermanent("Yoshimaru, Beloved Companion")!!
            activate(game, yoshimaru)

            plusOnes(game, yoshimaru) shouldBe 2
            game.state.projectedState.getPower(yoshimaru) shouldBe 4
        }

        test("an opponent's legendary creature gets just the one counter") {
            val game = scenario().withPlayers()
                .withCardOnBattlefield(1, "Yoshimaru, Beloved Companion")
                .withCardOnBattlefield(2, "Yoshimaru, Scrappy Stray")
                .withLandsOnBattlefield(1, "Plains", 6)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()

            val stray = game.findPermanent("Yoshimaru, Scrappy Stray")!!
            activate(game, stray)

            plusOnes(game, stray) shouldBe 1
        }

        test("a nonlegendary creature is not a legal target") {
            val game = scenario().withPlayers()
                .withCardOnBattlefield(1, "Yoshimaru, Beloved Companion")
                .withCardOnBattlefield(1, "Grizzly Bears")
                .withLandsOnBattlefield(1, "Plains", 6)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()

            val yoshimaru = game.findPermanent("Yoshimaru, Beloved Companion")!!
            val bears = game.findPermanent("Grizzly Bears")!!
            val ability = cardRegistry.requireCard("Yoshimaru, Beloved Companion").activatedAbilities.single().id
            val result = game.execute(
                ActivateAbility(
                    playerId = game.player1Id,
                    sourceId = yoshimaru,
                    abilityId = ability,
                    targets = listOf(ChosenTarget.Permanent(bears)),
                )
            )
            (result.error != null) shouldBe true
        }

        test("the replacement applies to counters from other sources on your creatures") {
            val game = scenario().withPlayers()
                .withCardOnBattlefield(1, "Yoshimaru, Beloved Companion")
                .withCardOnBattlefield(1, "Lyra, Archangel of Dawn")
                .withCardInHand(1, "Sacred Nectar")
                .withLandsOnBattlefield(1, "Plains", 2)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()

            game.castSpell(1, "Sacred Nectar").error shouldBe null
            game.resolveStack()

            plusOnes(game, game.findPermanent("Lyra, Archangel of Dawn")!!) shouldBe 2
        }
    }
}
