package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.mtg.sets.definitions.m20.cards.GoblinSmuggler
import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Goblin Smuggler ({2}{R}, 2/2 Goblin Rogue, Haste).
 *
 * {T}: Another target creature with power 2 or less can't be blocked this turn.
 *
 * Verifies the tap-activated ability grants [AbilityFlag.CANT_BE_BLOCKED] to a legal target,
 * that the power-2-or-less restriction rejects a bigger creature, and that "another" excludes
 * the Smuggler itself.
 */
class GoblinSmugglerScenarioTest : ScenarioTestBase() {

    private val abilityId = GoblinSmuggler.activatedAbilities.first().id

    init {
        context("Goblin Smuggler") {

            test("grants can't-be-blocked to another creature with power 2 or less") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Goblin Smuggler", summoningSickness = false)
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val smuggler = game.findPermanent("Goblin Smuggler")!!
                val bears = game.findPermanent("Grizzly Bears")!!

                withClue("Grizzly Bears is not evasive before the ability") {
                    game.state.projectedState.hasKeyword(bears, AbilityFlag.CANT_BE_BLOCKED) shouldBe false
                }

                val result = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = smuggler,
                        abilityId = abilityId,
                        targets = listOf(ChosenTarget.Permanent(bears)),
                    )
                )
                withClue("activating targeting a power-2 creature is legal: ${result.error}") {
                    result.error shouldBe null
                }
                game.resolveStack()

                withClue("the targeted creature can't be blocked this turn") {
                    game.state.projectedState.hasKeyword(bears, AbilityFlag.CANT_BE_BLOCKED) shouldBe true
                }
            }

            test("legal targets are power-2-or-less creatures, excluding itself") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Goblin Smuggler", summoningSickness = false)
                    .withCardOnBattlefield(1, "Grizzly Bears") // power 2 → eligible
                    .withCardOnBattlefield(2, "Hill Giant") // power 3 → too big
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val smuggler = game.findPermanent("Goblin Smuggler")!!
                val bears = game.findPermanent("Grizzly Bears")!!
                val giant = game.findPermanent("Hill Giant")!!

                val ability = game.getLegalActions(1).single { info ->
                    val act = info.action
                    act is ActivateAbility && act.sourceId == smuggler && act.abilityId == abilityId
                }
                val validTargets = ability.validTargets ?: emptyList()

                withClue("power-2 Grizzly Bears is a legal target: $validTargets") {
                    (bears in validTargets) shouldBe true
                }
                withClue("power-3 Hill Giant exceeds 'power 2 or less': $validTargets") {
                    (giant in validTargets) shouldBe false
                }
                withClue("'another' excludes the Smuggler itself: $validTargets") {
                    (smuggler in validTargets) shouldBe false
                }
            }
        }
    }
}
