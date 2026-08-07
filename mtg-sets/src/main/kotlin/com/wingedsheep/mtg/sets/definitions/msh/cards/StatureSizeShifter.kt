package com.wingedsheep.mtg.sets.definitions.msh.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantBeBlocked
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.conditions.ComparisonOperator
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Stature, Size Shifter — Marvel Super Heroes #76 (uncommon)
 * {U} · Legendary Creature — Human Hero · 1/1
 *
 * Stature can't be blocked if her power is 1 or less.
 * Power-up — {X}{U}{U}: Put X +1/+1 counters on Stature. (Activate each power-up ability only
 * once. Reduce the cost by her mana cost if she entered this turn.)
 *
 * The card is a deliberate tension and both halves have to be modelled exactly for it to read
 * right: growing her with her own power-up is what *turns off* her evasion. So the "can't be
 * blocked" clause is a [ConditionalStaticAbility] re-asked every projection — it comes back if she
 * shrinks again — and never a one-shot grant.
 *
 * The only `{X}` power-up in the set. Cost reduction never touches `{X}` (CR 601.2f applies
 * reductions to the total cost after X is announced, and the reduction here has no generic
 * component anyway): `{X}{U}{U}` − `{U}` = `{X}{U}`, so the turn she lands every mana past the
 * first blue goes straight into counters.
 */
val StatureSizeShifter = card("Stature, Size Shifter") {
    manaCost = "{U}"
    colorIdentity = "U"
    typeLine = "Legendary Creature — Human Hero"
    oracleText = "Stature can't be blocked if her power is 1 or less.\n" +
        "Power-up — {X}{U}{U}: Put X +1/+1 counters on Stature. (Activate each power-up ability " +
        "only once. Reduce the cost by her mana cost if she entered this turn.)"
    power = 1
    toughness = 1

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = CantBeBlocked(),
            condition = Conditions.CompareAmounts(
                DynamicAmounts.sourcePower(),
                ComparisonOperator.LTE,
                DynamicAmount.Fixed(1)
            )
        )
    }

    activatedAbility {
        isPowerUp = true
        cost = Costs.Mana("{X}{U}{U}")
        effect = Effects.AddDynamicCounters(
            Counters.PLUS_ONE_PLUS_ONE,
            DynamicAmount.XValue,
            EffectTarget.Self
        )
        description = "Put X +1/+1 counters on Stature."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "76"
        artist = "Mintautas Šukys"
        flavorText = "\"Aw, cute! Fun-sized Dooms!\""
        imageUri = "https://cards.scryfall.io/normal/front/f/e/fe692959-64ca-4065-9f9e-1abe590e3d0f.jpg?1783902951"
    }
}
