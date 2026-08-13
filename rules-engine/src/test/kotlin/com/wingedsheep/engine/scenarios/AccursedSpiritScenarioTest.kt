package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.DeclareBlockers
import com.wingedsheep.engine.handlers.EffectContext
import com.wingedsheep.engine.mechanics.layers.Layer
import com.wingedsheep.engine.mechanics.layers.SerializableModification
import com.wingedsheep.engine.mechanics.layers.addFloatingEffect
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.m14.cards.AccursedSpirit
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.Duration
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContainIgnoringCase

/**
 * Accursed Spirit (M14 #83) — Intimidate.
 *
 * Oracle text retrieved 2026-08-13: "This creature can't be blocked except by artifact creatures
 * and/or creatures that share a color with it." The rule is checked while blockers are declared;
 * the relevant type and color characteristics are projected at that point, not read from printing.
 */
class AccursedSpiritScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver = GameTestDriver().also { driver ->
        driver.registerCards(TestCards.all + AccursedSpirit)
        driver.initMirrorMatch(
            deck = Deck.of("Swamp" to 20, "Forest" to 20, "Grizzly Bears" to 20),
            skipMulligans = true,
        )
    }

    fun GameTestDriver.advanceToAttackerDeclareBlockers(attacker: EntityId) {
        passPriorityUntil(Step.DECLARE_ATTACKERS)
        var safety = 0
        while (activePlayer != player1 && safety++ < 50) {
            bothPass()
            passPriorityUntil(Step.DECLARE_ATTACKERS)
        }
        declareAttackers(player1, listOf(attacker), player2).isSuccess shouldBe true
        bothPass()
        currentStep shouldBe Step.DECLARE_BLOCKERS
    }

    fun GameTestDriver.addProjectedChange(
        target: EntityId,
        layer: Layer,
        modification: SerializableModification,
    ) {
        replaceState(
            state.addFloatingEffect(
                layer = layer,
                modification = modification,
                affectedEntities = setOf(target),
                duration = Duration.EndOfTurn,
                context = EffectContext(sourceId = null, controllerId = player2),
            )
        )
    }

    test("nonartifact creature with no shared color cannot block Accursed Spirit") {
        val driver = createDriver()
        val spirit = driver.putCreatureOnBattlefield(driver.player1, "Accursed Spirit")
        val blocker = driver.putCreatureOnBattlefield(driver.player2, "Grizzly Bears")
        driver.removeSummoningSickness(spirit)

        driver.advanceToAttackerDeclareBlockers(spirit)

        val result = driver.submitExpectFailure(
            DeclareBlockers(driver.player2, mapOf(blocker to listOf(spirit)))
        )
        result.isSuccess shouldBe false
        result.error shouldContainIgnoringCase "intimidate"
    }

    test("creature sharing a current color with Accursed Spirit can block") {
        val driver = createDriver()
        val spirit = driver.putCreatureOnBattlefield(driver.player1, "Accursed Spirit")
        val blocker = driver.putCreatureOnBattlefield(driver.player2, "Grizzly Bears")
        driver.removeSummoningSickness(spirit)

        // A Layer-5 change makes the printed-green blocker black before block declaration.
        driver.addProjectedChange(
            blocker,
            Layer.COLOR,
            SerializableModification.ChangeColor(setOf(Color.BLACK.name)),
        )
        driver.state.projectedState.hasColor(blocker, Color.BLACK) shouldBe true

        driver.advanceToAttackerDeclareBlockers(spirit)

        driver.declareBlockers(driver.player2, mapOf(blocker to listOf(spirit))).isSuccess shouldBe true
    }

    test("a color change on Accursed Spirit itself changes which creatures can block") {
        val driver = createDriver()
        val spirit = driver.putCreatureOnBattlefield(driver.player1, "Accursed Spirit")
        val blocker = driver.putCreatureOnBattlefield(driver.player2, "Grizzly Bears")
        driver.removeSummoningSickness(spirit)

        // The printed-black attacker becomes green, so the printed-green blocker now shares a color.
        driver.addProjectedChange(
            spirit,
            Layer.COLOR,
            SerializableModification.ChangeColor(setOf(Color.GREEN.name)),
        )
        driver.state.projectedState.hasColor(spirit, Color.GREEN) shouldBe true

        driver.advanceToAttackerDeclareBlockers(spirit)

        driver.declareBlockers(driver.player2, mapOf(blocker to listOf(spirit))).isSuccess shouldBe true
    }

    test("a colorless Accursed Spirit can be blocked only by the artifact exception") {
        val driver = createDriver()
        val spirit = driver.putCreatureOnBattlefield(driver.player1, "Accursed Spirit")
        val greenBlocker = driver.putCreatureOnBattlefield(driver.player2, "Grizzly Bears")
        driver.removeSummoningSickness(spirit)

        // A colorless creature shares no color with any creature, so the ordinary green blocker
        // is no longer a legal declaration. The artifact-creature positive is covered separately.
        driver.addProjectedChange(
            spirit,
            Layer.COLOR,
            SerializableModification.ChangeColor(emptySet()),
        )
        driver.state.projectedState.getColors(spirit) shouldBe emptySet()

        driver.advanceToAttackerDeclareBlockers(spirit)

        val result = driver.submitExpectFailure(
            DeclareBlockers(driver.player2, mapOf(greenBlocker to listOf(spirit)))
        )
        result.isSuccess shouldBe false
        result.error shouldContainIgnoringCase "intimidate"
    }

    test("creature made an artifact in projected state can block") {
        val driver = createDriver()
        val spirit = driver.putCreatureOnBattlefield(driver.player1, "Accursed Spirit")
        val blocker = driver.putCreatureOnBattlefield(driver.player2, "Grizzly Bears")
        driver.removeSummoningSickness(spirit)

        // A Layer-4 addition creates an artifact creature without changing the printed card.
        driver.addProjectedChange(
            blocker,
            Layer.TYPE,
            SerializableModification.AddType("ARTIFACT"),
        )
        driver.state.projectedState.hasType(blocker, "ARTIFACT") shouldBe true
        driver.state.projectedState.isCreature(blocker) shouldBe true

        driver.advanceToAttackerDeclareBlockers(spirit)

        driver.declareBlockers(driver.player2, mapOf(blocker to listOf(spirit))).isSuccess shouldBe true
    }
})
