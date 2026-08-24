package com.wingedsheep.mtg.sets.definitions.grn.cards

import com.wingedsheep.sdk.dsl.basicLand

/**
 * Guilds of Ravnica Basic Lands
 *
 * One art per basic land type, cards 260-264, all by Richard Wright.
 */

val GuildsOfRavnicaPlains260 = basicLand("Plains") {
    collectorNumber = "260"
    artist = "Richard Wright"
    imageUri = "https://cards.scryfall.io/normal/front/b/9/b983acda-68b6-468b-b0c1-aad8b53db49c.jpg?1783934098"
}

val GuildsOfRavnicaIsland261 = basicLand("Island") {
    collectorNumber = "261"
    artist = "Richard Wright"
    imageUri = "https://cards.scryfall.io/normal/front/2/9/29bfbf3e-3a6c-40d4-8e1b-255f429de6cc.jpg?1783934098"
}

val GuildsOfRavnicaSwamp262 = basicLand("Swamp") {
    collectorNumber = "262"
    artist = "Richard Wright"
    imageUri = "https://cards.scryfall.io/normal/front/b/d/bdd5a7f0-5ad3-44e8-a103-07739fd53630.jpg?1783934097"
}

val GuildsOfRavnicaMountain263 = basicLand("Mountain") {
    collectorNumber = "263"
    artist = "Richard Wright"
    imageUri = "https://cards.scryfall.io/normal/front/7/f/7f918a49-a046-4115-80b8-13490ed5cd0a.jpg?1783934096"
}

val GuildsOfRavnicaForest264 = basicLand("Forest") {
    collectorNumber = "264"
    artist = "Richard Wright"
    imageUri = "https://cards.scryfall.io/normal/front/d/8/d85892ae-dacd-4a55-a557-9db3c16017c7.jpg?1783934097"
}

/**
 * All Guilds of Ravnica basic land variants.
 */
val GuildsOfRavnicaBasicLands = listOf(
    GuildsOfRavnicaPlains260,
    GuildsOfRavnicaIsland261,
    GuildsOfRavnicaSwamp262,
    GuildsOfRavnicaMountain263,
    GuildsOfRavnicaForest264
)
