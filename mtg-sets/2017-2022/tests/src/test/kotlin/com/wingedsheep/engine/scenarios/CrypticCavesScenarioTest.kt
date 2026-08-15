package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Cryptic Caves (M20 #244, reprinted FDN #771).
 *
 * "{T}: Add {C}.
 *  {1}, {T}, Sacrifice this land: Draw a card. Activate only if you control five or more lands."
 *
 * Covers the activation restriction in both directions — blocked at four lands, allowed at five
 * (the Caves counts itself, since it is still on the battlefield when legality is checked).
 */
class CrypticCavesScenarioTest : ScenarioTestBase() {

    private val drawAbilityId by lazy {
        cardRegistry.getCard("Cryptic Caves")!!.script.activatedAbilities[1].id
    }

    init {
        context("Cryptic Caves") {

            test("draws a card and sacrifices itself when you control five or more lands") {
                var builder = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Cryptic Caves")
                    // Four Forests + the Caves = five lands, and enough mana for the {1}.
                    .withLandsOnBattlefield(1, "Forest", 4)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                repeat(4) { builder = builder.withCardInLibrary(1, "Forest") }
                repeat(4) { builder = builder.withCardInLibrary(2, "Forest") }
                val game = builder.build()

                val handBefore = game.handSize(1)
                val librarySizeBefore = game.librarySize(1)
                val caves = game.findPermanent("Cryptic Caves")!!

                val result = game.execute(
                    ActivateAbility(playerId = game.player1Id, sourceId = caves, abilityId = drawAbilityId)
                )
                withClue("Activating with five lands should succeed: ${result.error}") {
                    result.error shouldBe null
                }
                game.resolveStack()

                withClue("Cryptic Caves sacrificed itself as part of the cost") {
                    game.isOnBattlefield("Cryptic Caves") shouldBe false
                    game.isInGraveyard(1, "Cryptic Caves") shouldBe true
                }
                withClue("A card was drawn") {
                    game.handSize(1) shouldBe handBefore + 1
                    game.librarySize(1) shouldBe librarySizeBefore - 1
                }
            }

            test("cannot be activated while you control only four lands") {
                var builder = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Cryptic Caves")
                    // Three Forests + the Caves = four lands — one short.
                    .withLandsOnBattlefield(1, "Forest", 3)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                repeat(4) { builder = builder.withCardInLibrary(1, "Forest") }
                repeat(4) { builder = builder.withCardInLibrary(2, "Forest") }
                val game = builder.build()

                val handBefore = game.handSize(1)
                val caves = game.findPermanent("Cryptic Caves")!!

                val result = game.execute(
                    ActivateAbility(playerId = game.player1Id, sourceId = caves, abilityId = drawAbilityId)
                )
                withClue("Activation should be rejected below five lands") {
                    (result.error != null) shouldBe true
                }
                withClue("Nothing happened — no draw, and the Caves is still on the battlefield") {
                    game.handSize(1) shouldBe handBefore
                    game.isOnBattlefield("Cryptic Caves") shouldBe true
                }
            }
        }
    }
}
