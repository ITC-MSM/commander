package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Konstrari Improviser // Soul Tether — enters prepared; its prepare spell creates a Heartwood
 * token (the predefined red and green artifact with "{T}: Add {R} or {G}.").
 */
val KonstrariImproviser = card("Konstrari Improviser") {
    manaCost = "{1}{R/G}"
    colorIdentity = "RG"
    typeLine = "Creature — Human Artificer"
    power = 2
    toughness = 2
    oracleText = "This creature enters prepared. (While it's prepared, you may cast a copy of its spell. Doing so unprepares it.)"

    keywords(Keyword.PREPARED)

    prepare("Soul Tether") {
        manaCost = "{2}{R/G}"
        typeLine = "Sorcery"
        oracleText = "Create a Heartwood token. (It's a red and green artifact with \"{T}: Add {R} or {G}.\")"
        spell {
            effect = Effects.CreateHeartwood()
        }
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "139"
        artist = "Matheus Graef"
        imageUri = "https://cards.scryfall.io/normal/front/4/2/42e28bd2-486b-45d4-8840-6e33c19c2d57.jpg?1789556881"
        inBooster = false
    }
}
