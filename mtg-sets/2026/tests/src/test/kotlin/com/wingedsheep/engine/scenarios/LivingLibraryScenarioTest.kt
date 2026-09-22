package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.mtg.sets.definitions.fra.cards.LivingLibrary
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.matchers.shouldBe
import io.kotest.matchers.nulls.shouldNotBeNull

class LivingLibraryScenarioTest : ScenarioTestBase() {
    init {
        listOf("Grizzly Bears", "Jace Beleren").forEach { targetName ->
            test("sacrifices as a cost and shuffles opposing $targetName into its owner's library") {
                val game = scenario().withPlayers()
                    .withCardOnBattlefield(1, "Living Library")
                    .withCardOnBattlefield(2, targetName)
                    .withLandsOnBattlefield(1, "Island", 6)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()
                val source = game.findPermanent("Living Library")!!
                val target = game.findPermanent(targetName)!!
                game.execute(ActivateAbility(game.player1Id, source,
                    LivingLibrary.script.activatedAbilities.single().id,
                    targets = listOf(ChosenTarget.Permanent(target)))).error shouldBe null
                game.isInGraveyard(1, "Living Library") shouldBe true
                game.resolveStack()
                game.state.getLibrary(game.player2Id).contains(target) shouldBe true
                game.findPermanent(targetName) shouldBe null
            }
        }

        test("cannot target your own creature") {
            val game = scenario().withPlayers()
                .withCardOnBattlefield(1, "Living Library")
                .withCardOnBattlefield(1, "Grizzly Bears")
                .withLandsOnBattlefield(1, "Island", 6)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()
            game.execute(ActivateAbility(game.player1Id, game.findPermanent("Living Library")!!,
                LivingLibrary.script.activatedAbilities.single().id,
                targets = listOf(ChosenTarget.Permanent(game.findPermanent("Grizzly Bears")!!)))).error.shouldNotBeNull()
            game.isInGraveyard(1, "Living Library") shouldBe false
        }
    }
}
