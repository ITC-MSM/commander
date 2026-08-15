package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Dub (DOM #15) — {2}{W} Enchantment — Aura.
 *
 * "Enchant creature
 *  Enchanted creature gets +2/+2, has first strike, and is a Knight in addition to its
 *  other types."
 *
 * Pins the whole continuous grant, in particular the added Knight subtype: `GrantSubtype`
 * defaults its filter to the *source*, so an Aura that omits the filter silently makes
 * itself the Knight instead of the creature it enchants.
 */
class DubScenarioTest : ScenarioTestBase() {

    private val projector = StateProjector()

    init {
        context("Dub") {

            test("grants +2/+2, first strike, and the Knight type to the enchanted creature") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardAttachedTo(1, "Dub", "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                val dub = game.findPermanent("Dub")!!
                val projected = projector.project(game.state)

                withClue("2/2 base plus +2/+2") {
                    projected.getPower(bears) shouldBe 4
                    projected.getToughness(bears) shouldBe 4
                }
                projected.hasKeyword(bears, Keyword.FIRST_STRIKE) shouldBe true
                withClue("Knight is added, the printed Bear type is kept") {
                    projected.hasSubtype(bears, "Knight") shouldBe true
                    projected.hasSubtype(bears, "Bear") shouldBe true
                }
                withClue("the Aura itself does not become the Knight") {
                    projected.hasSubtype(dub, "Knight") shouldBe false
                }
            }
        }
    }
}
