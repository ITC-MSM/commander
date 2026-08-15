package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CardsSelectedResponse
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.core.TargetsResponse
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Tests for Beatrix, Loyal General (FIN #554).
 *
 * Beatrix, Loyal General {4}{W}{W} Legendary Creature — Human Soldier 4/4
 * Vigilance
 * At the beginning of combat on your turn, you may attach any number of Equipment you control
 * to target creature you control.
 *
 * The ability is a pure composition: a begin-combat trigger targeting a creature you control,
 * wrapped in a "you may" (a yes/no decided before targeting). On "yes" its resolution gathers the
 * Equipment you control, lets you choose any number (0..all) of them, and attaches each chosen
 * Equipment to the target creature via a ForEach over the selection. These tests prove the
 * multi-attach (two Equipment onto one creature), the zero-choice no-op (the "any number = 0"
 * path), and declining the optional trigger up front.
 */
class BeatrixLoyalGeneralScenarioTest : ScenarioTestBase() {

    private val stateProjector = StateProjector()

    init {
        test("attaches any number of Equipment (two) to the target creature you control") {
            val game = scenario()
                .withPlayers()
                .withCardOnBattlefield(1, "Beatrix, Loyal General", summoningSickness = false)
                .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                .withCardOnBattlefield(1, "Buster Sword")   // +3/+2
                .withCardOnBattlefield(1, "Buster Sword")   // +3/+2
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val bears = game.findPermanent("Grizzly Bears")!!
            val swords = game.findPermanents("Buster Sword")
            swords.size shouldBe 2

            game.passUntilPhase(Phase.COMBAT, Step.BEGIN_COMBAT)

            // The begin-combat "you may" asks yes/no first.
            val mayDecision = game.getPendingDecision()
            withClue("begin-combat trigger asks the 'you may' yes/no") {
                (mayDecision is YesNoDecision) shouldBe true
            }
            game.answerYesNo(true)

            // Then choose the target creature you control (slot 0).
            val targetDecision = game.getPendingDecision()
            targetDecision shouldNotBe null
            game.submitDecision(TargetsResponse(targetDecision!!.id, mapOf(0 to listOf(bears))))
            game.resolveStack()

            // The ability resolves and pauses to choose any number of Equipment you control.
            val selectDecision = game.getPendingDecision()
            withClue("resolution pauses to choose any number of Equipment") {
                (selectDecision is SelectCardsDecision) shouldBe true
            }
            (selectDecision as SelectCardsDecision).minSelections shouldBe 0
            game.submitDecision(CardsSelectedResponse(selectDecision.id, swords))
            game.resolveStack()

            withClue("both Buster Swords (+3/+2 each) attach to Grizzly Bears: 2/2 + 6/4 = 8/6") {
                val projected = stateProjector.project(game.state)
                projected.getPower(bears) shouldBe 8
                projected.getToughness(bears) shouldBe 6
            }
        }

        test("choosing zero Equipment is a legal no-op (the 'any number' lower bound)") {
            val game = scenario()
                .withPlayers()
                .withCardOnBattlefield(1, "Beatrix, Loyal General", summoningSickness = false)
                .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                .withCardOnBattlefield(1, "Buster Sword")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val bears = game.findPermanent("Grizzly Bears")!!

            game.passUntilPhase(Phase.COMBAT, Step.BEGIN_COMBAT)

            (game.getPendingDecision() is YesNoDecision) shouldBe true
            game.answerYesNo(true)

            val targetDecision = game.getPendingDecision()!!
            game.submitDecision(TargetsResponse(targetDecision.id, mapOf(0 to listOf(bears))))
            game.resolveStack()

            val selectDecision = game.getPendingDecision()
            (selectDecision is SelectCardsDecision) shouldBe true
            game.submitDecision(CardsSelectedResponse(selectDecision!!.id, emptyList()))
            game.resolveStack()

            withClue("no Equipment chosen — Grizzly Bears stays 2/2, the Buster Sword stays unattached") {
                val projected = stateProjector.project(game.state)
                projected.getPower(bears) shouldBe 2
                projected.getToughness(bears) shouldBe 2
            }
        }

        test("declining the 'you may' up front does nothing") {
            val game = scenario()
                .withPlayers()
                .withCardOnBattlefield(1, "Beatrix, Loyal General", summoningSickness = false)
                .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                .withCardOnBattlefield(1, "Buster Sword")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val bears = game.findPermanent("Grizzly Bears")!!

            game.passUntilPhase(Phase.COMBAT, Step.BEGIN_COMBAT)

            // Decline the "you may" — no targeting, no Equipment selection, no attach.
            (game.getPendingDecision() is YesNoDecision) shouldBe true
            game.answerYesNo(false)
            game.resolveStack()

            withClue("declined — no Equipment-selection decision, Grizzly Bears stays 2/2") {
                (game.getPendingDecision() is SelectCardsDecision) shouldBe false
                val projected = stateProjector.project(game.state)
                projected.getPower(bears) shouldBe 2
                projected.getToughness(bears) shouldBe 2
            }
        }
    }
}
