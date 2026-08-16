package com.wingedsheep.mtg.sets.definitions.bro.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Tomakul Honor Guard
 * {1}{G}
 * Creature — Human Soldier
 * 3/1
 * Ward {2} (Whenever this creature becomes the target of a spell or ability an opponent controls, counter it unless that player pays {2}.)
 */
val TomakulHonorGuard = card("Tomakul Honor Guard") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Human Soldier"
    power = 3
    toughness = 1
    oracleText = "Ward {2} (Whenever this creature becomes the target of a spell or ability an opponent controls, counter it unless that player pays {2}.)"

    // Ward {2} (CR 702.21a). The bare `Keyword.WARD` marker is derived from this ability by the
    // builder, so it is not restated here.
    keywordAbility(KeywordAbility.ward("{2}"))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "195"
        artist = "Francisco Miyara"
        flavorText = "In the Great Desert, one law is enforced over all others: water belongs to all."
        imageUri = "https://cards.scryfall.io/normal/front/c/7/c7745439-f40b-4647-8bff-53751d511bbd.jpg?1783920037"
    }
}
