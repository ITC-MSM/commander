package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Fall of Cair Andros (LTR #124) — {2}{R} Enchantment.
 *
 * "Whenever a creature an opponent controls is dealt excess noncombat damage, amass Orcs
 *  X, where X is that excess damage."
 *
 * "{7}{R}: This enchantment deals 7 damage to target creature."
 *
 * Exercises the Gap 12 excess-damage primitive end-to-end on a real card: when Fall of
 * Cair Andros' own activated ability deals 7 damage to an opponent's small creature, the
 * trigger reads the excess via `ContextPropertyKey.TRIGGER_EXCESS_DAMAGE_AMOUNT` and
 * amasses for that amount.
 */
class FallOfCairAndrosScenarioTest : ScenarioTestBase() {

    private val projector = StateProjector()

    init {
        context("Fall of Cair Andros") {

            test("activated ability dealing 7 to an opponent's 2/2 triggers amass for 5 (the excess)") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Fall of Cair Andros")
                    .withLandsOnBattlefield(1, "Mountain", 8)
                    .withCardOnBattlefield(2, "Grizzly Bears") // 2/2
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val fall = game.findPermanent("Fall of Cair Andros")!!
                val bear = game.findPermanent("Grizzly Bears")!!
                val damageAbility = cardRegistry.getCard("Fall of Cair Andros")!!
                    .script.activatedAbilities[0]
                val activateResult = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = fall,
                        abilityId = damageAbility.id,
                        targets = listOf(ChosenTarget.Permanent(bear))
                    )
                )
                withClue("Activating Fall of Cair Andros' damage ability should succeed: ${activateResult.error}") {
                    activateResult.error shouldBe null
                }
                game.resolveStack()

                // 7 noncombat damage to a 2-toughness creature = 5 excess (CR 120.4a).
                // Bear dies (lethal). Triggered ability fires "amass Orcs X" with X = 5.
                withClue("Grizzly Bears should be dead") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe false
                }
                val army = game.findPermanent("Orc Army")
                    ?: error("The excess-damage trigger should have created an Orc Army")
                val counters = game.state.getEntity(army)?.get<CountersComponent>()
                withClue("Amass Orcs X with X = 5 puts five +1/+1 counters on the Army") {
                    counters?.getCount(CounterType.PLUS_ONE_PLUS_ONE) shouldBe 5
                }
                withClue("Projected power of a 0/0 token with five +1/+1 counters is 5") {
                    projector.project(game.state).getPower(army) shouldBe 5
                }
            }

            test("activated damage to an opponent's 3/3 triggers amass for 4 (the excess)") {
                // 7 damage to a 3-toughness creature = 4 excess. Pairs with the 2/2 case
                // above to confirm the trigger reads the per-event excess, not a constant.
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Fall of Cair Andros")
                    .withLandsOnBattlefield(1, "Mountain", 8)
                    .withCardOnBattlefield(2, "Hill Giant") // 3/3
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val fall = game.findPermanent("Fall of Cair Andros")!!
                val giant = game.findPermanent("Hill Giant")!!
                val damageAbility = cardRegistry.getCard("Fall of Cair Andros")!!
                    .script.activatedAbilities[0]
                game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = fall,
                        abilityId = damageAbility.id,
                        targets = listOf(ChosenTarget.Permanent(giant))
                    )
                )
                game.resolveStack()

                val army = game.findPermanent("Orc Army")
                    ?: error("4 excess should fire the trigger")
                val counters = game.state.getEntity(army)?.get<CountersComponent>()
                counters?.getCount(CounterType.PLUS_ONE_PLUS_ONE) shouldBe 4
            }
        }
    }
}
