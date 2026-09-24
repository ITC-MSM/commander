package com.wingedsheep.engine.handlers.mana

import com.wingedsheep.engine.mechanics.mana.ManaPool
import com.wingedsheep.engine.mechanics.mana.SpellPaymentContext
import com.wingedsheep.sdk.core.CardType
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.scripting.effects.ManaRestriction
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * [ManaRestriction.CannotCastSpellsFromHand] — "This mana can't be spent to cast spells from your
 * hand" (Heartwood Crafter). A *negative* restriction: it blocks a spell cast from hand and nothing
 * else, unlike [ManaRestriction.CastFromNonHandOnly], which also rejects ability activations.
 */
class ManaSpendRestrictionCannotCastSpellsFromHandTest : FunSpec({

    val cost = ManaCost.parse("{1}")
    val pool = ManaPool().addRestricted(null, 1, ManaRestriction.CannotCastSpellsFromHand)
    val sorcery = setOf(CardType.SORCERY)

    test("rejects a spell cast from hand") {
        pool.canPay(cost, SpellPaymentContext(cardTypes = sorcery, isFromHand = true)) shouldBe false
    }

    test("accepts a spell cast from anywhere else (exile, graveyard, a prepare-spell copy)") {
        pool.canPay(cost, SpellPaymentContext(cardTypes = sorcery, isFromHand = false, isFromExile = true)) shouldBe true
        pool.canPay(cost, SpellPaymentContext(cardTypes = sorcery, isFromHand = false)) shouldBe true
    }

    test("accepts an ability activation") {
        pool.canPay(cost, SpellPaymentContext(isAbilityActivation = true)) shouldBe true
    }

    test("accepts a non-cast payment such as a tax or ward cost") {
        pool.canPay(cost, SpellPaymentContext()) shouldBe true
    }
})
