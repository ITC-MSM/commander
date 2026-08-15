package com.wingedsheep.mtg.sets.definitions.rix.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Kitesail Corsair
 * {1}{U}
 * Creature — Human Pirate
 * 2/1
 * This creature has flying as long as it's attacking.
 */
val KitesailCorsair = card("Kitesail Corsair") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Pirate"
    power = 2
    toughness = 1
    oracleText = "This creature has flying as long as it's attacking."

    staticAbility {
        ability = GrantKeyword(Keyword.FLYING, GroupFilter.source())
        condition = Conditions.SourceIsAttacking
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "41"
        artist = "Greg Opalinski"
        flavorText = "\"Why perch in the crow's nest when I can fly like the crows?\""
        imageUri = "https://cards.scryfall.io/normal/front/4/b/4b8d0e8d-c2d4-4682-8095-827ffd79539b.jpg?1782710294"
    }
}
