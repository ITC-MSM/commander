package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.handlers.effects.permanent.types.ChangeColorExecutor
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.effects.ChangeColorEffect
import com.wingedsheep.sdk.scripting.predicates.CardPredicate
import com.wingedsheep.engine.handlers.EffectContext
import com.wingedsheep.engine.handlers.PredicateContext
import com.wingedsheep.engine.handlers.PredicateEvaluator
import com.wingedsheep.engine.handlers.effects.TargetResolutionUtils
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.EntityReference
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class LibraryTopReferenceTest : FunSpec({
    fun driver() = GameTestDriver().apply {
        registerCards(TestCards.all)
        initMirrorMatch(deck = Deck.of("Forest" to 40), skipMulligans = true, startingPlayer = 0)
    }

    test("a target-player reference uses the chosen player in effect and predicate contexts") {
        val d = driver()
        val top = d.putCardOnTopOfLibrary(d.player2, "Grizzly Bears")
        val creature = d.putCreatureOnBattlefield(d.player1, "Grizzly Bears")
        val ctx = EffectContext(sourceId = null, controllerId = d.player1, targets = listOf(ChosenTarget.Player(d.player2)))
        TargetResolutionUtils.resolveTarget(EffectTarget.LibraryTop(Player.TargetPlayer), ctx, d.state) shouldBe top
        PredicateEvaluator().matches(d.state, d.state.projectedState, creature,
            GameObjectFilter.Creature.sharingColorWith(EntityReference.LibraryTop(Player.TargetPlayer)),
            PredicateContext.fromEffectContext(ctx)) shouldBe true
        d.putCardOnTopOfLibrary(d.player2, "Lightning Bolt")
        PredicateEvaluator().matches(d.state, d.state.projectedState, creature,
            GameObjectFilter.Creature.sharingColorWith(EntityReference.LibraryTop(Player.TargetPlayer)),
            PredicateContext.fromEffectContext(ctx)) shouldBe false
    }

    test("projection supports nested color relationships and respects a colorless reference") {
        val d = driver()
        val leader = card("Color leader") {
            manaCost = "{G}"
            typeLine = "Creature — Bear"
            power = 2
            toughness = 2
            staticAbility {
                ability = ModifyStats(1, 1, GroupFilter(GameObjectFilter.Creature.youControl().copy(
                    cardPredicates = listOf(CardPredicate.IsCreature,
                        CardPredicate.Or(listOf(CardPredicate.SharesColorWith(EntityReference.Source))))
                )))
            }
        }
        d.registerCards(listOf(leader))
        val source = d.putCreatureOnBattlefield(d.player1, "Color leader")
        val bear = d.putCreatureOnBattlefield(d.player1, "Grizzly Bears")
        d.state.projectedState.getPower(bear) shouldBe 3
        d.replaceState(ChangeColorExecutor().execute(d.state,
            ChangeColorEffect(target = EffectTarget.SpecificEntity(source), colors = emptySet()),
            EffectContext(sourceId = source, controllerId = d.player1)).state)
        d.state.projectedState.getPower(bear) shouldBe 2
        d.state.projectedState.getPower(source) shouldBe 2
    }

    test("unbound and group player references do not silently choose a library") {
        val d = driver()
        val ctx = EffectContext(sourceId = null, controllerId = d.player1)
        for (player in listOf(Player.TargetPlayer, Player.Each, Player.EachOpponent)) {
            TargetResolutionUtils.resolveTarget(EffectTarget.LibraryTop(player), ctx, d.state) shouldBe null
            TargetResolutionUtils.resolveEntityReference(EntityReference.LibraryTop(player), ctx, d.state) shouldBe null
        }
    }
})
