package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * New Horizons (XLN #198, reprinted as FDN #557) — {2}{G} Enchantment — Aura.
 *
 * "Enchant land
 *  When this Aura enters, put a +1/+1 counter on target creature you control.
 *  Enchanted land has \"{T}: Add two mana of any one color.\""
 *
 * Pins the ETB counter trigger and the granted mana ability. The granted ability is a
 * *second* mana ability on the land — it does not replace the land's own tap-for-mana —
 * so the enchanted Forest surfaces two distinct activations.
 */
class NewHorizonsScenarioTest : ScenarioTestBase() {

    init {
        context("New Horizons") {

            test("its enters trigger puts a +1/+1 counter on a target creature you control") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Forest", 4)
                    .withCardInHand(1, "New Horizons")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val forest = game.findPermanent("Forest")!!
                val bears = game.findPermanent("Grizzly Bears")!!
                game.castSpell(1, "New Horizons", forest).error shouldBe null
                // The Aura resolves and enters; its ETB trigger then asks for a target — object
                // targets are never auto-chosen, even when only one is legal.
                game.resolveStack()
                game.selectTargets(listOf(bears)).error shouldBe null
                game.resolveStack()

                withClue("the Aura's enters trigger targets the only creature its controller has") {
                    game.state.getEntity(bears)?.get<CountersComponent>()
                        ?.getCount(CounterType.PLUS_ONE_PLUS_ONE) shouldBe 1
                }
            }

            test("the enchanted land gains a second tap-for-mana ability") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withLandsOnBattlefield(1, "Forest", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val forest = game.findPermanent("Forest")!!
                val before = game.getLegalActions(1)
                    .count { (it.action as? ActivateAbility)?.sourceId == forest }

                val enchanted = scenario()
                    .withPlayers("Player", "Opponent")
                    .withLandsOnBattlefield(1, "Forest", 1)
                    .withCardAttachedTo(1, "New Horizons", "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val enchantedForest = enchanted.findPermanent("Forest")!!
                val after = enchanted.getLegalActions(1)
                    .count { (it.action as? ActivateAbility)?.sourceId == enchantedForest }

                withClue("New Horizons adds an activation the bare Forest doesn't have") {
                    after shouldBe before + 1
                }
                enchanted.findPermanent("New Horizons") shouldNotBe null
            }
        }
    }
}
