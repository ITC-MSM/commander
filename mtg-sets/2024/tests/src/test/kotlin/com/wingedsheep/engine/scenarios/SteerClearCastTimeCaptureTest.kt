package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.otj.cards.GiantBeaver
import com.wingedsheep.mtg.sets.definitions.otj.cards.SteerClear
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Steer Clear: {W} Instant
 * Steer Clear deals 2 damage to target attacking or blocking creature. Steer Clear deals 4 damage
 * to that creature instead if you controlled a Mount as you cast this spell.
 *
 * Exercises the cast-time condition capture (CR 601.2i): whether the caster controlled a Mount is
 * frozen the moment the spell is cast, so a later board change can't flip the 4-vs-2 branch. Per the
 * OTJ ruling, losing the Mount before resolution still deals 4; gaining one after the cast still
 * deals 2. The Mount is removed/added mid-stack via the trigger-free `moveToGraveyard` /
 * `putCreatureOnBattlefield` test helpers.
 */
class SteerClearCastTimeCaptureTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(SteerClear, GiantBeaver))
        return driver
    }

    /** Attacker declares an attack; priority is handed to [caster] in the declare-attackers step. */
    fun GameTestDriver.attackThenGiveCasterPriority(
        attacker: EntityId,
        caster: EntityId,
        attackingCreature: EntityId,
    ) {
        passPriorityUntil(Step.DECLARE_ATTACKERS)
        declareAttackers(attacker, listOf(attackingCreature), caster)
        // Active player holds priority first in the declare-attackers step; pass it to the caster.
        passPriority(attacker)
        state.priorityPlayerId shouldBe caster
    }

    fun GameTestDriver.onBattlefield(playerId: EntityId, entityId: EntityId): Boolean =
        state.getZone(ZoneKey(playerId, Zone.BATTLEFIELD)).contains(entityId)

    test("controlled a Mount as cast: still deals 4 even though the Mount left before resolution") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)
        val attacker = driver.activePlayer!!
        val caster = driver.getOpponent(attacker)

        // The attacker's 3/3 — survives 2 damage, dies to 4.
        val courser = driver.putCreatureOnBattlefield(attacker, "Centaur Courser")
        driver.removeSummoningSickness(courser)
        // The caster controls a Mount and holds Steer Clear.
        val mount = driver.putCreatureOnBattlefield(caster, "Giant Beaver")
        val steer = driver.putCardInHand(caster, "Steer Clear")

        driver.attackThenGiveCasterPriority(attacker, caster, courser)
        // mana added here — unspent mana empties as each step/phase ends (CR 500.5)
        driver.giveMana(caster, Color.WHITE, 1)

        val cast = driver.submit(
            CastSpell(
                playerId = caster,
                cardId = steer,
                targets = listOf(ChosenTarget.Permanent(courser)),
                paymentStrategy = PaymentStrategy.FromPool,
            )
        )
        (cast.error == null) shouldBe true

        // The Mount leaves while Steer Clear is on the stack — the cast-time answer must hold.
        driver.moveToGraveyard(mount)
        driver.bothPass()

        // 4 damage killed the 3/3.
        driver.onBattlefield(attacker, courser) shouldBe false
        driver.assertInGraveyard(attacker, "Centaur Courser")
    }

    test("no Mount as cast: deals only 2 — the 3/3 survives") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)
        val attacker = driver.activePlayer!!
        val caster = driver.getOpponent(attacker)

        val courser = driver.putCreatureOnBattlefield(attacker, "Centaur Courser")
        driver.removeSummoningSickness(courser)
        // No Mount on the caster's side.
        val steer = driver.putCardInHand(caster, "Steer Clear")

        driver.attackThenGiveCasterPriority(attacker, caster, courser)
        // mana added here — unspent mana empties as each step/phase ends (CR 500.5)
        driver.giveMana(caster, Color.WHITE, 1)

        val cast = driver.submit(
            CastSpell(
                playerId = caster,
                cardId = steer,
                targets = listOf(ChosenTarget.Permanent(courser)),
                paymentStrategy = PaymentStrategy.FromPool,
            )
        )
        (cast.error == null) shouldBe true
        driver.bothPass()

        // Only 2 damage — the 3/3 is still on the battlefield (combat damage hasn't happened yet).
        driver.onBattlefield(attacker, courser) shouldBe true
        driver.getCardName(courser) shouldBe "Centaur Courser"
    }

    test("no Mount as cast: gaining a Mount before resolution still deals only 2") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)
        val attacker = driver.activePlayer!!
        val caster = driver.getOpponent(attacker)

        val courser = driver.putCreatureOnBattlefield(attacker, "Centaur Courser")
        driver.removeSummoningSickness(courser)
        val steer = driver.putCardInHand(caster, "Steer Clear")

        driver.attackThenGiveCasterPriority(attacker, caster, courser)
        // mana added here — unspent mana empties as each step/phase ends (CR 500.5)
        driver.giveMana(caster, Color.WHITE, 1)

        val cast = driver.submit(
            CastSpell(
                playerId = caster,
                cardId = steer,
                targets = listOf(ChosenTarget.Permanent(courser)),
                paymentStrategy = PaymentStrategy.FromPool,
            )
        )
        (cast.error == null) shouldBe true

        // A Mount enters after the cast — the frozen "no Mount" answer must hold.
        driver.putCreatureOnBattlefield(caster, "Giant Beaver")
        driver.bothPass()

        driver.onBattlefield(attacker, courser) shouldBe true
        driver.getCardName(courser) shouldBe "Centaur Courser"
    }
})
