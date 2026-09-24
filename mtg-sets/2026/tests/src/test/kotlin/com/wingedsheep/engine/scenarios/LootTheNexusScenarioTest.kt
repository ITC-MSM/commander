package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.ChooseColorDecision
import com.wingedsheep.engine.core.ColorChosenResponse
import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Loot, the Nexus (FRA #262) — "{T}: Choose a color. Add one mana of that color for each different
 * power among creatures you control."
 */
class LootTheNexusScenarioTest : ScenarioTestBase() {
    init {
        val manaAbilityId = cardRegistry.getCard("Loot, the Nexus")!!.script.activatedAbilities.single().id

        test("adds one mana of the chosen color per distinct power among your creatures") {
            // Loot (2), two Grizzly Bears (2), Hill Giant (3) → powers {2, 3} = 2.
            // The opponent's Serra Angel (4) doesn't count.
            val game = scenario().withPlayers()
                .withCardOnBattlefield(1, "Loot, the Nexus")
                .withCardOnBattlefield(1, "Grizzly Bears")
                .withCardOnBattlefield(1, "Hill Giant")
                .withCardOnBattlefield(1, "Grizzly Bears")
                .withCardOnBattlefield(2, "Serra Angel")
                .withCardInLibrary(1, "Forest")
                .withCardInLibrary(2, "Forest")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()
            val loot = game.findPermanent("Loot, the Nexus")!!

            game.execute(ActivateAbility(game.player1Id, loot, manaAbilityId)).error shouldBe null
            val decision = game.getPendingDecision().shouldBeInstanceOf<ChooseColorDecision>()
            game.submitDecision(ColorChosenResponse(decision.id, Color.RED)).error shouldBe null

            val pool = game.state.getEntity(game.player1Id)?.get<ManaPoolComponent>()
            withClue("pool=$pool") { pool?.red shouldBe 2 }
        }
    }
}
