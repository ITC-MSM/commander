package com.wingedsheep.mtg.sets.definitions.snc.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Angelic Observer
 * {5}{W}
 * Creature — Angel Advisor
 * 3 / 3
 * Affinity for Citizens (This spell costs {1} less to cast for each Citizen you control.)
 * Flying
 *
 * Affinity must be the [KeywordAbility.AffinityForSubtype] data class, not `Keyword.AFFINITY`:
 * the cost reduction is read off the keyword *ability*, so the bare enum would print the text
 * and reduce nothing. It derives `Keyword.AFFINITY` into the keyword set on its own.
 */
val AngelicObserver = card("Angelic Observer") {
    manaCost = "{5}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Angel Advisor"
    oracleText = "Affinity for Citizens (This spell costs {1} less to cast for each Citizen you control.)\nFlying"
    power = 3
    toughness = 3

    keywordAbility(KeywordAbility.AffinityForSubtype(Subtype.CITIZEN))
    keywords(Keyword.FLYING)

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "1"
        artist = "Zack Stella"
        flavorText = "Still and solemn as the statues of her kind that decorated the Park Heights rooftops, she gazed in sorrow on the city below."
        imageUri = "https://cards.scryfall.io/normal/front/e/6/e6cce4d3-e6d8-4c6f-9d9c-c0a8a607a42f.jpg?1783923164"
    }
}
