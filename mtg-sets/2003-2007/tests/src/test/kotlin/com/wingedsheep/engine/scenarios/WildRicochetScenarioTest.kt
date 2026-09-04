package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lrw.cards.WildRicochet
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.TypeLine
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.CardScript
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Wild Ricochet (LRW #196) — "You may choose new targets for target instant or sorcery spell. Then
 * copy that spell. You may choose new targets for the copy."
 *
 * The card is the first user of `ChangeTriggeringObjectTargetsEffect.spell`, so both halves of that
 * parameter are under test: that the retarget reads a *targeted* spell rather than
 * `context.triggeringEntityId` (which is null here — the card is cast, not triggered, so the
 * pre-parameter effect silently did nothing at all), and that the composite resumes into the copy
 * after the retarget pauses for a decision.
 *
 * The second test is the reason the effect isn't [com.wingedsheep.sdk.scripting.effects.ChangeTargetEffect]:
 * that one swaps a single target and no-ops on a spell with more than one, so a two-target spell
 * would keep both originals and the card would still *look* right. Two slots, two prompts, both
 * moved.
 */
class WildRicochetScenarioTest : FunSpec({

    /**
     * A two-target burn spell — two separate one-target requirements, so the retarget has two slots
     * to walk. Nothing in TestCards targets twice.
     */
    val SplitShock = CardDefinition(
        name = "Split Shock",
        manaCost = ManaCost.parse("{1}{R}"),
        typeLine = TypeLine.parse("Instant"),
        oracleText = "Split Shock deals 1 damage to target creature and 1 damage to another target creature.",
        script = CardScript.spell(
            Effects.Composite(
                Effects.DealDamage(1, EffectTarget.ContextTarget(0)),
                Effects.DealDamage(1, EffectTarget.ContextTarget(1)),
            ),
            TargetObject(filter = TargetFilter.Creature),
            TargetObject(filter = TargetFilter.Creature),
        )
    )

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + listOf(WildRicochet, SplitShock))
        d.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    test("retargets the original spell, then copies it and retargets the copy") {
        val d = driver()
        val p1 = d.player1
        val p2 = d.getOpponent(p1)

        val bolt = d.putCardInHand(p2, "Lightning Bolt")
        val ricochet = d.putCardInHand(p1, "Wild Ricochet")
        d.giveMana(p2, Color.RED, 1)
        d.giveMana(p1, Color.RED, 4)

        d.passPriority(p1)
        d.submit(
            CastSpell(p2, bolt, targets = listOf(ChosenTarget.Player(p1)), paymentStrategy = PaymentStrategy.FromPool)
        ).isSuccess shouldBe true
        val boltOnStack = d.getTopOfStack()!!
        d.passPriority(p2)

        d.submit(
            CastSpell(
                p1, ricochet,
                targets = listOf(ChosenTarget.Spell(boltOnStack)),
                paymentStrategy = PaymentStrategy.FromPool
            )
        ).isSuccess shouldBe true

        d.bothPass() // Wild Ricochet resolves and pauses on the original's one target slot

        // Slot 1: point the original Bolt at its own caster instead. Each of these submissions
        // hands back another pending decision, so `isSuccess` (which also demands an idle engine)
        // is false by design — `error` is the assertion that means "accepted".
        d.submitCardSelection(p1, listOf(p2)).error shouldBe null

        // The copy is on the stack now and asks for its own targets (CR 707.10c).
        withClue("the composite must resume into the copy after the retarget pause") {
            (d.state.pendingDecision is ChooseTargetsDecision) shouldBe true
        }
        d.submitTargetSelection(p1, listOf(p2)).error shouldBe null

        // Copy resolves, then the original.
        var guard = 0
        while (d.stackSize > 0 && guard < 10) {
            d.bothPass()
            guard++
        }

        withClue("both the copy and the retargeted original hit the Bolt's own caster") {
            d.getLifeTotal(p2) shouldBe 14
        }
        d.getLifeTotal(p1) shouldBe 20
    }

    test("every target slot is offered, not just the first") {
        val d = driver()
        val p1 = d.player1
        val p2 = d.getOpponent(p1)

        val mine = d.putCreatureOnBattlefield(p1, "Savannah Lions")
        val alsoMine = d.putCreatureOnBattlefield(p1, "Centaur Courser")
        val theirs = d.putCreatureOnBattlefield(p2, "Savannah Lions")
        val alsoTheirs = d.putCreatureOnBattlefield(p2, "Centaur Courser")

        val shock = d.putCardInHand(p2, "Split Shock")
        val ricochet = d.putCardInHand(p1, "Wild Ricochet")
        d.giveMana(p2, Color.RED, 2)
        d.giveMana(p1, Color.RED, 4)

        d.passPriority(p1)
        d.submit(
            CastSpell(
                p2, shock,
                targets = listOf(ChosenTarget.Permanent(mine), ChosenTarget.Permanent(alsoMine)),
                paymentStrategy = PaymentStrategy.FromPool
            )
        ).isSuccess shouldBe true
        val shockOnStack = d.getTopOfStack()!!
        d.passPriority(p2)

        d.submit(
            CastSpell(
                p1, ricochet,
                targets = listOf(ChosenTarget.Spell(shockOnStack)),
                paymentStrategy = PaymentStrategy.FromPool
            )
        ).isSuccess shouldBe true

        d.bothPass()

        // Both slots are walked — a single-target retarget would move only the first and leave the
        // second pointed at my own Courser.
        d.submitCardSelection(p1, listOf(theirs)).error shouldBe null
        d.submitCardSelection(p1, listOf(alsoTheirs)).error shouldBe null

        // The copy's own prompt: keep the (already opposing) targets.
        (d.state.pendingDecision is ChooseTargetsDecision) shouldBe true
        d.submitMultiTargetSelection(p1, mapOf(0 to listOf(theirs), 1 to listOf(alsoTheirs)))
            .error shouldBe null

        var guard = 0
        while (d.stackSize > 0 && guard < 10) {
            d.bothPass()
            guard++
        }

        withClue("copy + original each deal 1 to both new targets — the 2/1 dies, mine are untouched") {
            d.findPermanent(p2, "Savannah Lions") shouldBe null
            d.findPermanent(p2, "Centaur Courser") shouldBe alsoTheirs
            d.findPermanent(p1, "Savannah Lions") shouldBe mine
            d.findPermanent(p1, "Centaur Courser") shouldBe alsoMine
        }
    }
})
