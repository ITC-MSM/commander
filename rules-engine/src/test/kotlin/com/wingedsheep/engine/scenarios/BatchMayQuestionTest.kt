package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.BatchYesNoDecision
import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.OrderTriggeredAbilitiesDecision
import com.wingedsheep.engine.core.TriggeredAbilitiesOrderedResponse
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.state.components.stack.TriggeredAbilityOnStackComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.effects.MayEffect
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Targeted-trigger placement regressions.
 *
 * Two otherwise identical triggers are still separate stack objects. Their targets are chosen as
 * each object is put on the stack; an optional effect's yes/no decision happens only when that
 * individual object resolves. A pre-stack shared [BatchYesNoDecision] would skip the required
 * target-placement boundary and is deliberately rejected below.
 */
class BatchMayQuestionTest : FunSpec({

    fun GameTestDriver.acceptDefaultTriggerOrderIfNeeded() {
        val order = pendingDecision as? OrderTriggeredAbilitiesDecision ?: return
        submitDecision(
            order.playerId,
            TriggeredAbilitiesOrderedResponse(order.id, order.abilities.map { it.id })
        )
    }

    val optionalTargetPinger = card("Optional Target Pinger") {
        manaCost = "{1}"
        typeLine = "Creature — Test"
        power = 1
        toughness = 1
        oracleText = "Whenever another creature you control enters the battlefield, you may have " +
            "Optional Target Pinger deal 1 damage to any target."
        triggeredAbility {
            trigger = Triggers.OtherCreatureEnters
            val target = target("target", Targets.Any)
            effect = MayEffect(Effects.DealDamage(1, target))
        }
    }

    val mandatoryTargetPinger = card("Mandatory Target Pinger") {
        manaCost = "{1}"
        typeLine = "Creature — Test"
        power = 1
        toughness = 1
        oracleText = "Whenever another creature you control enters the battlefield, " +
            "Mandatory Target Pinger deals 1 damage to any target."
        triggeredAbility {
            trigger = Triggers.OtherCreatureEnters
            val target = target("target", Targets.Any)
            effect = Effects.DealDamage(1, target)
        }
    }

    val batchBear = card("Batch Bear") {
        manaCost = "{1}"
        typeLine = "Creature — Test"
        power = 2
        toughness = 2
    }

    fun driverWithPingers(cardName: String, count: Int): Triple<GameTestDriver, EntityId, EntityId> {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(optionalTargetPinger, mandatoryTargetPinger, batchBear))
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40))
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val player = driver.activePlayer!!
        val opponent = driver.getOpponent(player)
        repeat(count) { driver.putCreatureOnBattlefield(player, cardName) }

        driver.giveColorlessMana(player, 1)
        val bear = driver.putCardInHand(player, "Batch Bear")
        driver.castSpell(player, bear).isSuccess shouldBe true
        driver.bothPass() // resolve the bear; the pingers trigger
        driver.acceptDefaultTriggerOrderIfNeeded()
        return Triple(driver, player, opponent)
    }

    test("optional targeted triggers choose targets before each resolution-time may decision") {
        val (driver, player, opponent) = driverWithPingers("Optional Target Pinger", 2)

        // Corrupted pre-stack behaviour would present one shared BatchYesNoDecision here. Each
        // mandatory target declaration instead happens independently while the trigger is placed.
        (driver.pendingDecision is BatchYesNoDecision) shouldBe false
        val firstTarget = driver.pendingDecision.shouldBeInstanceOf<ChooseTargetsDecision>()
        driver.submitTargetSelection(firstTarget.playerId, listOf(opponent))
        val secondTarget = driver.pendingDecision.shouldBeInstanceOf<ChooseTargetsDecision>()
        driver.submitTargetSelection(secondTarget.playerId, listOf(opponent))

        val onStack = driver.state.stack.mapNotNull {
            driver.state.getEntity(it)?.get<TriggeredAbilityOnStackComponent>()
        }.filter { it.sourceName == "Optional Target Pinger" }
        onStack.size shouldBe 2

        // The top trigger resolves first and only then asks its own optional question.
        driver.bothPass()
        driver.pendingDecision.shouldBeInstanceOf<YesNoDecision>()
        driver.submitYesNo(player, true)
        driver.bothPass()
        driver.pendingDecision.shouldBeInstanceOf<YesNoDecision>()
        driver.submitYesNo(player, false)

        driver.assertLifeTotal(opponent, 19)
    }

    test("corrupted negative: simultaneous identical mandatory targeted triggers never batch") {
        val (driver, _, opponent) = driverWithPingers("Mandatory Target Pinger", 2)

        // A BatchYesNoDecision is invalid for this mandatory targeted interaction. It would make
        // a targetless/nonexistent stack object; both target declarations must instead be present.
        (driver.pendingDecision is BatchYesNoDecision) shouldBe false
        val firstTarget = driver.pendingDecision.shouldBeInstanceOf<ChooseTargetsDecision>()
        driver.submitTargetSelection(firstTarget.playerId, listOf(opponent))
        val secondTarget = driver.pendingDecision.shouldBeInstanceOf<ChooseTargetsDecision>()
        driver.submitTargetSelection(secondTarget.playerId, listOf(opponent))

        val onStack = driver.state.stack.mapNotNull {
            driver.state.getEntity(it)?.get<TriggeredAbilityOnStackComponent>()
        }.filter { it.sourceName == "Mandatory Target Pinger" }
        onStack.size shouldBe 2

        driver.bothPass()
        driver.bothPass()
        driver.assertLifeTotal(opponent, 18)
    }
})
