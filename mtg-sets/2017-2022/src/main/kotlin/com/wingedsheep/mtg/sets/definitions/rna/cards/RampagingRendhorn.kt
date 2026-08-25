package com.wingedsheep.mtg.sets.definitions.rna.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.riot
import com.wingedsheep.sdk.model.Rarity

/**
 * Rampaging Rendhorn — Ravnica Allegiance #135
 * {4}{G} · Creature — Beast · 4 / 4
 *
 * Riot on a vanilla body — see [ZhurTaaGoblin] for why the [riot] helper is load-bearing.
 */
val RampagingRendhorn = card("Rampaging Rendhorn") {
    manaCost = "{4}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Beast"
    power = 4
    toughness = 4
    oracleText = "Riot (This creature enters with your choice of a +1/+1 counter or haste.)"

    riot()

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "135"
        artist = "Ben Wootten"
        flavorText = "Tumult is its natural habitat."
        imageUri = "https://cards.scryfall.io/normal/front/1/2/12c1b820-0f06-41f6-804f-5c98f60c1529.jpg"
    }
}
