package com.wingedsheep.mtg.sets.definitions.som.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Necrogen Scudder
 * {2}{B}
 * Creature — Phyrexian Horror
 * 3/3
 * Flying
 * When this creature enters, you lose 3 life.
 *
 * Canonical printing: Scars of Mirrodin, the card's earliest real-expansion printing. Reprinted in
 * M15 as a `Printing` row.
 */
val NecrogenScudder = card("Necrogen Scudder") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Phyrexian Horror"
    power = 3
    toughness = 3
    oracleText =
        "Flying\n" +
        "When this creature enters, you lose 3 life."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.LoseLife(3, EffectTarget.Controller)
        description = "When this creature enters, you lose 3 life."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "71"
        artist = "Raymond Swanland"
        flavorText = "Contrary to popular belief, it's kept aloft by necrogen gas, not the screaming agony of a thousand murdered souls."
        imageUri = "https://cards.scryfall.io/normal/front/7/d/7d69c045-d705-478b-9e8f-272a24737225.jpg?1783941729"
    }
}
