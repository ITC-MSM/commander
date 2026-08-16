package com.wingedsheep.mtg.sets.definitions.dmu.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Talas Lookout
 * {2}{U}{U}
 * Creature — Human Pirate
 * 3/2
 * Flying
 * When this creature dies, look at the top two cards of your library. Put one of them into your hand and the other into your graveyard.
 *
 * The kept card goes to hand and the remainder to the graveyard — the
 * `lookAtTopAndKeep` defaults, so only the two counts are spelled here.
 */
val TalasLookout = card("Talas Lookout") {
    manaCost = "{2}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Pirate"
    oracleText = "Flying\nWhen this creature dies, look at the top two cards of your library. Put one of them into your hand and the other into your graveyard."
    power = 3
    toughness = 2

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.Dies
        effect = Patterns.Library.lookAtTopAndKeep(count = 2, keepCount = 1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "68"
        artist = "Julia Metzger"
        flavorText = "\"The Talas pirates are motivated by money, so we paid them twice what they could steal from us to spy for us instead.\"\n—Jhoira"
        imageUri = "https://cards.scryfall.io/normal/front/0/a/0a4b9afe-aeb7-4c56-8a22-96e19a7a938c.jpg?1783921343"
    }
}
