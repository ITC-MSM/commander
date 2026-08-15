package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Tests for Diamond Weapon (FIN #183).
 *
 * Diamond Weapon {7}{G}{G} Legendary Artifact Creature — Elemental 8/8
 * This spell costs {1} less to cast for each permanent card in your graveyard.
 * Reach
 * Immune — Prevent all combat damage that would be dealt to Diamond Weapon.
 *
 * Verifies the graveyard cost reduction: with three permanent cards in the graveyard, the
 * {7}{G}{G} (9 mana) spell is castable off only six lands. (The combat-damage prevention uses
 * the same self-recipient PreventDamage replacement proven by Argothian Treefolk.)
 */
class DiamondWeaponScenarioTest : ScenarioTestBase() {

    init {
        test("costs {1} less for each permanent card in the graveyard") {
            val game = scenario()
                .withPlayers()
                // Three permanent (creature) cards in the graveyard -> {3} reduction.
                .withCardInGraveyard(1, "Grizzly Bears")
                .withCardInGraveyard(1, "Hill Giant")
                .withCardInGraveyard(1, "Cargo Ship")
                .withCardInHand(1, "Diamond Weapon")
                // Only six lands: enough for the reduced {4}{G}{G}, far short of the full {7}{G}{G}.
                .withLandsOnBattlefield(1, "Forest", 6)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val cast = game.castSpell(1, "Diamond Weapon")
            withClue("Diamond Weapon should be castable off six lands thanks to the {3} reduction: ${cast.error}") {
                cast.error shouldBe null
            }
            game.resolveStack()

            withClue("Diamond Weapon resolves onto the battlefield") {
                game.isOnBattlefield("Diamond Weapon") shouldBe true
            }
        }
    }
}
