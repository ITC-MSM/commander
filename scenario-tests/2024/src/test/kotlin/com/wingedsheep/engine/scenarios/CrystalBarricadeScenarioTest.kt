package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.state.components.battlefield.DamageComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Crystal Barricade (FDN #7).
 *
 * {1}{W} Artifact Creature — Wall 0/4
 * "Defender
 *  You have hexproof.
 *  Prevent all noncombat damage that would be dealt to other creatures you control."
 *
 * Covers the prevention shield (applies to *other* creatures you control, not the Barricade
 * itself, not the opponent's creatures) and the controller-hexproof static.
 */
class CrystalBarricadeScenarioTest : ScenarioTestBase() {

    private fun damageOn(game: TestGame, name: String): Int =
        game.state.getEntity(game.findPermanent(name)!!)?.get<DamageComponent>()?.amount ?: 0

    init {
        context("Crystal Barricade") {

            test("noncombat damage to another creature you control is fully prevented") {
                val game = scenario()
                    .withPlayers()
                    .withCardOnBattlefield(1, "Crystal Barricade")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardInHand(2, "Shock")
                    .withLandsOnBattlefield(2, "Mountain", 2)
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                game.castSpell(2, "Shock", bears).error shouldBe null
                game.resolveStack()

                // Shock's 2 damage is prevented outright — the 2/2 survives unmarked.
                game.isOnBattlefield("Grizzly Bears") shouldBe true
                damageOn(game, "Grizzly Bears") shouldBe 0
            }

            test("the Barricade does not prevent damage dealt to itself") {
                val game = scenario()
                    .withPlayers()
                    .withCardOnBattlefield(1, "Crystal Barricade")
                    .withCardInHand(2, "Shock")
                    .withLandsOnBattlefield(2, "Mountain", 2)
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val barricade = game.findPermanent("Crystal Barricade")!!
                game.castSpell(2, "Shock", barricade).error shouldBe null
                game.resolveStack()

                // "other creatures you control" excludes the Barricade — the 0/4 takes the 2.
                game.isOnBattlefield("Crystal Barricade") shouldBe true
                damageOn(game, "Crystal Barricade") shouldBe 2
            }

            test("creatures the opponent controls are not shielded") {
                val game = scenario()
                    .withPlayers()
                    .withCardOnBattlefield(1, "Crystal Barricade")
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withCardInHand(1, "Shock")
                    .withLandsOnBattlefield(1, "Mountain", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                game.castSpell(1, "Shock", bears).error shouldBe null
                game.resolveStack()

                game.isOnBattlefield("Grizzly Bears") shouldBe false
            }

            test("you have hexproof — an opponent's spell can't target you") {
                val game = scenario()
                    .withPlayers()
                    .withCardOnBattlefield(1, "Crystal Barricade")
                    .withCardInHand(2, "Shock")
                    .withLandsOnBattlefield(2, "Mountain", 2)
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val shock = game.findCardsInHand(2, "Shock").single()
                val atYou = game.execute(
                    CastSpell(
                        playerId = game.player2Id,
                        cardId = shock,
                        targets = listOf(ChosenTarget.Player(game.player1Id)),
                    )
                )
                (atYou.error != null) shouldBe true

                // The Barricade's controller can still target themselves.
                val atSelf = game.execute(
                    CastSpell(
                        playerId = game.player2Id,
                        cardId = shock,
                        targets = listOf(ChosenTarget.Player(game.player2Id)),
                    )
                )
                atSelf.error shouldBe null
            }
        }
    }
}
