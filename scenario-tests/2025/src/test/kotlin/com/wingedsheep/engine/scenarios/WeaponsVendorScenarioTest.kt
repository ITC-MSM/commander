package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectManaSourcesDecision
import com.wingedsheep.engine.core.TargetsResponse
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Tests for Weapons Vendor (FIN #40).
 *
 * Weapons Vendor {3}{W} Creature — Human Artificer 2/2
 * When this creature enters, draw a card.
 * At the beginning of combat on your turn, if you control an Equipment, you may pay {1}. When
 * you do, attach target Equipment you control to target creature you control.
 *
 * Verifies the begin-combat reflexive may-pay: paying {1} fires the "when you do" reflexive
 * ability which attaches the chosen Equipment to the chosen creature (Scryfall ruling — targets
 * chosen as the reflexive ability goes on the stack).
 */
class WeaponsVendorScenarioTest : ScenarioTestBase() {

    private val stateProjector = StateProjector()

    init {
        test("paying {1} at combat attaches a controlled Equipment to a controlled creature") {
            val game = scenario()
                .withPlayers()
                .withCardOnBattlefield(1, "Weapons Vendor")
                .withCardOnBattlefield(1, "Buster Sword")   // an Equipment to move (+3/+2)
                .withCardOnBattlefield(1, "Grizzly Bears")   // 2/2 to receive it
                .withLandsOnBattlefield(1, "Plains", 1)      // pays the {1}
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val sword = game.findPermanent("Buster Sword")!!
            val bears = game.findPermanent("Grizzly Bears")!!

            // Step into begin-of-combat; the trigger goes on the stack (no target of its own).
            game.passUntilPhase(Phase.COMBAT, Step.BEGIN_COMBAT)
            game.resolveStack()

            withClue("the begin-combat ability offers the may-pay {1}") {
                (game.getPendingDecision() is YesNoDecision) shouldBe true
            }
            game.answerYesNo(true).error shouldBe null
            withClue("paying then asks which mana sources to use") {
                (game.getPendingDecision() is SelectManaSourcesDecision) shouldBe true
            }
            game.submitManaSourcesAutoPay().error shouldBe null

            // The reflexive "when you do" ability now chooses its two targets:
            // slot 0 = Equipment, slot 1 = creature.
            withClue("reflexive ability pauses to choose Equipment + creature targets") {
                game.hasPendingDecision() shouldBe true
            }
            val td = game.getPendingDecision()!!
            game.submitDecision(TargetsResponse(td.id, mapOf(0 to listOf(sword), 1 to listOf(bears))))
            game.resolveStack()

            withClue("Buster Sword is now attached to Grizzly Bears (2/2 + 3/+2 = 5/4)") {
                stateProjector.project(game.state).getPower(bears) shouldBe 5
                stateProjector.project(game.state).getToughness(bears) shouldBe 4
            }
        }
    }
}
