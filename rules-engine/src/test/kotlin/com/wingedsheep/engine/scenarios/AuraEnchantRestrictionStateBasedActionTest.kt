package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * CR 303.4c/704.5m: an Aura's "enchant" restriction is checked continuously, not only when the
 * Aura spell resolves. Once the permanent it's attached to stops matching that restriction, the
 * Aura is "attached to an illegal object" and is put into its owner's graveyard as a state-based
 * action.
 *
 * The case exercised here is the common one: an Aura that reads "Enchant creature you control"
 * (Cradle of Safety) stays on the battlefield only while its controller still controls the host.
 * An opponent's Act of Treason steals the host, so the Aura falls off — while an unrestricted
 * "Enchant creature" Aura (Holy Strength) on the same creature is untouched, because a creature an
 * opponent controls is still a legal host for it.
 */
class AuraEnchantRestrictionStateBasedActionTest : ScenarioTestBase() {

    init {
        context("an aura whose enchant restriction stops being met falls off") {
            test("stealing the host sends an 'enchant creature you control' aura to the graveyard") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardAttachedTo(1, "Cradle of Safety", "Grizzly Bears")  // enchant creature you control
                    .withCardAttachedTo(1, "Holy Strength", "Grizzly Bears")     // enchant creature
                    .withCardInHand(2, "Act of Treason")
                    .withLandsOnBattlefield(2, "Mountain", 3)
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                val result = game.castSpell(2, "Act of Treason", bears)
                withClue("Player2 should be able to steal the enchanted creature: ${result.error}") {
                    result.error shouldBe null
                }
                game.resolveStack()

                withClue("Act of Treason gave Player2 control of the host") {
                    game.state.projectedState.getController(bears) shouldBe game.player2Id
                }
                withClue("Cradle of Safety enchants 'a creature you control' — its host is now an opponent's, so it falls off") {
                    game.isOnBattlefield("Cradle of Safety") shouldBe false
                    game.isInGraveyard(1, "Cradle of Safety") shouldBe true
                }
                withClue("Holy Strength just enchants 'creature' — a stolen creature is still a legal host") {
                    game.isOnBattlefield("Holy Strength") shouldBe true
                }
                withClue("The host itself is unharmed") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe true
                }
            }

            test("turning the host into a land sends an 'enchant creature' aura to the graveyard") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardAttachedTo(1, "Holy Strength", "Grizzly Bears")  // enchant creature
                    .withCardInHand(2, "Imprisoned in the Moon")
                    .withLandsOnBattlefield(2, "Island", 3)
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                val result = game.castSpell(2, "Imprisoned in the Moon", bears)
                withClue("Imprisoned in the Moon can enchant a creature: ${result.error}") {
                    result.error shouldBe null
                }
                game.resolveStack()

                withClue("The host is now a land, not a creature") {
                    game.state.projectedState.isCreature(bears) shouldBe false
                    game.state.projectedState.hasType(bears, "LAND") shouldBe true
                }
                withClue("Holy Strength enchants a creature — its host is a land now, so it falls off") {
                    game.isOnBattlefield("Holy Strength") shouldBe false
                    game.isInGraveyard(1, "Holy Strength") shouldBe true
                }
                withClue("Imprisoned in the Moon enchants a creature, land, or planeswalker — a land host is still legal") {
                    game.isOnBattlefield("Imprisoned in the Moon") shouldBe true
                }
                withClue("The host itself is unharmed") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe true
                }
            }
        }
    }
}
