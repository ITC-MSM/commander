package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Golgothian Sylex — "{1}, {T}: Each nontoken permanent with a name originally printed in the
 * Antiquities expansion is sacrificed by its controller."
 *
 * Proves the `OriginallyPrintedInSet("ATQ")` set-membership filter + `SacrificeAll`:
 *  - ATQ-printed nontoken permanents (both players') are sacrificed,
 *  - non-ATQ permanents survive,
 *  - Golgothian Sylex sacrifices itself too (its name was originally printed in ATQ and
 *    the oracle text has no self-exclusion),
 *  - a token (even of an ATQ permanent's name) survives, since tokens have no original set.
 */
class GolgothianSylexScenarioTest : ScenarioTestBase() {

    private val sylexAbilityId by lazy {
        cardRegistry.getCard("Golgothian Sylex")!!.activatedAbilities[0].id
    }

    init {
        context("Golgothian Sylex set-membership mass sacrifice") {
            test("sacrifices all ATQ-printed nontoken permanents, including itself, sparing non-ATQ") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Golgothian Sylex", tapped = false, summoningSickness = false)
                    // ATQ permanents (should be sacrificed) — one each side, different types.
                    .withCardOnBattlefield(1, "Ornithopter", summoningSickness = false) // ATQ artifact creature
                    .withCardOnBattlefield(2, "Yotian Soldier", summoningSickness = false) // ATQ artifact creature
                    // Non-ATQ permanent (should survive).
                    .withCardOnBattlefield(2, "Grizzly Bears", summoningSickness = false)
                    .withLandsOnBattlefield(1, "Island", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val sylex = game.findPermanent("Golgothian Sylex")!!
                val result = game.execute(ActivateAbility(game.player1Id, sylex, sylexAbilityId))
                withClue("Activating Golgothian Sylex should succeed: ${result.error}") {
                    result.error shouldBe null
                }
                game.resolveStack()

                withClue("Ornithopter (ATQ) should be sacrificed") {
                    game.isOnBattlefield("Ornithopter") shouldBe false
                    game.isInGraveyard(1, "Ornithopter") shouldBe true
                }
                withClue("Yotian Soldier (ATQ) should be sacrificed by its controller (P2)") {
                    game.isOnBattlefield("Yotian Soldier") shouldBe false
                    game.isInGraveyard(2, "Yotian Soldier") shouldBe true
                }
                withClue("Grizzly Bears (non-ATQ) should survive") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe true
                }
                withClue("Golgothian Sylex sacrifices itself (its name is ATQ-printed; no self-exclusion in oracle)") {
                    game.isOnBattlefield("Golgothian Sylex") shouldBe false
                    game.isInGraveyard(1, "Golgothian Sylex") shouldBe true
                }
            }
        }
    }
}
