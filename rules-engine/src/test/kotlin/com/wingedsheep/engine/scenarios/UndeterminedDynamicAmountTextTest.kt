package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.engine.view.ClientStateTransformer
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain

/**
 * Regression for World War Hulk's chapter III rendering as "target gets +0/+0 until end of turn".
 *
 * An ability that pumps a creature by *its own* stats ("double its power and toughness") reads
 * [com.wingedsheep.sdk.scripting.values.EntityReference.Target], so its amount is only knowable once
 * a target exists. Two surfaces render such an ability, and they sat on opposite sides of one bug:
 *
 *  1. **The targeting banner**, drawn *before* the player chooses. No target exists by construction,
 *     so `DynamicAmountEvaluator` couldn't resolve the reference and answered `0` — indistinguishable
 *     from a real zero, and rendered as a concrete "+0/+0". `evaluateForDisplay` now reports `null`
 *     there and the renderer falls back to the amount's own wording.
 *  2. **The ability on the stack**, where the target *is* locked in and the number is genuinely
 *     known. `ClientStateTransformer` never passed the ability's chosen targets into the context it
 *     rendered with — the spell path did — so this also read "+0/+0" when it should have read
 *     "+5/+5".
 *
 * The final assertion pins the text to reality: whatever the stack claimed is what the creature
 * actually becomes.
 */
class UndeterminedDynamicAmountTextTest : FunSpec({

    // The shape of World War Hulk's chapter III, reduced to the mechanic under test: a *targeted
    // triggered* ability whose pump is read off the target itself.
    val Doubler = card("Display Doubler") {
        manaCost = "{0}"
        typeLine = "Creature — Giant"
        power = 1
        toughness = 1
        triggeredAbility {
            trigger = Triggers.EntersBattlefield
            val tgt = target("target creature you control", Targets.CreatureYouControl)
            effect = Effects.ModifyStats(DynamicAmounts.targetPower(), DynamicAmounts.targetToughness(), tgt)
        }
    }

    // A locally-defined body so the doubling math is unambiguous — several TestCards entries are
    // 1/1 stubs, which would make 5/5 -> 10/10 indistinguishable from a no-op.
    val Bruiser = card("Display Bruiser") {
        manaCost = "{0}"
        typeLine = "Creature — Ogre"
        power = 5
        toughness = 5
    }

    test("a pump read off the target renders by name before targeting and by value on the stack") {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + listOf(Doubler, Bruiser))
        d.initMirrorMatch(deck = Deck.of("Forest" to 40))
        val active = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val bruiser = d.putCreatureOnBattlefield(active, "Display Bruiser")

        // Casting the Doubler fires its ETB trigger, which stops to ask for a target.
        val doubler = d.putCardInHand(active, "Display Doubler")
        d.castSpell(active, doubler)
        d.bothPass()

        // --- Surface 1: the targeting banner, drawn before a target exists -------------------
        val decision = d.state.pendingDecision
        decision.shouldNotBeNull()
        val hint = decision.context.effectHint
        hint.shouldNotBeNull()
        withClue("the banner must not invent a concrete +0/+0 for an amount it cannot know yet") {
            hint shouldNotContain "+0/+0"
        }
        withClue("it should name the amount instead — banner hint was: '$hint'") {
            hint shouldContain "power"
        }

        // --- Surface 2: the ability on the stack, with its target locked in ------------------
        d.submitTargetSelection(active, listOf(bruiser))

        val stackId = d.state.stack.first()
        val stackCard = ClientStateTransformer(cardRegistry = d.cardRegistry)
            .transform(d.state, viewingPlayerId = active)
            .cards[stackId]
        stackCard.shouldNotBeNull()
        withClue("the target is chosen now, so the real number is knowable: ${stackCard.oracleText}") {
            stackCard.oracleText shouldContain "+5/+5"
            stackCard.oracleText shouldNotContain "+0/+0"
        }

        // --- The text told the truth ---------------------------------------------------------
        d.bothPass()
        d.state.projectedState.getPower(bruiser) shouldBe 10
        d.state.projectedState.getToughness(bruiser) shouldBe 10
    }

    test("an amount that genuinely resolves to zero still renders as a number, not as its name") {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + listOf(Doubler, Bruiser))
        d.initMirrorMatch(deck = Deck.of("Forest" to 40))
        val active = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // A 0-power body: "double its power" is a real +0 here, and must not be mistaken for the
        // undeterminable case — that distinction is the whole point of the nullable resolver.
        val wall = card("Display Wall") {
            manaCost = "{0}"
            typeLine = "Creature — Wall"
            power = 0
            toughness = 4
        }
        d.registerCards(listOf(wall))
        val zeroPower = d.putCreatureOnBattlefield(active, "Display Wall")

        val doubler = d.putCardInHand(active, "Display Doubler")
        d.castSpell(active, doubler)
        d.bothPass()
        d.submitTargetSelection(active, listOf(zeroPower))

        val stackId = d.state.stack.first()
        val stackCard = ClientStateTransformer(cardRegistry = d.cardRegistry)
            .transform(d.state, viewingPlayerId = active)
            .cards[stackId]
        stackCard.shouldNotBeNull()
        withClue("a resolved zero is a value, so it prints: ${stackCard.oracleText}") {
            stackCard.oracleText shouldContain "+0/+4"
        }
    }
})
