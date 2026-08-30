package com.wingedsheep.engine.handlers.continuations

import com.wingedsheep.engine.core.ChooseOptionDecision
import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.EngineServices
import com.wingedsheep.engine.core.ExecutionResult
import com.wingedsheep.engine.core.OptionChosenResponse
import com.wingedsheep.engine.core.TargetsResponse
import com.wingedsheep.engine.handlers.actions.decision.DecisionValidators
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.scripting.effects.ModalEffect
import com.wingedsheep.sdk.scripting.effects.Mode
import com.wingedsheep.sdk.scripting.targets.TargetOpponent
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class ModalTargetForcednessTest : FunSpec({

    fun mode(optional: Boolean) = Mode.withTarget(
        effect = Effects.GainLife(1),
        target = TargetOpponent(optional = optional),
        description = if (optional) "Gain life with up to one target opponent" else "Gain life with target opponent",
    )

    fun processMode(optional: Boolean): Pair<GameTestDriver, ExecutionResult> {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(Deck.of("Forest" to 40))
        val controller = driver.player1

        val result = processChosenModeQueue(
            services = EngineServices(driver.cardRegistry),
            state = driver.state,
            queue = listOf(mode(optional)),
            controllerId = controller,
            sourceId = null,
            sourceName = "Test modal effect",
            xValue = null,
            triggeringEntityId = null,
            allowCancelBackToModesList = null,
            outerTargets = emptyList(),
            outerNamedTargets = emptyMap(),
            accumulatedEvents = emptyList(),
            checkForMore = { state, events -> ExecutionResult.success(state, events) },
        )
        return driver to result
    }

    test("resolution-time modal target is surfaced when the lone opponent may be declined") {
        val (driver, result) = processMode(optional = true)

        result.isPaused.shouldBeTrue()
        val decision = result.pendingDecision.shouldBeInstanceOf<ChooseTargetsDecision>()
        decision.targetRequirements.single().minTargets shouldBe 0
        decision.legalTargets.values.single() shouldBe listOf(driver.player2)
        DecisionValidators.validate(
            decision,
            TargetsResponse(decision.id, emptyMap()),
            result.state,
        ).shouldBeNull()
    }

    test("resolution-time modal target still auto-selects the lone mandatory opponent") {
        val (driver, result) = processMode(optional = false)

        result.isPaused.shouldBeFalse()
        result.isSuccess.shouldBeTrue()
        result.state.lifeTotal(driver.player1) shouldBe driver.state.lifeTotal(driver.player1) + 1
    }

    fun triggeredModalCard(optional: Boolean) = card(
        if (optional) "Optional Player Modal Trigger" else "Mandatory Player Modal Trigger"
    ) {
        manaCost = "{0}"
        typeLine = "Creature — Test"
        power = 1
        toughness = 1
        triggeredAbility {
            trigger = Triggers.EntersBattlefield
            effect = ModalEffect.chooseOne(
                mode(optional),
                Mode.noTarget(Effects.GainLife(2), "Gain 2 life"),
            )
        }
    }

    fun chooseFirstTriggeredMode(optional: Boolean): GameTestDriver {
        val testCard = triggeredModalCard(optional)
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + testCard)
        driver.initMirrorMatch(Deck.of("Forest" to 40))
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val controller = driver.activePlayer!!
        val cardId = driver.putCardInHand(controller, testCard.name)
        driver.castSpell(controller, cardId).isSuccess.shouldBeTrue()

        var guard = 0
        while (driver.state.stack.isNotEmpty() && driver.pendingDecision == null && guard++ < 20) {
            driver.bothPass()
        }

        val modeDecision = driver.pendingDecision.shouldBeInstanceOf<ChooseOptionDecision>()
        driver.submitDecision(controller, OptionChosenResponse(modeDecision.id, optionIndex = 0))
        return driver
    }

    test("stack-time modal target is surfaced when the lone opponent may be declined") {
        val driver = chooseFirstTriggeredMode(optional = true)

        val decision = driver.pendingDecision.shouldBeInstanceOf<ChooseTargetsDecision>()
        decision.targetRequirements.single().minTargets shouldBe 0
        DecisionValidators.validate(
            decision,
            TargetsResponse(decision.id, emptyMap()),
            driver.state,
        ).shouldBeNull()
    }

    test("stack-time modal target still auto-selects the lone mandatory opponent") {
        val driver = chooseFirstTriggeredMode(optional = false)

        driver.pendingDecision.shouldBeNull()
        driver.state.stack.isNotEmpty().shouldBeTrue()
    }
})
