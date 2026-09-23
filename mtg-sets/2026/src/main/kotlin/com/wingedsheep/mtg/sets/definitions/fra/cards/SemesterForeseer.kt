package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

val SemesterForeseer = card("Semester Foreseer") {
    manaCost = "{3}{U}"
    colorIdentity = "UW"
    typeLine = "Creature — Human Wizard"
    power = 3
    toughness = 4
    oracleText = "This creature enters prepared. (While it's prepared, you may cast a copy of its spell. Doing so unprepares it.)\nWhen this creature enters, surveil 1."

    keywords(Keyword.PREPARED)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Library.surveil(1)
        description = "When this creature enters, surveil 1."
    }

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
        collectorNumber = "39"
        artist = "Alix Branwyn"
        imageUri = "https://cards.scryfall.io/normal/front/7/e/7e5a640b-fd88-4b5a-9dfc-1d8f5a5cef41.jpg?1789127516"
        inBooster = false
    }
}
