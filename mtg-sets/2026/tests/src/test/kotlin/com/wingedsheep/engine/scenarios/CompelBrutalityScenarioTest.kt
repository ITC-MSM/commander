package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.battlefield.DamageComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Compel Brutality (FRA #101, {1}{G} Instant).
 *
 *   Choose one —
 *   • Target creature you control deals damage equal to its power to target creature or
 *     planeswalker an opponent controls.
 *   • Target planeswalker you control deals damage equal to its loyalty to target creature or
 *     planeswalker an opponent controls.
 */
class CompelBrutalityScenarioTest : ScenarioTestBase() {

    init {
        context("Compel Brutality") {

            test("mode 1: your creature deals damage equal to its power, one-sided") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Hill Giant")
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withCardInHand(1, "Compel Brutality")
                    .withLandsOnBattlefield(1, "Forest", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val giant = game.findPermanent("Hill Giant")!!
                val bears = game.findPermanent("Grizzly Bears")!!
                cast(game, modeIndex = 0, giant, bears)
                game.resolveStack()

                game.isOnBattlefield("Grizzly Bears").shouldBeFalse()
                withClue("the Bears deal no damage back") {
                    (game.state.getEntity(giant)?.get<DamageComponent>()?.amount ?: 0) shouldBe 0
                }
            }

            test("mode 2: your planeswalker deals damage equal to its loyalty") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Chandra, Torch of Defiance")
                    .withCardOnBattlefield(2, "Serra Angel")
                    .withCardInHand(1, "Compel Brutality")
                    .withLandsOnBattlefield(1, "Forest", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val chandra = game.findPermanent("Chandra, Torch of Defiance")!!
                game.state = game.state.updateEntity(chandra) { c ->
                    c.with(CountersComponent(mapOf(CounterType.LOYALTY to 4)))
                }
                val angel = game.findPermanent("Serra Angel")!!
                cast(game, modeIndex = 1, chandra, angel)
                game.resolveStack()

                withClue("4 loyalty = 4 damage kills the 4/4 Angel") {
                    game.isOnBattlefield("Serra Angel").shouldBeFalse()
                }
                withClue("Chandra's loyalty is unchanged") {
                    game.state.getEntity(chandra)?.get<CountersComponent>()?.getCount(CounterType.LOYALTY) shouldBe 4
                }
            }

            test("mode 1 can't have its damage source be an opponent's creature") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(2, "Hill Giant")
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withCardInHand(1, "Compel Brutality")
                    .withLandsOnBattlefield(1, "Forest", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val giant = game.findPermanent("Hill Giant")!!
                val bears = game.findPermanent("Grizzly Bears")!!
                val result = game.execute(castAction(game, 0, giant, bears))
                result.error shouldNotBe null
            }
        }
    }

    private fun castAction(game: TestGame, modeIndex: Int, source: EntityId, victim: EntityId): CastSpell {
        val card = game.state.getHand(game.player1Id).first {
            game.state.getEntity(it)?.get<CardComponent>()?.name == "Compel Brutality"
        }
        val targets = listOf(ChosenTarget.Permanent(source), ChosenTarget.Permanent(victim))
        return CastSpell(
            game.player1Id,
            card,
            targets,
            chosenModes = listOf(modeIndex),
            modeTargetsOrdered = listOf(targets),
        )
    }

    private fun cast(game: TestGame, modeIndex: Int, source: EntityId, victim: EntityId) {
        game.execute(castAction(game, modeIndex, source, victim)).error shouldBe null
        game.hasPendingDecision().shouldBeFalse()
    }
}
