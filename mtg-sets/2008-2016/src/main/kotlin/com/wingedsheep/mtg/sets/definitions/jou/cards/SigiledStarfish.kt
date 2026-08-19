package com.wingedsheep.mtg.sets.definitions.jou.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Sigiled Starfish
 * {1}{U}
 * Creature — Starfish
 * 0/3
 *
 * {T}: Scry 1. (Look at the top card of your library. You may put that card on the bottom.)
 */
val SigiledStarfish = card("Sigiled Starfish") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Starfish"
    oracleText = "{T}: Scry 1. (Look at the top card of your library. You may put that card on the bottom.)"
    power = 0
    toughness = 3

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.Scry(1)
        description = "{T}: Scry 1."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "52"
        artist = "Nils Hamm"
        flavorText = "Kruphix hid the most dire prophecies about humankind where humans would never find them and tritons wouldn't care to read them."
        imageUri = "https://cards.scryfall.io/normal/front/8/5/85a61f99-44f8-4a2b-b7f2-7899a694552d.jpg?1783939442"
    }
}
