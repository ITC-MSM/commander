package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Exiled Boggart
 * {1}{B}
 * Creature — Goblin Rogue
 * 2/2
 * When this creature dies, discard a card.
 */
val ExiledBoggart = card("Exiled Boggart") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Goblin Rogue"
    power = 2
    toughness = 2
    oracleText = "When this creature dies, discard a card."

    triggeredAbility {
        trigger = Triggers.Dies
        effect = Effects.Discard(1)
        description = "When this creature dies, discard a card."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "109"
        artist = "Pete Venters"
        flavorText = "Among the boggarts, there is only one real rule: all new treasures and experiences must be shared. Those who hoard their gifts commit the one truly unforgivable sin."
        imageUri = "https://cards.scryfall.io/normal/front/5/f/5fe41509-02db-415c-964e-971ea1d5485c.jpg?1783942891"
    }
}
