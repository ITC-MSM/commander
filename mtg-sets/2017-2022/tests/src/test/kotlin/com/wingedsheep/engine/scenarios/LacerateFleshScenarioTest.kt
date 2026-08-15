package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Lacerate Flesh (VOW #166) — {4}{R} Sorcery
 *
 *   Lacerate Flesh deals 4 damage to target creature. Create a number of Blood tokens equal to the
 *   amount of excess damage dealt to that creature this way.
 *
 * Exercises the excess-marked-damage → Blood tokens composite (CR 120.4a): excess is
 * `max(0, 4 − toughness)`, evaluated after the 4 damage is marked but before SBAs, so a 2/2 yields
 * 2 Blood while a 5/5 yields none.
 */
class LacerateFleshScenarioTest : ScenarioTestBase() {

    init {
        context("Lacerate Flesh — 4 damage, Blood tokens equal to excess") {

            test("2/2 target: 4 damage, 2 excess -> 2 Blood tokens") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Lacerate Flesh")
                    .withLandsOnBattlefield(1, "Mountain", 5)
                    .withCardOnBattlefield(2, "Grizzly Bears") // 2/2
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                game.findPermanents("Blood").size shouldBe 0

                game.castSpell(1, "Lacerate Flesh", targetId = bears).error shouldBe null
                game.resolveStack()

                withClue("2/2 hit for 4 => 2 excess => 2 Blood tokens") {
                    game.findPermanents("Blood").size shouldBe 2
                }
                withClue("the 2/2 died to lethal damage") {
                    game.isInGraveyard(2, "Grizzly Bears") shouldBe true
                }
            }

            test("5/5 target: 4 damage, 0 excess -> no Blood tokens, creature survives") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Lacerate Flesh")
                    .withLandsOnBattlefield(1, "Mountain", 5)
                    .withCardOnBattlefield(2, "Force of Nature") // 5/5
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val fon = game.findPermanent("Force of Nature")!!

                game.castSpell(1, "Lacerate Flesh", targetId = fon).error shouldBe null
                game.resolveStack()

                withClue("5/5 hit for 4 => 0 excess => no Blood tokens") {
                    game.findPermanents("Blood").size shouldBe 0
                }
                withClue("the 5/5 survived (4 < 5 toughness)") {
                    game.isOnBattlefield("Force of Nature") shouldBe true
                }
            }
        }
    }
}
