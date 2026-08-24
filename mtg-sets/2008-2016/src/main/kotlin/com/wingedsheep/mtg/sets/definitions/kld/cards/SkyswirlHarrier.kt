package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Skyswirl Harrier
 * {4}{W}
 * Creature — Bird
 * 3/4
 *
 * Flying
 *
 * A printed keyword and nothing else.
 */
val SkyswirlHarrier = card("Skyswirl Harrier") {
    manaCost = "{4}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Bird"
    oracleText = "Flying"
    power = 3
    toughness = 4

    keywords(Keyword.FLYING)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "28"
        artist = "John Severin Brassell"
        flavorText = "The great birds dive through the sky, wings skimming the aether streams, to fall upon their unsuspecting prey."
        imageUri = "https://cards.scryfall.io/normal/front/b/9/b951bc89-be0b-4330-8a13-e196e084d53c.jpg?1783937228"
    }
}
