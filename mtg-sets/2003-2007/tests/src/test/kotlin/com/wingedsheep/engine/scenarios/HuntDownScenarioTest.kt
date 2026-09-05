package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.SerializableModification
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lrw.cards.HuntDown
import com.wingedsheep.mtg.sets.definitions.scg.cards.AmbushCommander
import com.wingedsheep.mtg.sets.definitions.tsp.cards.MomentaryBlink
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class HuntDownScenarioTest : FunSpec({
    fun driver() = GameTestDriver().apply {
        registerCards(TestCards.all + listOf(HuntDown, AmbushCommander, MomentaryBlink))
        initMirrorMatch(Deck.of("Plains" to 40), startingPlayer = 0)
        passPriorityUntil(Step.PRECOMBAT_MAIN)
    }

    fun GameTestDriver.hunt(blocker: EntityId, attacker: EntityId, resolve: Boolean = true) {
        val spell = putCardInHand(player1, "Hunt Down")
        giveMana(player1, Color.GREEN, 1)
        castSpell(player1, spell, listOf(blocker, attacker)).error shouldBe null
        if (resolve) bothPass().error shouldBe null
    }

    fun GameTestDriver.attack(attacker: EntityId) {
        removeSummoningSickness(attacker)
        passPriorityUntil(Step.DECLARE_ATTACKERS)
        declareAttackers(player1, listOf(attacker), defendingPlayer = player2).error shouldBe null
        passPriorityUntil(Step.DECLARE_BLOCKERS)
    }

    test("requires the chosen blocker to block the chosen attacker") {
        val d = driver()
        val attacker = d.putCreatureOnBattlefield(d.player1, "Centaur Courser")
        val blocker = d.putCreatureOnBattlefield(d.player2, "Grizzly Bears")
        d.hunt(blocker, attacker)
        d.attack(attacker)
        d.declareBlockers(d.player2, emptyMap()).isSuccess shouldBe false
        d.declareBlockers(d.player2, mapOf(blocker to listOf(attacker))).error shouldBe null
    }

    test("an animated Forest is a legal blocker and receives the requirement") {
        val d = driver()
        val attacker = d.putCreatureOnBattlefield(d.player1, "Centaur Courser")
        d.putCreatureOnBattlefield(d.player2, "Ambush Commander")
        val blocker = d.putLandOnBattlefield(d.player2, "Forest")
        d.state.projectedState.isCreature(blocker) shouldBe true
        d.hunt(blocker, attacker)
        d.attack(attacker)
        d.declareBlockers(d.player2, emptyMap()).isSuccess shouldBe false
        d.declareBlockers(d.player2, mapOf(blocker to listOf(attacker))).error shouldBe null
    }

    test("does not require an impossible block against a flying attacker") {
        val d = driver()
        val attacker = d.putCreatureOnBattlefield(d.player1, "Serra Angel")
        val blocker = d.putCreatureOnBattlefield(d.player2, "Grizzly Bears")
        d.hunt(blocker, attacker)
        d.attack(attacker)
        d.declareBlockers(d.player2, emptyMap()).error shouldBe null
    }

    test("a creature cannot be required to block its controller's creature") {
        val d = driver()
        val attacker = d.putCreatureOnBattlefield(d.player1, "Centaur Courser")
        val blocker = d.putCreatureOnBattlefield(d.player1, "Grizzly Bears")
        d.hunt(blocker, attacker)
        d.attack(attacker)
        d.declareBlockers(d.player2, emptyMap()).error shouldBe null
    }

    for (removeBlocker in listOf(true, false)) {
        test("does not create a requirement when the ${if (removeBlocker) "blocker" else "attacker"} target dies before resolution") {
            val d = driver()
            val attacker = d.putCreatureOnBattlefield(d.player1, "Centaur Courser")
            val blocker = d.putCreatureOnBattlefield(d.player2, "Grizzly Bears")
            d.hunt(blocker, attacker, resolve = false)
            val bolt = d.putCardInHand(d.player1, "Lightning Bolt")
            d.giveMana(d.player1, Color.RED, 1)
            d.castSpell(d.player1, bolt, listOf(if (removeBlocker) blocker else attacker)).error shouldBe null
            d.bothPass().error shouldBe null
            d.bothPass().error shouldBe null
            d.stackSize shouldBe 0
            d.state.floatingEffects.any { it.effect.modification is SerializableModification.MustBlockSpecificAttacker } shouldBe false
        }
    }

    for (blinkBlocker in listOf(true, false)) {
        test("a returned ${if (blinkBlocker) "blocker" else "attacker"} is not bound by the old requirement") {
            val d = driver()
            val attacker = d.putCreatureOnBattlefield(d.player1, "Centaur Courser")
            val blocker = d.putCreatureOnBattlefield(d.player2, "Grizzly Bears")
            d.hunt(blocker, attacker)
            val owner = if (blinkBlocker) d.player2 else d.player1
            val blink = d.putCardInHand(owner, "Momentary Blink")
            d.giveMana(owner, Color.WHITE, 2)
            if (d.priorityPlayer != owner) d.passPriority(d.priorityPlayer!!).error shouldBe null
            d.castSpell(owner, blink, listOf(if (blinkBlocker) blocker else attacker)).error shouldBe null
            d.bothPass().error shouldBe null
            d.attack(attacker)
            d.declareBlockers(d.player2, emptyMap()).error shouldBe null
        }
    }

    test("the blocking requirement expires at end of turn") {
        val d = driver()
        val attacker = d.putCreatureOnBattlefield(d.player1, "Centaur Courser")
        val blocker = d.putCreatureOnBattlefield(d.player2, "Grizzly Bears")
        d.hunt(blocker, attacker)
        d.state.floatingEffects.any { it.effect.modification is SerializableModification.MustBlockSpecificAttacker } shouldBe true
        d.passPriorityUntil(Step.UPKEEP)
        d.state.floatingEffects.any { it.effect.modification is SerializableModification.MustBlockSpecificAttacker } shouldBe false
    }
})
