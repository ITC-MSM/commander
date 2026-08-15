package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.OptionalCostEffect
import com.wingedsheep.sdk.scripting.effects.PayManaCostEffect

/**
 * Descendant of Storms — Tarkir: Dragonstorm #8
 * {W} · Creature — Human Soldier · 2/1
 *
 * Whenever this creature attacks, you may pay {1}{W}. If you do, it endures 1.
 * (Put a +1/+1 counter on it or create a 1/1 white Spirit creature token.)
 *
 * The "you may pay {1}{W}" gate is an [OptionalCostEffect] (same shape as
 * Zoraline, Cosmos Caller): if the mana is paid, the Endure 1 effect runs.
 */
val DescendantOfStorms = card("Descendant of Storms") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Soldier"
    power = 2
    toughness = 1
    oracleText = "Whenever this creature attacks, you may pay {1}{W}. If you do, it endures 1. " +
        "(Put a +1/+1 counter on it or create a 1/1 white Spirit creature token.)"

    triggeredAbility {
        trigger = Triggers.Attacks
        effect = OptionalCostEffect(
            cost = PayManaCostEffect(ManaCost.parse("{1}{W}")),
            ifPaid = Effects.Endure(1)
        )
        description = "Whenever this creature attacks, you may pay {1}{W}. If you do, it endures 1."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "8"
        artist = "Lie Setiawan"
        imageUri = "https://cards.scryfall.io/normal/front/f/6/f632be90-9e7f-41f8-a52e-a2952354d730.jpg?1743203984"
    }
}
