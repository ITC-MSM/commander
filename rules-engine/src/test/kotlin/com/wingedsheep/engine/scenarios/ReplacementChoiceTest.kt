package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseOptionDecision
import com.wingedsheep.engine.handlers.EffectContext
import com.wingedsheep.engine.replacement.PendingGameEvent
import com.wingedsheep.engine.replacement.ProcessorResult
import com.wingedsheep.engine.replacement.ReplacementEffectProcessor
import com.wingedsheep.engine.state.ComponentContainer
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.battlefield.ReplacementEffectSourceComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.ControllerComponent
import com.wingedsheep.engine.state.components.identity.OwnerComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.TypeLine
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.ReplaceDrawWithEffect
import com.wingedsheep.sdk.scripting.RedirectZoneChange
import com.wingedsheep.sdk.scripting.ReplacementEffect
import com.wingedsheep.sdk.scripting.effects.DrawCardsEffect
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotContain
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * CR 616.1 hands the choice between competing replacement effects to the affected player, and
 * 616.1a–d each say "one of them **must be chosen**" — the player picks among that group's
 * members. `ReplacementEffectProcessor` only offers that choice for the
 * [com.wingedsheep.sdk.scripting.ReplacementPriorityGroup.ANY] group; every higher group falls
 * straight to `applySingle(groupEffects.first())`, so two competing self-replacements silently
 * resolve in gather order.
 *
 * Also pins the text the choice is rendered with, since `GatheredReplacement.description` is
 * what the player reads in the `ChooseOptionDecision`.
 */
class ReplacementChoiceTest : FunSpec({

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all)
        d.initMirrorMatch(deck = Deck.of("Plains" to 20))
        return d
    }

    fun GameState.withReplacementPermanent(
        controllerId: EntityId,
        name: String,
        effect: ReplacementEffect
    ): GameState {
        val permanentId = EntityId.generate()
        val container = ComponentContainer.of(
            CardComponent(
                cardDefinitionId = name,
                name = name,
                manaCost = ManaCost.ZERO,
                typeLine = TypeLine.parse("Enchantment"),
                oracleText = effect.description,
                colors = emptySet(),
                ownerId = controllerId,
            ),
            OwnerComponent(controllerId),
            ControllerComponent(controllerId),
            ReplacementEffectSourceComponent(listOf(effect))
        )
        return withEntity(permanentId, container)
            .addToZone(ZoneKey(controllerId, Zone.BATTLEFIELD), permanentId)
    }

    test("two competing SELF_REPLACEMENT effects present a choice (CR 616.1a)") {
        // RedirectZoneChange(selfOnly = true) is used purely as a carrier: it is the shipped
        // SDK type that declares a non-ANY priorityGroup, and the processor's group bucketing
        // is documented as domain-agnostic, so it is exactly the branch under test. Pointing
        // its appliesTo at a DrawEvent is what routes it into the one wired PendingGameEvent.
        val d = driver()
        val me = d.player1
        val selfReplacement = { name: String ->
            RedirectZoneChange(
                newDestination = Zone.EXILE,
                appliesTo = EventPattern.DrawEvent(),
                selfOnly = true
            ).let { name to it }
        }

        var state = d.state
        selfReplacement("Redirector A").let { (n, e) -> state = state.withReplacementPermanent(me, n, e) }
        selfReplacement("Redirector B").let { (n, e) -> state = state.withReplacementPermanent(me, n, e) }

        val processor = ReplacementEffectProcessor()
        val event = PendingGameEvent.DrawPending(me, 1)

        withClue("Sanity: both effects were gathered and land in the same priority group") {
            processor.gatherReplacements(state, event).size shouldBe 2
        }

        val outcome = runCatching { processor.process(state, event, EffectContext(EntityId.generate(), me)) }
        withClue(
            "CR 616.1a — with two applicable self-replacement effects the affected player " +
                "chooses which to apply, so the processor must pause. Instead it auto-applied " +
                "the first, because the `size > 1` guard is nested inside the ANY branch. " +
                "Got: ${outcome.exceptionOrNull()?.message ?: outcome.getOrNull()}"
        ) {
            outcome.getOrNull().shouldBeInstanceOf<ProcessorResult.Paused>()
        }
    }

    test("two competing ANY-group effects from different sources present a choice (CR 616.1e)") {
        val d = driver()
        val me = d.player1

        var state = d.state
        state = state.withReplacementPermanent(
            me, "Replacer A", ReplaceDrawWithEffect(replacementEffect = DrawCardsEffect(1))
        )
        state = state.withReplacementPermanent(
            me, "Replacer B", ReplaceDrawWithEffect(replacementEffect = DrawCardsEffect(2))
        )

        val processor = ReplacementEffectProcessor()
        val result = processor.process(state, PendingGameEvent.DrawPending(me, 1), EffectContext(EntityId.generate(), me))

        result.shouldBeInstanceOf<ProcessorResult.Paused>()
        val decision = result.decision
        decision.shouldBeInstanceOf<ChooseOptionDecision>()
        decision.options.size shouldBe 2
    }

    test("the replacement choice a player reads has no dangling 'while' clause") {
        val d = driver()
        val me = d.player1

        var state = d.state
        state = state.withReplacementPermanent(
            me, "Replacer A", ReplaceDrawWithEffect(replacementEffect = DrawCardsEffect(1))
        )
        state = state.withReplacementPermanent(
            me, "Replacer B", ReplaceDrawWithEffect(replacementEffect = DrawCardsEffect(2))
        )

        val result = ReplacementEffectProcessor().process(state, PendingGameEvent.DrawPending(me, 1), EffectContext(EntityId.generate(), me))
        result.shouldBeInstanceOf<ProcessorResult.Paused>()
        val decision = result.decision
        decision.shouldBeInstanceOf<ChooseOptionDecision>()

        for (option in decision.options) {
            withClue("Player-facing choice option: \"$option\"") {
                option shouldNotContain " while ,"
            }
        }
    }
})
