package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Matoya, Archon Elder
 * {2}{U}
 * Legendary Creature — Human Warlock
 * 1/4
 * Whenever you scry or surveil, draw a card. (Draw after you scry or surveil.)
 *
 * Uses the unified [Triggers.WheneverYouScryOrSurveil] trigger so a single scry or surveil
 * event fires the ability once (the engine collapses simultaneous scry+surveil into one event).
 */
val MatoyaArchonElder = card("Matoya, Archon Elder") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Legendary Creature — Human Warlock"
    power = 1
    toughness = 4
    oracleText = "Whenever you scry or surveil, draw a card. (Draw after you scry or surveil.)"

    triggeredAbility {
        trigger = Triggers.WheneverYouScryOrSurveil
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "62"
        artist = "Luisa J. Preissler"
        flavorText = "\"Don't you know it's rude to enter without knocking? Hmph, the youth of today...\""
        imageUri = "https://cards.scryfall.io/normal/front/1/d/1dd61cf6-2fb5-4cff-ab00-7677ac85774c.jpg?1748705987"
    }
}
