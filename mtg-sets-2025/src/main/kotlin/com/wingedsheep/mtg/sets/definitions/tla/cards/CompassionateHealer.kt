package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Compassionate Healer
 * {1}{W}
 * Creature — Human Cleric Ally
 * 2/2
 * Whenever this creature becomes tapped, you gain 1 life and scry 1. (Look at the top
 * card of your library. You may put it on the bottom.)
 */
val CompassionateHealer = card("Compassionate Healer") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Cleric Ally"
    power = 2
    toughness = 2
    oracleText = "Whenever this creature becomes tapped, you gain 1 life and scry 1. " +
        "(Look at the top card of your library. You may put it on the bottom.)"

    triggeredAbility {
        trigger = Triggers.BecomesTapped
        effect = Effects.Composite(
            Effects.GainLife(1),
            Effects.Scry(1),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "13"
        artist = "Kuno"
        flavorText = "Waterbending does not only heal physical wounds. It can also soothe the spirit and calm the mind."
        imageUri = "https://cards.scryfall.io/normal/front/8/8/88d5f8fd-d4de-4e64-9b74-e53719ffbcdc.jpg?1764119957"
    }
}
