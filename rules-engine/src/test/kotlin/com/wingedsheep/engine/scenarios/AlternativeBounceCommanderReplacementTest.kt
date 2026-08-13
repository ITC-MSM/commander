package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.AlternativeCostType
import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.ManaSpentEvent
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.core.YesNoResponse
import com.wingedsheep.engine.core.ZoneChangeEvent
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.CommanderComponent
import com.wingedsheep.engine.state.components.combat.AttackingComponent
import com.wingedsheep.engine.state.components.combat.BlockersDeclaredThisCombatComponent
import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.engine.state.components.stack.SpellOnStackComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Format
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.sneak
import com.wingedsheep.sdk.dsl.webSlinging
import com.wingedsheep.sdk.scripting.AdditionalCostPayment
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/** Commander 903.9b regression gate for the two alternative-cost return paths. */
class AlternativeBounceCommanderReplacementTest : ScenarioTestBase() {
    init {
        cardRegistry.register(card("Commander Sneak Spell") { manaCost = "{G}"; typeLine = "Creature — Ninja"; power = 1; toughness = 1; sneak("{G}") })
        cardRegistry.register(card("Commander Web Spell") { manaCost = "{G}"; typeLine = "Creature — Spider"; power = 1; toughness = 1; webSlinging("{G}") })

        fun begin(cardName: String, type: AlternativeCostType, accept: Boolean): Pair<TestGame, com.wingedsheep.engine.core.ExecutionResult> {
            val game = scenario().withPlayers("P1", "P2").withCardInHand(1, cardName)
                .withCardOnBattlefield(1, "Grizzly Bears", tapped = type == AlternativeCostType.WEB_SLINGING)
                .withActivePlayer(1)
                .inPhase(if (type == AlternativeCostType.SNEAK) Phase.COMBAT else Phase.PRECOMBAT_MAIN,
                    if (type == AlternativeCostType.SNEAK) Step.DECLARE_BLOCKERS else Step.PRECOMBAT_MAIN).build()
            val spell = game.state.getHand(game.player1Id).single()
            val commander = game.state.getBattlefield(game.player1Id).single {
                game.state.getEntity(it)?.get<CardComponent>()?.name == "Grizzly Bears"
            }
            game.state = game.state.copy(format = Format.Commander())
                .updateEntity(game.player1Id) { it.with((it.get<ManaPoolComponent>() ?: ManaPoolComponent()).add(Color.GREEN)) }
                .updateEntity(commander) { it.with(CommanderComponent(game.player1Id)) }
            if (type == AlternativeCostType.SNEAK) {
                game.state = game.state.updateEntity(commander) { it.with(AttackingComponent(game.player2Id)) }
                    .updateEntity(game.player2Id) { it.with(BlockersDeclaredThisCombatComponent) }
            }
            val paused = game.execute(CastSpell(game.player1Id, spell, useAlternativeCost = true, alternativeCostType = type,
                additionalCostPayment = AdditionalCostPayment(bouncedPermanents = listOf(commander)), paymentStrategy = PaymentStrategy.FromPool))
            paused.isPaused shouldBe true
            paused.state.stack.size shouldBe 0
            paused.state.getBattlefield(game.player1Id) shouldContain commander
            val decision = paused.state.pendingDecision.shouldBeInstanceOf<YesNoDecision>()
            return game to game.submitDecision(YesNoResponse(decision.id, accept))
        }

        listOf(AlternativeCostType.SNEAK, AlternativeCostType.WEB_SLINGING).forEach { type ->
            listOf(true, false).forEach { accept -> test("$type Commander choice $accept moves once and casts once") {
                val cardName = if (type == AlternativeCostType.SNEAK) "Commander Sneak Spell" else "Commander Web Spell"
                val (game, completed) = begin(cardName, type, accept)
                val commander = game.state.entities.entries.first { it.value.get<CardComponent>()?.name == "Grizzly Bears" }.key
                completed.events.filterIsInstance<ZoneChangeEvent>().count { it.entityId == commander } shouldBe 1
                completed.events.filterIsInstance<ManaSpentEvent>().size shouldBe 1
                if (accept) game.state.getZone(ZoneKey(game.player1Id, Zone.COMMAND)) shouldContain commander
                else game.state.getHand(game.player1Id) shouldContain commander
                game.state.stack.size shouldBe 1
                val stack = game.state.getEntity(game.state.stack.single())!!.get<SpellOnStackComponent>()!!
                if (type == AlternativeCostType.SNEAK) { stack.wasSneaked shouldBe true; stack.sneakAttackDefenderId shouldBe game.player2Id }
                else { stack.wasWebSlung shouldBe true; stack.webSlungReturnedManaValue shouldBe 2 }
            }}
        }
    }
}
