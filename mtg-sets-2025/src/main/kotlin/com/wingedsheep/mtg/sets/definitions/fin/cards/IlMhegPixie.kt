package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity


/**
 * Il Mheg Pixie
 * {1}{U}
 * Creature — Faerie
 * 2/1
 * Flying
 * Whenever this creature attacks, surveil 1. (Look at the top card of your library. You may put it into your graveyard.)
 */
val IlMhegPixie = card("Il Mheg Pixie") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Faerie"
    oracleText = "Flying\nWhenever this creature attacks, surveil 1. (Look at the top card of your library. You may put it into your graveyard.)"
    power = 2
    toughness = 1
    keywords(Keyword.FLYING)
    triggeredAbility {
        trigger = Triggers.Attacks
        effect = Patterns.Library.surveil(1)
    }
    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "57"
        artist = "Ramza Psyru"
        flavorText = "\"Well now, aren't you just brimming with life? I'd love to suck up all that aether like nectar from a flower!\""
        imageUri = "https://cards.scryfall.io/normal/front/a/e/ae612312-3a8e-495f-8730-deaaf7505ca1.jpg"
    }
}
