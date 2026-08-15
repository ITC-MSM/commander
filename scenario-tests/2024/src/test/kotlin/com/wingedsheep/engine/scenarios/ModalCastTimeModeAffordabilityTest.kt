package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.ChooseOptionDecision
import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.OptionChosenResponse
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.state.components.identity.LifeTotalComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.otj.cards.UnfortunateAccident
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.TypeLine
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.CardScript
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget
import com.wingedsheep.sdk.scripting.effects.DrawCardsEffect
import com.wingedsheep.sdk.scripting.effects.GainLifeEffect
import com.wingedsheep.sdk.scripting.effects.LoseLifeEffect
import com.wingedsheep.sdk.scripting.effects.Mode
import com.wingedsheep.sdk.scripting.effects.ModalEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Cast-time mode selection must respect stacked per-mode additional mana costs
 * (rule 700.2h): each iterative mode pick may only offer modes the caster can still
 * pay for *on top of* the modes already picked. Without the gate, a caster (notably
 * the AI, which drives the iterative decision flow) can lock in an unpayable mode
 * combination, sail through target selection, and dead-end at payment — leaving a
 * pending decision that can never be answered legally (the production Unfortunate
 * Accident soft-lock).
 *
 * The up-front `chosenModes` path is covered by [ModalPerModeAdditionalCostTest];
 * this covers the interactive decision flow in
 * [com.wingedsheep.engine.handlers.actions.spell.CastSpellHandler.presentCastModalModeDecision].
 */
class ModalCastTimeModeAffordabilityTest : FunSpec({

    // Mode 0: free "Gain 1 life"; Mode 1: +{1} "Draw a card"; Mode 2: +{2} "Lose 1 life".
    val StackingCostsModal = CardDefinition(
        name = "Test Stacking Costs Modal",
        manaCost = ManaCost.parse("{R}"),
        typeLine = TypeLine.sorcery(),
        oracleText = "Choose one or more —\n• Gain 1 life\n• {1}: Draw a card\n• {2}: You lose 1 life",
        script = CardScript.spell(
            effect = ModalEffect(
                modes = listOf(
                    Mode.noTarget(
                        GainLifeEffect(DynamicAmount.Fixed(1), EffectTarget.Controller),
                        "Gain 1 life"
                    ),
                    Mode(
                        effect = DrawCardsEffect(DynamicAmount.Fixed(1), EffectTarget.Controller),
                        description = "Pay {1}: Draw a card",
                        additionalManaCost = "{1}"
                    ),
                    Mode(
                        effect = LoseLifeEffect(DynamicAmount.Fixed(1), EffectTarget.Controller),
                        description = "Pay {2}: You lose 1 life",
                        additionalManaCost = "{2}"
                    )
                ),
                chooseCount = 3,
                minChooseCount = 1
            )
        )
    )

    // Base {1}{R}; Mode 0: free "Gain 1 life"; Mode 1: +{2} "Lose 1 life". Cast under
    // the reducer below so the gate must price mode picks through the reduced effective
    // cost, not the printed one.
    val ReducedCostModal = CardDefinition(
        name = "Test Reduced Cost Modal",
        manaCost = ManaCost.parse("{1}{R}"),
        typeLine = TypeLine.sorcery(),
        oracleText = "Choose one or more —\n• Gain 1 life\n• {2}: You lose 1 life",
        script = CardScript.spell(
            effect = ModalEffect(
                modes = listOf(
                    Mode.noTarget(
                        GainLifeEffect(DynamicAmount.Fixed(1), EffectTarget.Controller),
                        "Gain 1 life"
                    ),
                    Mode(
                        effect = LoseLifeEffect(DynamicAmount.Fixed(1), EffectTarget.Controller),
                        description = "Pay {2}: You lose 1 life",
                        additionalManaCost = "{2}"
                    )
                ),
                chooseCount = 2,
                minChooseCount = 1
            )
        )
    )

    val SpellCostReducer = card("Test Spell Cost Reducer") {
        manaCost = "{1}"
        colorIdentity = ""
        typeLine = "Artifact"
        oracleText = "Spells you cast cost {1} less to cast."
        staticAbility {
            ability = ModifySpellCost(
                target = SpellCostTarget.YouCast(GameObjectFilter.Any),
                modification = CostModification.ReduceGeneric(1),
            )
        }
    }

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(
            TestCards.all + StackingCostsModal + ReducedCostModal + SpellCostReducer + UnfortunateAccident
        )
        return d
    }

    test("a mode whose stacked cost is unpayable is not offered on the next pick") {
        val d = driver()
        d.initMirrorMatch(deck = Deck.of("Mountain" to 40))
        val p1 = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // {R} + {2}: can afford base + either costed mode alone, not both stacked.
        d.giveMana(p1, Color.RED, 1)
        d.giveColorlessMana(p1, 2)

        val lifeBefore = d.state.getEntity(p1)!!.get<LifeTotalComponent>()!!.life
        val spell = d.putCardInHand(p1, "Test Stacking Costs Modal")
        d.submit(
            CastSpell(playerId = p1, cardId = spell, paymentStrategy = PaymentStrategy.FromPool)
        ).isPaused shouldBe true

        // Pick 1: every mode is individually affordable, so all three are offered.
        val firstPick = d.pendingDecision.shouldBeInstanceOf<ChooseOptionDecision>()
        firstPick.options shouldBe listOf("Gain 1 life", "Pay {1}: Draw a card", "Pay {2}: You lose 1 life")
        d.submitDecision(p1, OptionChosenResponse(firstPick.id, 2)) // take the {2} mode

        // Pick 2: the {1} mode would stack to {R}{3} (unpayable with {R}{2}) — it must
        // be withheld. The free mode and "Done" remain.
        val secondPick = d.pendingDecision.shouldBeInstanceOf<ChooseOptionDecision>()
        secondPick.options shouldBe listOf("Gain 1 life", "Done")
        d.submitDecision(p1, OptionChosenResponse(secondPick.id, 1)) // Done

        d.bothPass()

        // Only the {2} mode resolved: lose 1 life, no draw, and the cast never soft-locked.
        d.state.pendingDecision shouldBe null
        d.state.getEntity(p1)!!.get<LifeTotalComponent>()!!.life shouldBe (lifeBefore - 1)
    }

    test("with enough mana the same pick still offers every mode") {
        val d = driver()
        d.initMirrorMatch(deck = Deck.of("Mountain" to 40))
        val p1 = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // {R} + {3}: both costed modes stack to {R}{3} — affordable.
        d.giveMana(p1, Color.RED, 1)
        d.giveColorlessMana(p1, 3)

        val spell = d.putCardInHand(p1, "Test Stacking Costs Modal")
        d.submit(
            CastSpell(playerId = p1, cardId = spell, paymentStrategy = PaymentStrategy.FromPool)
        ).isPaused shouldBe true

        val firstPick = d.pendingDecision.shouldBeInstanceOf<ChooseOptionDecision>()
        d.submitDecision(p1, OptionChosenResponse(firstPick.id, 2)) // the {2} mode

        val secondPick = d.pendingDecision.shouldBeInstanceOf<ChooseOptionDecision>()
        secondPick.options shouldContain "Pay {1}: Draw a card"
    }

    test("a mode affordable only through a cost-reduction static is still offered") {
        val d = driver()
        d.initMirrorMatch(deck = Deck.of("Mountain" to 40))
        val p1 = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        d.putPermanentOnBattlefield(p1, "Test Spell Cost Reducer")
        // {R} + {2}: the reduced base ({1}{R} less {1} = {R}) plus the {2} mode is exactly
        // affordable. Priced at the printed {1}{R} the mode would (wrongly) be withheld —
        // the gate must use the same cost pipeline payment does.
        d.giveMana(p1, Color.RED, 1)
        d.giveColorlessMana(p1, 2)

        val lifeBefore = d.state.getEntity(p1)!!.get<LifeTotalComponent>()!!.life
        val spell = d.putCardInHand(p1, "Test Reduced Cost Modal")
        d.submit(
            CastSpell(playerId = p1, cardId = spell, paymentStrategy = PaymentStrategy.FromPool)
        ).isPaused shouldBe true

        val firstPick = d.pendingDecision.shouldBeInstanceOf<ChooseOptionDecision>()
        firstPick.options shouldBe listOf("Gain 1 life", "Pay {2}: You lose 1 life")
        d.submitDecision(p1, OptionChosenResponse(firstPick.id, 1)) // the {2} mode

        val secondPick = d.pendingDecision.shouldBeInstanceOf<ChooseOptionDecision>()
        secondPick.options shouldBe listOf("Gain 1 life", "Done")
        d.submitDecision(p1, OptionChosenResponse(secondPick.id, 1)) // Done

        d.bothPass()

        // The {2} mode resolved, paid through the reduced cost — no payment dead-end.
        d.state.pendingDecision shouldBe null
        d.state.getEntity(p1)!!.get<LifeTotalComponent>()!!.life shouldBe (lifeBefore - 1)
    }

    test("Unfortunate Accident with 4 mana: destroy mode picked, token mode withheld, cast completes") {
        // Reproduces the production soft-lock: Spree base {B}, +{2}{B} destroy, +{1} token.
        // With {B}{B}{2} the destroy mode alone ({2}{B}{B}) is exactly affordable; adding
        // the token mode ({3}{B}{B}) is not. The old flow offered it anyway and the cast
        // dead-ended at payment after target selection.
        val d = driver()
        d.initMirrorMatch(deck = Deck.of("Swamp" to 40))
        val p1 = d.activePlayer!!
        val p2 = d.getOpponent(p1)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val victim = d.putCreatureOnBattlefield(p2, "Grizzly Bears")
        d.giveMana(p1, Color.BLACK, 2)
        d.giveColorlessMana(p1, 2)

        val spell = d.putCardInHand(p1, "Unfortunate Accident")
        d.submit(
            CastSpell(playerId = p1, cardId = spell, paymentStrategy = PaymentStrategy.FromPool)
        ).isPaused shouldBe true

        // Pick 1: both spree modes are individually affordable.
        val firstPick = d.pendingDecision.shouldBeInstanceOf<ChooseOptionDecision>()
        firstPick.options.size shouldBe 2
        val destroyIndex = firstPick.options.indexOfFirst { it.contains("Destroy") }
        destroyIndex shouldNotBe -1
        d.submitDecision(p1, OptionChosenResponse(firstPick.id, destroyIndex))

        // Pick 2: the token mode would stack past the available mana — only "Done" is left.
        val secondPick = d.pendingDecision.shouldBeInstanceOf<ChooseOptionDecision>()
        secondPick.options shouldBe listOf("Done")
        d.submitDecision(p1, OptionChosenResponse(secondPick.id, 0))

        // Target selection for the destroy mode, then the cast must complete (no payment
        // dead-end) and resolve.
        d.pendingDecision.shouldBeInstanceOf<ChooseTargetsDecision>()
        d.submitTargetSelection(p1, listOf(victim))
        d.bothPass()

        d.state.pendingDecision shouldBe null
        d.findPermanent(p2, "Grizzly Bears") shouldBe null
        d.getGraveyardCardNames(p2) shouldContain "Grizzly Bears"
    }
})
