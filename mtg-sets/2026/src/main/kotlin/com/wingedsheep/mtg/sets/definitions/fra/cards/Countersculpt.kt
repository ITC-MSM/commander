package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Countersculpt
 * {U}{U}
 * Instant
 *
 * As an additional cost to cast this spell, behold a Jace or pay {1}.
 * Counter target spell. Empower Jace 1.
 *
 * "Behold a Jace or pay {1}" is [Costs.additional.BeholdOrPay] (Lys Alana Dignitary's shape) —
 * the behold leg spans Jaces the caster controls (including a Jace token) and Jace cards in hand.
 * Empower Jace happens even if the spell couldn't be countered.
 */
val Countersculpt = card("Countersculpt") {
    manaCost = "{U}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "As an additional cost to cast this spell, behold a Jace or pay {1}. " +
        "(To behold a Jace, choose a Jace you control or reveal a Jace card from your hand.)\n" +
        "Counter target spell. Empower Jace 1. (Put a loyalty counter on a Jace token you control. " +
        "If you don't control one, first create a blue Jace planeswalker token with \"[−1]: Surveil 1\" " +
        "and \"[−3]: Draw a card.\")"

    additionalCost(
        Costs.additional.BeholdOrPay(
            filter = Filters.WithSubtype("Jace"),
            alternativeManaCost = "{1}"
        )
    )

    spell {
        target = Targets.Spell
        effect = Effects.Composite(
            Effects.CounterSpell(),
            Patterns.Mechanic.empowerJace(1),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "25"
        artist = "Anna Podedworna"
        imageUri = "https://cards.scryfall.io/normal/front/1/4/145b928d-a7ff-4fe5-ae4d-bbae7b1d955b.jpg?1788878107"
        inBooster = false
    }
}
