package com.wingedsheep.mtg.sets.definitions.arn.cards

import com.wingedsheep.sdk.dsl.basicLand

/**
 * Arabian Nights Basic Lands
 *
 * Arabian Nights is the one expansion that printed a *single* basic land: Mountain (card 77),
 * the desert scene Douglas Shuler painted for the set. There is no Plains, Island, Swamp or
 * Forest in ARN, which is why [com.wingedsheep.mtg.sets.definitions.arn.ArabianNightsSet] keeps
 * its `basicLandsFallback` — a limited deck built from an ARN pool still needs the other four
 * types, and they come from Portal.
 */

// =============================================================================
// Mountain (Card 77)
// =============================================================================

val ArnMountain77 = basicLand("Mountain") {
    collectorNumber = "77"
    artist = "Douglas Shuler"
    imageUri = "https://cards.scryfall.io/normal/front/c/3/c321d0e1-ff30-4424-979b-25e1a33e45d5.jpg?1783948374"
}

val ArabianNightsBasicLands = listOf(ArnMountain77)
