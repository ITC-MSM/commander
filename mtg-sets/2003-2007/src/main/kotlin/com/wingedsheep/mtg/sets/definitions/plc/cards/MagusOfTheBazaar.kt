package com.wingedsheep.mtg.sets.definitions.plc.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Magus of the Bazaar
 * {1}{U}
 * Creature — Human Wizard
 * 0/1
 * {T}: Draw two cards, then discard three cards.
 *
 * The Magus of Bazaar of Baghdad. The discard is a full Gather → Select → Move pipeline rather
 * than a count, because the player chooses which three leave.
 */
val MagusOfTheBazaar = card("Magus of the Bazaar") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Wizard"
    power = 0
    toughness = 1
    oracleText = "{T}: Draw two cards, then discard three cards."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.DrawCards(2) then Patterns.Hand.discardCards(3)
        description = "{T}: Draw two cards, then discard three cards."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "43"
        artist = "Rob Alexander"
        flavorText = "\"Some trade in goods, some in secrets. My soul has walked the futures, and I offer the rare coin of possibility.\""
        imageUri = "https://cards.scryfall.io/normal/front/a/8/a89762f8-70e0-414b-a788-532710129733.jpg"
    }
}
