package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.state.components.battlefield.AttachedToComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lci.cards.SunfireTorch
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe

/**
 * Sunfire Torch (LCI #167) — {R} Artifact — Equipment.
 *
 * "Equipped creature gets +1/+0 and has 'Whenever this creature attacks, you may sacrifice
 *  Sunfire Torch. When you do, this creature deals 2 damage to any target.'"
 *
 * Focus: the granted attack trigger's "sacrifice Sunfire Torch" refers only to the specific
 * granting Equipment (CR 201.5a). The sacrifice is scoped with `.attachedToSource()`, so with a
 * second Sunfire Torch elsewhere on the battlefield only the one attached to the attacking
 * creature is a legal sacrifice — and that is the one that goes to the graveyard.
 */
class SunfireTorchScenarioTest : FunSpec({

    val equipAbilityId = SunfireTorch.activatedAbilities.single { it.isEquipAbility }.id

    fun setup(): GameTestDriver = GameTestDriver().apply {
        registerCards(TestCards.all)
        initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20, skipMulligans = true)
    }

    test("granted attack trigger sacrifices the attached Torch (only it is legal) and deals 2 damage") {
        val d = setup()
        val p1 = d.activePlayer!!
        val opponent = d.getOpponent(p1)

        val courser = d.putCreatureOnBattlefield(p1, "Centaur Courser") // 3/3
        d.removeSummoningSickness(courser)
        val attachedTorch = d.putPermanentOnBattlefield(p1, "Sunfire Torch")
        // A second, unattached Sunfire Torch that must NOT be a legal sacrifice.
        val otherTorch = d.putPermanentOnBattlefield(p1, "Sunfire Torch")

        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        d.giveColorlessMana(p1, 1)
        d.submit(
            ActivateAbility(
                playerId = p1,
                sourceId = attachedTorch,
                abilityId = equipAbilityId,
                targets = listOf(ChosenTarget.Permanent(courser))
            )
        ).isSuccess shouldBe true
        d.bothPass()
        d.state.getEntity(attachedTorch)?.get<AttachedToComponent>()?.targetId shouldBe courser

        d.passPriorityUntil(Step.DECLARE_ATTACKERS)
        d.declareAttackers(p1, listOf(courser), opponent).error shouldBe null

        // Resolve the granted attack trigger by decision shape (may → sacrifice → reflexive
        // damage target). The `.attachedToSource()` scope means the sacrifice has exactly one
        // legal target (the attached Torch), so it never presents a two-way choice — the key
        // discriminator: under the old `youControl().named(...)` filter BOTH Torches would be
        // offerable. Assert the unattached Torch is never an offered sacrifice, and hit the
        // opponent with the 2 damage.
        var guard = 0
        var otherTorchWasOfferable = false
        while (guard++ < 8) {
            while (d.pendingDecision == null && d.state.stack.isNotEmpty()) d.bothPass()
            when (val decision = d.pendingDecision) {
                is YesNoDecision -> d.submitYesNo(p1, true).error shouldBe null
                is ChooseTargetsDecision -> {
                    val legal = decision.legalTargets[0].orEmpty()
                    if (otherTorch in legal) otherTorchWasOfferable = true
                    when {
                        attachedTorch in legal ->
                            d.submitTargetSelection(p1, listOf(attachedTorch)).error shouldBe null
                        else -> // "deal 2 damage to any target" — hit the opponent.
                            d.submitTargetSelection(p1, listOf(opponent)).error shouldBe null
                    }
                }
                is SelectCardsDecision -> {
                    if (otherTorch in decision.options) otherTorchWasOfferable = true
                    d.submitCardSelection(p1, listOf(attachedTorch))
                }
                else -> break
            }
        }
        while (d.pendingDecision == null && d.state.stack.isNotEmpty()) d.bothPass()

        // The unattached Torch was never a legal sacrifice (the granting-permanent rule).
        otherTorchWasOfferable shouldBe false
        // The attached Torch was sacrificed; the second Torch is untouched; 2 damage dealt.
        d.getGraveyard(p1) shouldContain attachedTorch
        d.findPermanent(p1, "Sunfire Torch") shouldBe otherTorch
        d.getLifeTotal(opponent) shouldBe 18
    }
})
