package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

val FateholdChronologist = card("Fatehold Chronologist") {
    manaCost = "{1}{W/U}"
    colorIdentity = "UW"
    typeLine = "Creature — Bird Wizard"
    power = 1
    toughness = 2
    oracleText = "Flying\nThis creature enters prepared. (While it's prepared, you may cast a copy of its spell. Doing so unprepares it.)"

    keywords(Keyword.FLYING)
    keywords(Keyword.PREPARED)

    prepare("Peer Review") {
        manaCost = "{2}{W/U}"
        typeLine = "Sorcery"
        oracleText = "Create a 2/2 colorless Wizard Soldier creature token named Cadet. Surveil 1."
        spell {
            effect = Effects.CreateToken(power = 2, toughness = 2, name = "Cadet", creatureTypes = setOf("Wizard", "Soldier"), imageUri = "https://cards.scryfall.io/normal/front/8/f/8f4534d8-2783-484f-8ebf-a47b1cc4c6df.jpg?1789734318")
                .then(Patterns.Library.surveil(1))
        }
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "133"
        artist = "Alexander Mokhov"
        imageUri = "https://cards.scryfall.io/normal/front/2/9/29e7ec16-0c16-48aa-8e09-ce6e0d5bd40b.jpg?1789060173"
        inBooster = false
    }
}
