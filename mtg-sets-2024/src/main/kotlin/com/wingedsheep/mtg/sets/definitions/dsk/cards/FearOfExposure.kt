package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Fear of Exposure
 * {2}{G}
 * Enchantment Creature — Nightmare
 * 5/4
 * As an additional cost to cast this spell, tap two untapped creatures and/or lands you control.
 * Trample
 *
 * The tap is modeled as an `AdditionalCost.TapPermanents` over untapped creatures-or-lands you
 * control (CR 601.2f — paid as the spell is cast), so the spell can't be cast without two
 * untapped creatures/lands to tap.
 */
val FearOfExposure = card("Fear of Exposure") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Enchantment Creature — Nightmare"
    oracleText = "As an additional cost to cast this spell, tap two untapped creatures and/or lands " +
        "you control.\nTrample"
    power = 5
    toughness = 4

    keywords(Keyword.TRAMPLE)

    additionalCost(
        Costs.additional.TapPermanents(
            count = 2,
            filter = GameObjectFilter.CreatureOrLand.untapped().youControl()
        )
    )

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "177"
        artist = "Josu Hernaiz"
        flavorText = "A mouth like that can never keep secrets."
        imageUri = "https://cards.scryfall.io/normal/front/9/3/93bb4abc-5af1-4cf5-919b-244b6a36f8ec.jpg?1726286520"
    }
}
