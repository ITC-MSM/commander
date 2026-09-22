package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.mtg.sets.definitions.fra.cards.EardrumRattler
import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.matchers.shouldBe
import io.kotest.matchers.nulls.shouldNotBeNull

class EardrumRattlerScenarioTest : ScenarioTestBase() {
    init {
        test("another small creature cannot be blocked until cleanup") {
            val game = scenario().withPlayers()
                .withCardOnBattlefield(1, "Eardrum Rattler")
                .withCardOnBattlefield(1, "Grizzly Bears")
                .withLandsOnBattlefield(1, "Mountain", 1)
                .withCardInLibrary(1, "Mountain").withCardInLibrary(2, "Forest")
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()
            val bear = game.findPermanent("Grizzly Bears")!!
            game.execute(ActivateAbility(game.player1Id, game.findPermanent("Eardrum Rattler")!!,
                EardrumRattler.script.activatedAbilities.single().id,
                targets = listOf(ChosenTarget.Permanent(bear)))).error shouldBe null
            game.resolveStack()
            game.state.projectedState.hasKeyword(bear, AbilityFlag.CANT_BE_BLOCKED) shouldBe true
            game.passUntilPhase(Phase.ENDING, Step.END)
            game.passUntilPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
            game.state.projectedState.hasKeyword(bear, AbilityFlag.CANT_BE_BLOCKED) shouldBe false
        }

        listOf("self", "opponent", "boosted").forEach { invalid ->
            test("rejects $invalid target using projected power") {
                val builder = scenario().withPlayers()
                    .withCardOnBattlefield(1, "Eardrum Rattler")
                    .withCardOnBattlefield(if (invalid == "opponent") 2 else 1, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                if (invalid == "boosted") builder.withCardOnBattlefield(1, "Glorious Anthem")
                val game = builder.build()
                val source = game.findPermanent("Eardrum Rattler")!!
                val target = if (invalid == "self") source else game.findPermanent("Grizzly Bears")!!
                game.execute(ActivateAbility(game.player1Id, source,
                    EardrumRattler.script.activatedAbilities.single().id,
                    targets = listOf(ChosenTarget.Permanent(target)))).error.shouldNotBeNull()
            }
        }
    }
}
