package com.wingedsheep.engine.replacement

import com.wingedsheep.engine.core.EngineServices
import com.wingedsheep.engine.core.ZoneChangeEvent
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.core.YesNoResponse
import com.wingedsheep.engine.core.ChooseOptionDecision
import com.wingedsheep.engine.core.OptionChosenResponse
import com.wingedsheep.engine.handlers.effects.ZoneEntryOptions
import com.wingedsheep.engine.handlers.effects.ZoneTransitionService
import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.engine.state.ComponentContainer
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.CommanderComponent
import com.wingedsheep.engine.state.components.identity.OwnerComponent
import com.wingedsheep.engine.state.components.identity.ControllerComponent
import com.wingedsheep.engine.state.components.identity.LifeTotalComponent
import com.wingedsheep.engine.state.components.identity.PlayerComponent
import com.wingedsheep.engine.state.components.battlefield.ReplacementEffectSourceComponent
import com.wingedsheep.sdk.core.CardType
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Format
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Supertype
import com.wingedsheep.sdk.core.TypeLine
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.CreatureStats
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.RedirectZoneChange
import com.wingedsheep.sdk.scripting.RedirectZoneChangeWithEffect
import com.wingedsheep.sdk.scripting.effects.GainLifeEffect
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/** Pins the pre-move Commander replacement, not the post-move 903.9a SBA. */
class CommanderZoneReplacementTest : FunSpec({
    val services = EngineServices(CardRegistry())
    val owner = EntityId.generate()
    val commander = EntityId.generate()

    fun state(): GameState = GameState(format = Format.Commander())
        .withEntity(owner, ComponentContainer.EMPTY)
        .withEntity(commander, ComponentContainer.of(
            CardComponent(
                cardDefinitionId = "TestCommander", name = "Test Commander",
                manaCost = ManaCost.ZERO,
                typeLine = TypeLine(setOf(Supertype.LEGENDARY), setOf(CardType.CREATURE)),
                oracleText = "", baseStats = CreatureStats(2, 2), colors = setOf(Color.BLUE), ownerId = owner
            ),
            OwnerComponent(owner), CommanderComponent(owner)
        ))
        .addToZone(ZoneKey(owner, Zone.BATTLEFIELD), commander)
        .copy(turnOrder = listOf(owner))

    fun resume(state: GameState, choice: Boolean): GameState {
        val decision = state.pendingDecision.shouldBeInstanceOf<YesNoDecision>()
        return services.continuationHandler
            .resume(state.clearPendingDecision(), YesNoResponse(decision.id, choice)).state
    }

    /** A mandatory redirect that becomes applicable only after 903.9b changes the destination. */
    fun stateWithCommandToLibraryRedirect(): GameState {
        val redirector = EntityId.generate()
        return state()
            .withEntity(redirector, ComponentContainer.of(
                CardComponent("Command Redirector", "Command Redirector", ManaCost.ZERO,
                    TypeLine(setOf(), setOf(CardType.ENCHANTMENT)), "", ownerId = owner),
                OwnerComponent(owner), ControllerComponent(owner),
                ReplacementEffectSourceComponent(listOf(
                    RedirectZoneChange(Zone.LIBRARY, EventPattern.ZoneChangeEvent(to = Zone.COMMAND))
                ))
            ))
            .addToZone(ZoneKey(owner, Zone.BATTLEFIELD), redirector)
    }

    test("bounce commander pauses before movement and accepting performs exactly one command-zone move") {
        val result = ZoneTransitionService.attemptMoveToZone(state(), commander, Zone.HAND, ZoneEntryOptions())
        result.isPaused shouldBe true
        result.state.getZone(ZoneKey(owner, Zone.BATTLEFIELD)).shouldContainExactly(commander)

        val resolved = resume(result.state, true)
        resolved.getZone(ZoneKey(owner, Zone.COMMAND)).shouldContainExactly(commander)
        resolved.getZone(ZoneKey(owner, Zone.HAND)).shouldContainExactly()
    }

    test("bounce commander decline performs exactly one move to hand with no hand SBA") {
        val result = ZoneTransitionService.attemptMoveToZone(state(), commander, Zone.HAND)
        val resolved = resume(result.state, false)
        resolved.getZone(ZoneKey(owner, Zone.HAND)).shouldContainExactly(commander)
        resolved.getZone(ZoneKey(owner, Zone.COMMAND)).shouldContainExactly()
        resolved.pendingDecision shouldBe null
    }

    test("tuck commander accept and decline use the same pre-move replacement") {
        val accept = resume(ZoneTransitionService.attemptMoveToZone(state(), commander, Zone.LIBRARY).state, true)
        accept.getZone(ZoneKey(owner, Zone.COMMAND)).shouldContainExactly(commander)

        val decline = resume(ZoneTransitionService.attemptMoveToZone(state(), commander, Zone.LIBRARY).state, false)
        decline.getZone(ZoneKey(owner, Zone.LIBRARY)).shouldContainExactly(commander)
    }

    test("903.9b may apply twice to one event after an intervening redirect when accepted twice") {
        val attempted = ZoneTransitionService.attemptMoveToZone(stateWithCommandToLibraryRedirect(), commander, Zone.HAND)
        val firstPrompt = attempted.state.pendingDecision.shouldBeInstanceOf<YesNoDecision>()
        val afterFirstAccept = services.continuationHandler.resume(
            attempted.state.clearPendingDecision(), YesNoResponse(firstPrompt.id, true)
        )
        afterFirstAccept.isPaused shouldBe true
        val secondPrompt = afterFirstAccept.state.pendingDecision.shouldBeInstanceOf<YesNoDecision>()

        val completed = services.continuationHandler.resume(
            afterFirstAccept.state.clearPendingDecision(), YesNoResponse(secondPrompt.id, true)
        )
        completed.state.getZone(ZoneKey(owner, Zone.COMMAND)).shouldContainExactly(commander)
        completed.state.getZone(ZoneKey(owner, Zone.LIBRARY)).shouldContainExactly()
        completed.state.pendingDecision shouldBe null
        completed.state.activeReplacementChain shouldBe null
        (attempted.events + afterFirstAccept.events + completed.events)
            .filterIsInstance<ZoneChangeEvent>().filter { it.entityId == commander }.size shouldBe 1
    }

    test("903.9b may apply twice to one event after an intervening redirect when second choice declines") {
        val attempted = ZoneTransitionService.attemptMoveToZone(stateWithCommandToLibraryRedirect(), commander, Zone.HAND)
        val firstPrompt = attempted.state.pendingDecision.shouldBeInstanceOf<YesNoDecision>()
        val afterFirstAccept = services.continuationHandler.resume(
            attempted.state.clearPendingDecision(), YesNoResponse(firstPrompt.id, true)
        )
        val secondPrompt = afterFirstAccept.state.pendingDecision.shouldBeInstanceOf<YesNoDecision>()

        val completed = services.continuationHandler.resume(
            afterFirstAccept.state.clearPendingDecision(), YesNoResponse(secondPrompt.id, false)
        )
        completed.state.getZone(ZoneKey(owner, Zone.LIBRARY)).shouldContainExactly(commander)
        completed.state.getZone(ZoneKey(owner, Zone.COMMAND)).shouldContainExactly()
        completed.state.pendingDecision shouldBe null
        completed.state.activeReplacementChain shouldBe null
        (attempted.events + afterFirstAccept.events + completed.events)
            .filterIsInstance<ZoneChangeEvent>().filter { it.entityId == commander }.size shouldBe 1
    }

    test("a competing zone redirect and Commander replacement give the owner the CR 616 choice") {
        val replacer = EntityId.generate()
        val withRedirect = state().withEntity(replacer, ComponentContainer.of(
            CardComponent("Redirector", "Redirector", ManaCost.ZERO, TypeLine(setOf(), setOf(CardType.ENCHANTMENT)), "", ownerId = owner),
            OwnerComponent(owner), ControllerComponent(owner),
            ReplacementEffectSourceComponent(listOf(
                RedirectZoneChange(Zone.EXILE, EventPattern.ZoneChangeEvent(to = Zone.HAND))
            ))
        )).addToZone(ZoneKey(owner, Zone.BATTLEFIELD), replacer)

        val result = ZoneTransitionService.attemptMoveToZone(withRedirect, commander, Zone.HAND)
        result.isPaused shouldBe true
        result.pendingDecision.shouldBeInstanceOf<ChooseOptionDecision>().options.size shouldBe 2
        result.state.getZone(ZoneKey(owner, Zone.BATTLEFIELD)).shouldContainExactly(commander, replacer)
    }

    test("a controller chooses the competing replacement, while the owner still chooses the Commander replacement") {
        val controller = EntityId.generate()
        val replacer = EntityId.generate()
        val stolenCommander = state()
            .withEntity(controller, ComponentContainer.EMPTY)
            .updateEntity(commander) { it.with(ControllerComponent(controller)) }
            .withEntity(replacer, ComponentContainer.of(
                CardComponent("Redirector", "Redirector", ManaCost.ZERO,
                    TypeLine(setOf(), setOf(CardType.ENCHANTMENT)), "", ownerId = controller),
                OwnerComponent(controller), ControllerComponent(controller),
                ReplacementEffectSourceComponent(listOf(
                    RedirectZoneChange(Zone.EXILE, EventPattern.ZoneChangeEvent(to = Zone.HAND))
                ))
            ))
            .addToZone(ZoneKey(controller, Zone.BATTLEFIELD), replacer)

        val attempt = ZoneTransitionService.attemptMoveToZone(stolenCommander, commander, Zone.HAND)
        val choice = attempt.pendingDecision.shouldBeInstanceOf<ChooseOptionDecision>()

        // CR 616's affected-object chooser is B, its controller; CR 903.9b remains A's option.
        choice.playerId shouldBe controller

        val redirectIndex = choice.options.indexOfFirst { it.startsWith("Redirector -") }
        val redirected = services.continuationHandler.resume(
            attempt.state.clearPendingDecision(), OptionChosenResponse(choice.id, redirectIndex)
        ).state
        redirected.getZone(ZoneKey(owner, Zone.EXILE)).shouldContainExactly(commander)
        redirected.getZone(ZoneKey(owner, Zone.COMMAND)).shouldContainExactly()

        val commandAttempt = ZoneTransitionService.attemptMoveToZone(stolenCommander, commander, Zone.HAND)
        val commandChoice = commandAttempt.pendingDecision.shouldBeInstanceOf<ChooseOptionDecision>()
        val commanderIndex = commandChoice.options.indexOfFirst { it.startsWith("Commander —") }
        val commanderChoiceResult = services.continuationHandler.resume(
            commandAttempt.state.clearPendingDecision(), OptionChosenResponse(commandChoice.id, commanderIndex)
        )
        val commanderChoiceState = commanderChoiceResult.state
        val commanderPrompt = commanderChoiceState.pendingDecision.shouldBeInstanceOf<YesNoDecision>()
        commanderPrompt.playerId shouldBe owner

        val commanded = services.continuationHandler.resume(
            commanderChoiceState.clearPendingDecision(), YesNoResponse(commanderPrompt.id, true)
        ).state
        commanded.getZone(ZoneKey(owner, Zone.COMMAND)).shouldContainExactly(commander)

        val declineAttempt = ZoneTransitionService.attemptMoveToZone(stolenCommander, commander, Zone.HAND)
        val declineChoice = declineAttempt.pendingDecision.shouldBeInstanceOf<ChooseOptionDecision>()
        val declineCommanderIndex = declineChoice.options.indexOfFirst { it.startsWith("Commander —") }
        val declineCommanderResult = services.continuationHandler.resume(
            declineAttempt.state.clearPendingDecision(), OptionChosenResponse(declineChoice.id, declineCommanderIndex)
        )
        val declinePrompt = declineCommanderResult.state.pendingDecision.shouldBeInstanceOf<YesNoDecision>()
        declinePrompt.playerId shouldBe owner

        val declined = services.continuationHandler.resume(
            declineCommanderResult.state.clearPendingDecision(), YesNoResponse(declinePrompt.id, false)
        )
        declined.state.getZone(ZoneKey(owner, Zone.EXILE)).shouldContainExactly(commander)
        declined.state.getZone(ZoneKey(owner, Zone.COMMAND)).shouldContainExactly()
        declined.state.getZone(ZoneKey(owner, Zone.HAND)).shouldContainExactly()
        declined.state.pendingDecision shouldBe null
        (declineCommanderResult.events + declined.events).filterIsInstance<ZoneChangeEvent>().size shouldBe 1
    }

    test("a zone replacement with a rider moves once then runs its rider for the replacement controller") {
        val opponent = EntityId.generate()
        val victim = EntityId.generate()
        val replacer = EntityId.generate()
        val base = GameState()
            .withEntity(owner, ComponentContainer.of(PlayerComponent("Owner", 20), LifeTotalComponent(20)))
            .withEntity(opponent, ComponentContainer.of(PlayerComponent("Opponent", 20), LifeTotalComponent(20)))
            .withEntity(victim, ComponentContainer.of(
                CardComponent("Victim", "Victim", ManaCost.ZERO,
                    TypeLine(setOf(), setOf(CardType.CREATURE)), "", ownerId = opponent),
                OwnerComponent(opponent), ControllerComponent(opponent)
            ))
            .withEntity(replacer, ComponentContainer.of(
                CardComponent("Rider", "Rider", ManaCost.ZERO,
                    TypeLine(setOf(), setOf(CardType.ENCHANTMENT)), "", ownerId = owner),
                OwnerComponent(owner), ControllerComponent(owner),
                ReplacementEffectSourceComponent(listOf(
                    RedirectZoneChangeWithEffect(
                        newDestination = Zone.EXILE,
                        additionalEffect = GainLifeEffect(2),
                        appliesTo = EventPattern.ZoneChangeEvent(from = Zone.BATTLEFIELD, to = Zone.GRAVEYARD)
                    )
                ))
            ))
            .addToZone(ZoneKey(opponent, Zone.BATTLEFIELD), victim)
            .addToZone(ZoneKey(owner, Zone.BATTLEFIELD), replacer)
            .copy(turnOrder = listOf(owner, opponent))

        val result = ZoneTransitionService.attemptMoveToZone(base, victim, Zone.GRAVEYARD)

        result.isPaused shouldBe false
        result.state.getZone(ZoneKey(opponent, Zone.EXILE)).shouldContainExactly(victim)
        result.state.getZone(ZoneKey(opponent, Zone.GRAVEYARD)).shouldContainExactly()
        result.state.getEntity(owner)!!.get<LifeTotalComponent>()!!.life shouldBe 22
        result.state.getEntity(opponent)!!.get<LifeTotalComponent>()!!.life shouldBe 20
    }

    test("choosing a replacement-with-rider from a CR 616 prompt moves once then runs its rider") {
        val rider = EntityId.generate()
        val withLife = state()
            .withEntity(owner, ComponentContainer.of(PlayerComponent("Owner", 20), LifeTotalComponent(20)))
            .withEntity(rider, ComponentContainer.of(
                CardComponent("Rider", "Rider", ManaCost.ZERO,
                    TypeLine(setOf(), setOf(CardType.ENCHANTMENT)), "", ownerId = owner),
                OwnerComponent(owner), ControllerComponent(owner),
                ReplacementEffectSourceComponent(listOf(
                    RedirectZoneChangeWithEffect(
                        newDestination = Zone.EXILE,
                        additionalEffect = GainLifeEffect(2),
                        appliesTo = EventPattern.ZoneChangeEvent(to = Zone.HAND)
                    )
                ))
            ))
            .addToZone(ZoneKey(owner, Zone.BATTLEFIELD), rider)

        val attempt = ZoneTransitionService.attemptMoveToZone(withLife, commander, Zone.HAND)
        val choice = attempt.pendingDecision.shouldBeInstanceOf<ChooseOptionDecision>()
        val riderIndex = choice.options.indexOfFirst { it.startsWith("Rider -") }
        riderIndex shouldBe 0 // options are deterministic here: RedirectZoneChangeWithEffect precedes the Commander rule.

        val resolved = services.continuationHandler.resume(
            attempt.state.clearPendingDecision(), OptionChosenResponse(choice.id, riderIndex)
        ).state
        resolved.getZone(ZoneKey(owner, Zone.EXILE)).shouldContainExactly(commander)
        resolved.getZone(ZoneKey(owner, Zone.HAND)).shouldContainExactly()
        resolved.getZone(ZoneKey(owner, Zone.COMMAND)).shouldContainExactly()
        resolved.getEntity(owner)!!.get<LifeTotalComponent>()!!.life shouldBe 22
    }
})
