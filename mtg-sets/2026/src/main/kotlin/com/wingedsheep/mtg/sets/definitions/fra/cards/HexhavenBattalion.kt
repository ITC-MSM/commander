package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

val HexhavenBattalion = card("Hexhaven Battalion") {
    manaCost = "{4}{W}{W}"
    colorIdentity = "W"
    typeLine = "Sorcery"
    oracleText = "Create three 2/2 colorless Wizard Soldier creature tokens named Cadet. Empower Jace 2. (Put two loyalty counters on a Jace token you control. If you don't control one, first create a blue Jace planeswalker token with \"[−1]: Surveil 1\" and \"[−3]: Draw a card.\")\nBasic landcycling {2} ({2}, Discard this card: Search your library for a basic land card, reveal it, put it into your hand, then shuffle.)"

    spell {
        effect = Effects.CreateToken(
            power = 2,
            toughness = 2,
            count = 3,
            name = "Cadet",
            creatureTypes = setOf("Wizard", "Soldier"),
            imageUri = "https://cards.scryfall.io/normal/front/8/f/8f4534d8-2783-484f-8ebf-a47b1cc4c6df.jpg?1789734318"
        ).then(Patterns.Mechanic.empowerJace(2))
    }

    keywordAbility(KeywordAbility.basicLandcycling(ManaCost.parse("{2}")))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "12"
        artist = "Julia Griffin"
        imageUri = "https://cards.scryfall.io/normal/front/3/b/3b6ac80e-c726-4bd0-893a-e666041a04a6.jpg?1789556695"
        inBooster = false
    }
}
