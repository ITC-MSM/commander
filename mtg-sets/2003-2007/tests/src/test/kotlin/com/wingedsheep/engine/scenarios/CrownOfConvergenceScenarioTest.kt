package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.handlers.effects.permanent.types.ChangeColorExecutor
import com.wingedsheep.engine.handlers.effects.permanent.control.GainControlExecutor
import com.wingedsheep.sdk.scripting.effects.ChangeColorEffect
import com.wingedsheep.sdk.scripting.effects.GainControlEffect
import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.handlers.ConditionEvaluator
import com.wingedsheep.engine.handlers.EffectContext
import com.wingedsheep.engine.handlers.effects.TargetResolutionUtils
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.engine.view.ClientStateTransformer
import com.wingedsheep.mtg.sets.definitions.rav.cards.CrownOfConvergence
import com.wingedsheep.mtg.sets.definitions.rav.cards.Watchwolf
import com.wingedsheep.mtg.sets.definitions.rav.cards.CourierHawk
import com.wingedsheep.mtg.sets.definitions.rav.cards.GlassGolem
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.EntityReference
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain

class CrownOfConvergenceScenarioTest : FunSpec({
    fun driver() = GameTestDriver().apply {
        registerCards(TestCards.all + listOf(CrownOfConvergence, Watchwolf, CourierHawk, GlassGolem))
        initMirrorMatch(deck = Deck.of("Forest" to 40), skipMulligans = true, startingPlayer = 0)
        passPriorityUntil(Step.PRECOMBAT_MAIN)
    }

    test("multicolor top gives one bonus to each matching creature you control") {
        val d = driver()
        d.putCreatureOnBattlefield(d.player1, "Crown of Convergence")
        val wolf = d.putCreatureOnBattlefield(d.player1, "Watchwolf")
        val hawk = d.putCreatureOnBattlefield(d.player1, "Courier Hawk")
        val golem = d.putCreatureOnBattlefield(d.player1, "Glass Golem")
        val enemy = d.putCreatureOnBattlefield(d.player2, "Watchwolf")
        d.putCardOnTopOfLibrary(d.player1, "Watchwolf")
        d.state.projectedState.getPower(wolf) shouldBe 4
        d.state.projectedState.getToughness(wolf) shouldBe 4
        d.state.projectedState.getPower(hawk) shouldBe 2
        d.state.projectedState.getPower(golem) shouldBe 6
        d.state.projectedState.getPower(enemy) shouldBe 3
    }

    test("noncreature and colorless tops give no bonus and changing top updates immediately") {
        val d = driver()
        d.putCreatureOnBattlefield(d.player1, "Crown of Convergence")
        val wolf = d.putCreatureOnBattlefield(d.player1, "Watchwolf")
        val golem = d.putCreatureOnBattlefield(d.player1, "Glass Golem")
        d.putCardOnTopOfLibrary(d.player1, "Giant Growth")
        d.state.projectedState.getPower(wolf) shouldBe 3
        d.putCardOnTopOfLibrary(d.player1, "Watchwolf")
        d.state.projectedState.getPower(wolf) shouldBe 4
        d.putCardOnTopOfLibrary(d.player1, "Glass Golem")
        d.state.projectedState.getPower(wolf) shouldBe 3
        d.state.projectedState.getPower(golem) shouldBe 6
    }

    test("activation moves the resolution-time top to bottom even after Crown leaves") {
        val d = driver()
        val crown = d.putCreatureOnBattlefield(d.player1, "Crown of Convergence")
        val original = d.putCardOnTopOfLibrary(d.player1, "Watchwolf")
        d.giveMana(d.player1, Color.GREEN)
        d.giveMana(d.player1, Color.WHITE)
        d.submitSuccess(ActivateAbility(d.player1, crown, CrownOfConvergence.activatedAbilities.single().id))
        val newTop = d.putCardOnTopOfLibrary(d.player1, "Courier Hawk")
        d.moveToGraveyard(crown)
        d.bothPass()
        d.state.getLibrary(d.player1).last() shouldBe newTop
        d.state.getLibrary(d.player1).first() shouldBe original
    }

    test("empty library has no matching card and activation still resolves") {
        val d = driver()
        val crown = d.putCreatureOnBattlefield(d.player1, "Crown of Convergence")
        val wolf = d.putCreatureOnBattlefield(d.player1, "Watchwolf")
        d.replaceState(d.state.copy(zones = d.state.zones + (ZoneKey(d.player1, Zone.LIBRARY) to emptyList())))
        d.state.projectedState.getPower(wolf) shouldBe 3
        val ctx = EffectContext(controllerId = d.player1, sourceId = crown)
        TargetResolutionUtils.resolveEntityReference(EntityReference.LibraryTop(), ctx, d.state) shouldBe null
        d.giveMana(d.player1, Color.GREEN)
        d.giveMana(d.player1, Color.WHITE)
        d.submitSuccess(ActivateAbility(d.player1, crown, CrownOfConvergence.activatedAbilities.single().id))
        d.bothPass()
        d.state.getLibrary(d.player1) shouldBe emptyList()
    }

    test("top card is public to both seats and reveal ends when Crown leaves") {
        val d = driver()
        val crown = d.putCreatureOnBattlefield(d.player1, "Crown of Convergence")
        val top = d.putCardOnTopOfLibrary(d.player1, "Watchwolf")
        val hidden = d.putCardOnTopOfLibrary(d.player2, "Courier Hawk")
        val transformer = ClientStateTransformer(cardRegistry = d.cardRegistry)
        for (viewer in listOf(d.player1, d.player2)) {
            val view = transformer.transform(d.state, viewingPlayerId = viewer)
            view.cards.keys shouldContain top
            view.cards.keys shouldNotContain hidden
        }
        d.moveToGraveyard(crown)
        transformer.transform(d.state, viewingPlayerId = d.player2).cards.keys shouldNotContain top
    }

    test("the bonus reads projected colors and stops when Crown leaves") {
        val d = driver()
        val crown = d.putCreatureOnBattlefield(d.player1, "Crown of Convergence")
        val hawk = d.putCreatureOnBattlefield(d.player1, "Courier Hawk")
        d.putCardOnTopOfLibrary(d.player1, "Watchwolf")
        d.state.projectedState.getPower(hawk) shouldBe 2
        val context = EffectContext(controllerId = d.player1, sourceId = crown)
        d.replaceState(ChangeColorExecutor().execute(d.state,
            ChangeColorEffect(target = EffectTarget.SpecificEntity(hawk), colors = setOf("BLUE")), context).state)
        d.state.projectedState.getPower(hawk) shouldBe 1
        d.replaceState(ChangeColorExecutor().execute(d.state,
            ChangeColorEffect(target = EffectTarget.SpecificEntity(hawk), colors = setOf("GREEN")), context).state)
        d.state.projectedState.getPower(hawk) shouldBe 2
        d.moveToGraveyard(crown)
        d.state.projectedState.getPower(hawk) shouldBe 1
    }

    test("stealing Crown switches both the library reference and the creatures receiving the bonus") {
        val d = driver()
        val crown = d.putCreatureOnBattlefield(d.player1, "Crown of Convergence")
        val mine = d.putCreatureOnBattlefield(d.player1, "Watchwolf")
        val theirs = d.putCreatureOnBattlefield(d.player2, "Courier Hawk")
        val oldTop = d.putCardOnTopOfLibrary(d.player1, "Glass Golem")
        val top = d.putCardOnTopOfLibrary(d.player2, "Watchwolf")
        d.replaceState(GainControlExecutor().execute(d.state,
            GainControlEffect(target = EffectTarget.SpecificEntity(crown)),
            EffectContext(sourceId = null, controllerId = d.player2)).state)
        d.state.projectedState.getPower(mine) shouldBe 3
        d.state.projectedState.getPower(theirs) shouldBe 2
        ClientStateTransformer(cardRegistry = d.cardRegistry)
            .transform(d.state, viewingPlayerId = d.player1).cards.keys shouldContain top
        ClientStateTransformer(cardRegistry = d.cardRegistry)
            .transform(d.state, viewingPlayerId = d.player2).cards.keys shouldNotContain oldTop
    }

    test("player-relative references and conditions read the same live card at resolution") {
        val d = driver()
        val top = d.putCardOnTopOfLibrary(d.player2, "Watchwolf")
        val ctx = EffectContext(sourceId = null, controllerId = d.player1)
        TargetResolutionUtils.resolveEntityReference(EntityReference.LibraryTop(Player.AnOpponent), ctx, d.state) shouldBe top
        TargetResolutionUtils.resolveTarget(EffectTarget.LibraryTop(Player.AnOpponent), ctx, d.state) shouldBe top
        ConditionEvaluator().evaluate(d.state,
            Conditions.EntityMatches(EffectTarget.LibraryTop(Player.AnOpponent), GameObjectFilter.Creature), ctx) shouldBe true
    }
})
