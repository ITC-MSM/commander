package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lci.cards.DeepfathomEcho
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Deepfathom Echo (LCI #228) — {2}{G}{U} 4/4 Creature — Merfolk Spirit
 *
 * "At the beginning of combat on your turn, this creature explores. Then you may have it
 *  become a copy of another creature you control until end of turn."
 *
 * Tests:
 *  1. Explore with a land on top → land goes to hand; player accepts the copy step →
 *     Deepfathom Echo becomes a copy of the chosen other creature until end of turn
 *     (copiable values only — power/toughness change, counters/attachments are unaffected).
 *  2. Player declines the copy step → Deepfathom Echo stays a 4/4 Merfolk Spirit;
 *     the land still went to hand from explore.
 */
class DeepfathomEchoScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(DeepfathomEcho))
        return driver
    }

    /**
     * Advance to the begin-of-combat step on player 1's turn (mirrors the helper from
     * JenovaAncientCalamityScenarioTest).
     */
    fun GameTestDriver.advanceToPlayer1BeginCombat() {
        passPriorityUntil(Step.BEGIN_COMBAT)
        var safety = 0
        while (activePlayer != player1 && safety < 50) {
            bothPass()
            passPriorityUntil(Step.BEGIN_COMBAT)
            safety++
        }
    }

    test("explore (land → hand), accept copy: Echo becomes a copy of the chosen other creature until end of turn") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Forest" to 20, "Island" to 20),
            startingLife = 20,
            skipMulligans = true,
            startingPlayer = 0
        )
        val player: EntityId = driver.player1

        // Put Deepfathom Echo (4/4) and Grizzly Bears (2/2) on the battlefield.
        val echo = driver.putCreatureOnBattlefield(player, "Deepfathom Echo")
        driver.putCreatureOnBattlefield(player, "Grizzly Bears")

        // Seed a land on top so explore takes the land-to-hand branch without pausing.
        driver.putCardOnTopOfLibrary(player, "Forest")

        val handSizeBefore = driver.getHandSize(player)

        // Advance to player 1's begin-of-combat step. The trigger fires and goes on the stack;
        // no decision is pending yet (no target is declared at stack-placement time — target
        // selection is embedded inside the MayEffect and runs only at resolution if yes).
        driver.advanceToPlayer1BeginCombat()

        // Resolve the trigger: Explore runs (Forest → hand, no pause) → MayEffect → YesNoDecision.
        driver.bothPass()

        // The "you may have it become a copy" prompt should be pending.
        (driver.pendingDecision is YesNoDecision) shouldBe true

        // Accept the copy.
        driver.submitYesNo(player, true)

        // Grizzly Bears is the only other creature the controller controls (Deepfathom Echo is
        // excluded by OtherCreatureYouControl's excludeSelf filter). SelectTargetEffect
        // auto-selects it — no explicit ChooseTargetsDecision is expected.

        // Advance state so the copy effect completes and priority is returned.
        driver.bothPass()

        // Forest went to hand during explore.
        driver.getHandSize(player) shouldBe handSizeBefore + 1
        driver.findCardInHand(player, "Forest").shouldNotBeNull()

        // Deepfathom Echo is now a copy of Grizzly Bears: projected power/toughness = 2/2.
        val projected = driver.state.projectedState
        projected.getPower(echo) shouldBe 2
        projected.getToughness(echo) shouldBe 2
    }

    test("explore (land → hand), decline copy: Echo remains a 4/4 Merfolk Spirit") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Forest" to 20, "Island" to 20),
            startingLife = 20,
            skipMulligans = true,
            startingPlayer = 0
        )
        val player: EntityId = driver.player1

        val echo = driver.putCreatureOnBattlefield(player, "Deepfathom Echo")
        // A second creature on the battlefield so the SelectTarget has a valid option,
        // but the player will decline before any target is ever chosen.
        driver.putCreatureOnBattlefield(player, "Grizzly Bears")
        driver.putCardOnTopOfLibrary(player, "Forest")

        val handSizeBefore = driver.getHandSize(player)

        driver.advanceToPlayer1BeginCombat()

        // Resolve the trigger: explore (Forest → hand) → MayEffect → YesNoDecision.
        driver.bothPass()

        (driver.pendingDecision is YesNoDecision) shouldBe true

        // Decline the copy.
        driver.submitYesNo(player, false)
        driver.bothPass()

        // Forest still went to hand during explore (explore is unconditional).
        driver.getHandSize(player) shouldBe handSizeBefore + 1
        driver.findCardInHand(player, "Forest").shouldNotBeNull()

        // Deepfathom Echo was not copied — it is still a 4/4.
        val projected = driver.state.projectedState
        projected.getPower(echo) shouldBe 4
        projected.getToughness(echo) shouldBe 4
    }
})
