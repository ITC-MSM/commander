package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CombatResolutionDecision
import com.wingedsheep.engine.core.OrderObjectsDecision
import com.wingedsheep.engine.core.OrderedResponse
import com.wingedsheep.engine.core.PassPriority
import com.wingedsheep.engine.core.PendingDecision
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.FaceDownComponent
import com.wingedsheep.engine.state.components.identity.ManifestedComponent
import com.wingedsheep.engine.state.components.identity.MorphDataComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.costs.PayCost
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Regression test for a hidden-information leak: a morphed or manifested (face-down) creature must
 * not reveal the identity of the card it really is through the combat-damage-assignment board
 * ([CombatResolutionDecision]). The engine used to copy the raw [CardComponent] name straight into
 * the [com.wingedsheep.engine.core.ResolutionAttacker] / [com.wingedsheep.engine.core.ResolutionBlocker]
 * nodes (and into the decision prompt / source name), so the assigning player could read e.g.
 * "Unstoppable Slasher" off the assign-combat-damage GUI even though the card was face down.
 *
 * The whole decision graph is shared across every chooser (the attacker assigns its damage, the
 * defender assigns any blocker damage — opponents), so face-down names are masked to the generic
 * "Face-down creature" label unconditionally, mirroring the client state transformer.
 */
class CombatDamageFaceDownNameLeakTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        return driver
    }

    /** Turn an on-battlefield creature into a face-down morph (keeps its hidden real name). */
    fun GameTestDriver.morphFaceDown(entityId: EntityId) {
        replaceState(state.updateEntity(entityId) { container ->
            val defId = container.get<CardComponent>()?.cardDefinitionId ?: ""
            container.with(FaceDownComponent).with(MorphDataComponent(PayCost.OwnManaCost, defId))
        })
    }

    /** Turn an on-battlefield creature into a face-down manifested permanent. */
    fun GameTestDriver.manifestFaceDown(entityId: EntityId) {
        replaceState(state.updateEntity(entityId) { container ->
            container.with(FaceDownComponent).with(ManifestedComponent)
        })
    }

    /** Advance steps until a pending decision shows up (without auto-resolving). */
    fun advanceUntilDecision(driver: GameTestDriver, maxPasses: Int = 50) {
        var passes = 0
        while (driver.state.pendingDecision == null && passes < maxPasses) {
            val priority = driver.state.priorityPlayerId ?: error("No priority and no pending decision")
            driver.submit(PassPriority(priority))
            passes++
            if (driver.state.gameOver) error("Game ended before a decision was emitted")
        }
        if (passes >= maxPasses) {
            error("No pending decision emitted within $maxPasses passes; current step=${driver.currentStep}")
        }
    }

    test("morphed attacker and manifested blocker do not leak their real names in the combat-damage board") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40))
        val attacker = driver.activePlayer!!
        val defender = if (attacker == driver.player1) driver.player2 else driver.player1

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Face-down morph attacker — really "Centaur Courser", shows as a 2/2 face-down creature.
        val morphAttacker = driver.putCreatureOnBattlefield(attacker, "Centaur Courser")
        driver.morphFaceDown(morphAttacker)
        driver.removeSummoningSickness(morphAttacker)

        // Two blockers so the 2/2 attacker must assign combat damage (CR 510.1c) → board decision.
        // One is a face-down manifested creature (really "Savannah Lions"), one is a plain creature.
        val manifestBlocker = driver.putCreatureOnBattlefield(defender, "Savannah Lions")
        driver.manifestFaceDown(manifestBlocker)
        val plainBlocker = driver.putCreatureOnBattlefield(defender, "Trample Beast")

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(attacker, listOf(morphAttacker), defender)
        driver.passPriorityUntil(Step.DECLARE_BLOCKERS)
        driver.declareBlockers(
            defender,
            mapOf(manifestBlocker to listOf(morphAttacker), plainBlocker to listOf(morphAttacker)),
        )

        // Declaring two blockers on one attacker pauses on an OrderObjectsDecision first.
        var decision: PendingDecision? = driver.state.pendingDecision
        if (decision is OrderObjectsDecision) {
            driver.submitDecision(
                decision.playerId,
                OrderedResponse(decision.id, listOf(manifestBlocker, plainBlocker)),
            )
        }
        advanceUntilDecision(driver)
        decision = driver.state.pendingDecision

        decision.shouldBeInstanceOf<CombatResolutionDecision>()

        // The morph attacker's node is masked, not its real card name.
        val attackerNode = decision.attackers.single { it.id == morphAttacker }
        attackerNode.name shouldBe "Face-down creature"

        // The manifested blocker is masked; the plain blocker keeps its real name.
        decision.blockers.single { it.id == manifestBlocker }.name shouldBe "Face-down creature"
        decision.blockers.single { it.id == plainBlocker }.name shouldBe "Trample Beast"

        // The hidden real names appear nowhere in what the assigning player receives — not in the
        // node names, the prompt, or the decision source name.
        val displayedNames = decision.attackers.map { it.name } + decision.blockers.map { it.name }
        (displayedNames.contains("Centaur Courser")) shouldBe false
        (displayedNames.contains("Savannah Lions")) shouldBe false
        decision.prompt.contains("Centaur Courser") shouldBe false
        decision.context.sourceName shouldBe "Face-down creature"
    }
})
