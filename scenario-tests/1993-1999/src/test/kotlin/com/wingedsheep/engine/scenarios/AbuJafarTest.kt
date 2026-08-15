package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.ActiveFloatingEffect
import com.wingedsheep.engine.mechanics.layers.FloatingEffectData
import com.wingedsheep.engine.mechanics.layers.Layer
import com.wingedsheep.engine.mechanics.layers.SerializableModification
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.Duration
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Tests for Abu Ja'far (Arabian Nights, {W}, 0/1 Human).
 *
 * Oracle: "When this creature dies, destroy all creatures blocking or blocked by it.
 * They can't be regenerated."
 *
 * The dies trigger uses [com.wingedsheep.sdk.scripting.effects.CardSource.LastKnownCombatPairedWithSource]:
 * the live combat cross-references are torn down by the time a dies trigger resolves, so the
 * pairing (CR 509) is captured as last-known information on Abu Ja'far's leaves-battlefield
 * event ([com.wingedsheep.engine.core.ZoneChangeEvent.lastKnownBlockingOrBlockedByIds]) and the
 * gather restricts it to creatures still on the battlefield. `noRegenerate = true`, so a
 * shielded blocker is destroyed anyway.
 */
class AbuJafarTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        return driver
    }

    test("Abu Ja'far blocking and dying destroys the attacker it blocked, but not bystanders") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)

        val defender = driver.activePlayer!!
        val attacker = driver.getOpponent(defender)

        val abu = driver.putCreatureOnBattlefield(defender, "Abu Ja'far")
        driver.removeSummoningSickness(abu)
        // A second creature the defender controls that takes no part in combat.
        val bystander = driver.putCreatureOnBattlefield(defender, "Hill Giant")

        val attackingBears = driver.putCreatureOnBattlefield(attacker, "Grizzly Bears")
        driver.removeSummoningSickness(attackingBears)

        // Advance to the attacker's declare-attackers step. The attacker is the non-active
        // player, so step past the active player's own combat (to their postcombat main)
        // before seeking the next turn's declare-attackers.
        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        if (driver.activePlayer != attacker) {
            driver.passPriorityUntil(Step.POSTCOMBAT_MAIN)
            driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        }

        driver.declareAttackers(attacker, listOf(attackingBears), defender)
        driver.passPriorityUntil(Step.DECLARE_BLOCKERS)

        // Abu Ja'far (0/1) blocks the 2/2.
        driver.declareBlockers(defender, mapOf(abu to listOf(attackingBears)))

        // Combat damage kills Abu Ja'far; its dies trigger resolves and destroys the
        // creature it was blocked by (the attacking Grizzly Bears).
        driver.passPriorityUntil(Step.POSTCOMBAT_MAIN)

        driver.findPermanent(defender, "Abu Ja'far") shouldBe null
        driver.findPermanent(attacker, "Grizzly Bears") shouldBe null
        // The bystander was never in combat with Abu Ja'far — it must survive.
        driver.findPermanent(defender, "Hill Giant") shouldNotBe null
        bystander shouldNotBe null
    }

    test("Abu Ja'far attacking and dying destroys the creature blocking it") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)

        val attacker = driver.activePlayer!!
        val defender = driver.getOpponent(attacker)

        val abu = driver.putCreatureOnBattlefield(attacker, "Abu Ja'far")
        driver.removeSummoningSickness(abu)

        val blockingBears = driver.putCreatureOnBattlefield(defender, "Grizzly Bears")
        driver.removeSummoningSickness(blockingBears)

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        if (driver.activePlayer != attacker) {
            driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        }

        driver.declareAttackers(attacker, listOf(abu), defender)
        driver.passPriorityUntil(Step.DECLARE_BLOCKERS)

        // The 2/2 blocks Abu Ja'far (0/1).
        driver.declareBlockers(defender, mapOf(blockingBears to listOf(abu)))

        // Abu Ja'far dies to combat damage; its trigger destroys the blocker.
        driver.passPriorityUntil(Step.POSTCOMBAT_MAIN)

        driver.findPermanent(attacker, "Abu Ja'far") shouldBe null
        driver.findPermanent(defender, "Grizzly Bears") shouldBe null
    }

    test("Abu Ja'far's trigger can't be regenerated — a shielded blocker is destroyed anyway") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)

        val attacker = driver.activePlayer!!
        val defender = driver.getOpponent(attacker)

        val abu = driver.putCreatureOnBattlefield(attacker, "Abu Ja'far")
        driver.removeSummoningSickness(abu)

        // The 2/2 blocks Abu Ja'far (0/1) — it takes no combat damage back, so its
        // regeneration shield is still up when the dies trigger resolves.
        val blockingBears = driver.putCreatureOnBattlefield(defender, "Grizzly Bears")
        driver.removeSummoningSickness(blockingBears)
        driver.replaceState(
            driver.state.copy(
                floatingEffects = driver.state.floatingEffects + regenerationShield(blockingBears, defender)
            )
        )

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        if (driver.activePlayer != attacker) {
            driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        }

        driver.declareAttackers(attacker, listOf(abu), defender)
        driver.passPriorityUntil(Step.DECLARE_BLOCKERS)
        driver.declareBlockers(defender, mapOf(blockingBears to listOf(abu)))

        // Abu Ja'far dies; its "can't be regenerated" trigger destroys the blocker even
        // though a regeneration shield is up.
        driver.passPriorityUntil(Step.POSTCOMBAT_MAIN)

        driver.findPermanent(attacker, "Abu Ja'far") shouldBe null
        driver.findPermanent(defender, "Grizzly Bears") shouldBe null
    }
})

private fun regenerationShield(entityId: EntityId, controllerId: EntityId) = ActiveFloatingEffect(
    id = EntityId.generate(),
    effect = FloatingEffectData(
        layer = Layer.ABILITY,
        modification = SerializableModification.RegenerationShield,
        affectedEntities = setOf(entityId)
    ),
    duration = Duration.EndOfTurn,
    sourceId = null,
    controllerId = controllerId,
    timestamp = 1L
)
