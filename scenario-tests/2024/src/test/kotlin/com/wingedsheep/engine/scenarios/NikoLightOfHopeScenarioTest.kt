package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Niko, Light of Hope (DSK #224, {2}{W}{U} Legendary 3/4 Human Wizard).
 *
 *   When Niko enters, create two Shard tokens.
 *   {2}, {T}: Exile target nonlegendary creature you control. Shards you control become copies of
 *   it until the next end step. Return it to the battlefield under its owner's control at the
 *   beginning of the next end step.
 *
 * Exercises the new [com.wingedsheep.sdk.scripting.Duration.UntilNextEndStep] copy duration: the
 * Shards become copies of the exiled creature and revert — alongside the creature's return — on
 * entry to the next end step.
 */
class NikoLightOfHopeScenarioTest : ScenarioTestBase() {

    init {
        test("ETB creates two Shard tokens") {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withCardInHand(1, "Niko, Light of Hope")
                .withLandsOnBattlefield(1, "Plains", 2)
                .withLandsOnBattlefield(1, "Island", 2)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val cast = game.castSpell(1, "Niko, Light of Hope")
            withClue("Casting Niko should succeed: ${cast.error}") { cast.error shouldBe null }
            game.resolveStack()

            withClue("Niko's ETB makes two Shard tokens") {
                game.findPermanents("Shard").size shouldBe 2
            }
        }

        test("activated ability: exile a creature, Shards copy it, both revert at the next end step") {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withCardOnBattlefield(1, "Niko, Light of Hope", summoningSickness = false)
                .withCardOnBattlefield(1, "Shard")
                .withCardOnBattlefield(1, "Shard")
                .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                .withLandsOnBattlefield(1, "Island", 2)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val niko = game.findPermanent("Niko, Light of Hope")!!
            val bears = game.findPermanent("Grizzly Bears")!!
            val shardIds = game.findPermanents("Shard")
            shardIds.size shouldBe 2

            val ability = cardRegistry.getCard("Niko, Light of Hope")!!.script.activatedAbilities[0]
            val result = game.execute(
                ActivateAbility(
                    playerId = game.player1Id,
                    sourceId = niko,
                    abilityId = ability.id,
                    targets = listOf(ChosenTarget.Permanent(bears)),
                )
            )
            withClue("Activating Niko's ability should succeed: ${result.error}") {
                result.error shouldBe null
            }
            game.resolveStack()

            // The targeted creature is exiled (the entity itself, not a copy of its name).
            withClue("Grizzly Bears is exiled") {
                (bears in game.state.getBattlefield()) shouldBe false
                (bears in game.state.getExile(game.player1Id)) shouldBe true
            }

            // Both Shards are now copies of Grizzly Bears (2/2), read from exile (CR 707).
            shardIds.forEach { id ->
                withClue("Shard copied Grizzly Bears") {
                    game.state.getEntity(id)?.get<CardComponent>()?.name shouldBe "Grizzly Bears"
                    game.state.projectedState.getPower(id) shouldBe 2
                    game.state.projectedState.getToughness(id) shouldBe 2
                }
            }
            withClue("On the battlefield there are now two creatures named Grizzly Bears (the Shard copies)") {
                game.findPermanents("Grizzly Bears").size shouldBe 2
            }

            // Advance to the end step. The copy duration wears off on entry to the step; the delayed
            // trigger then returns the exiled creature.
            game.passUntilPhase(Phase.ENDING, Step.END)
            game.resolveStack()

            withClue("Grizzly Bears returned to the battlefield (and is no longer in exile)") {
                game.findPermanents("Grizzly Bears").size shouldBe 1
                game.state.getExile(game.player1Id).none {
                    game.state.getEntity(it)?.get<CardComponent>()?.name == "Grizzly Bears"
                } shouldBe true
            }
            withClue("The Shards reverted from copies back to Shard enchantments") {
                val shardsAfter = game.findPermanents("Shard")
                shardsAfter.size shouldBe 2
                shardsAfter.forEach { id ->
                    game.state.getEntity(id)?.get<CardComponent>()?.name shouldBe "Shard"
                }
            }
        }
    }
}
