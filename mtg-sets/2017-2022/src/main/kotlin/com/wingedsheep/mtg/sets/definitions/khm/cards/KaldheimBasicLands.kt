package com.wingedsheep.mtg.sets.definitions.khm.cards

import com.wingedsheep.sdk.dsl.basicLand

/**
 * Kaldheim Basic Lands
 *
 * Kaldheim's five nonsnow basics, cards 394-398. The set's *snow* basics are a different card
 * (they carry the Snow supertype and a different name), so they are reprint rows against Ice Age's
 * canonicals rather than [basicLand] vals — see `SnowCovered*Reprint.kt` in this package.
 */

val KaldheimPlains394 = basicLand("Plains") {
    collectorNumber = "394"
    artist = "Piotr Dura"
    imageUri = "https://cards.scryfall.io/normal/front/5/c/5cbfbafa-f58f-40b2-a374-68ac35b77d89.jpg"
}

val KaldheimIsland395 = basicLand("Island") {
    collectorNumber = "395"
    artist = "Johannes Voss"
    imageUri = "https://cards.scryfall.io/normal/front/1/a/1a25a714-c7f3-4697-8b69-8f966b4d370a.jpg"
}

val KaldheimSwamp396 = basicLand("Swamp") {
    collectorNumber = "396"
    artist = "Piotr Dura"
    imageUri = "https://cards.scryfall.io/normal/front/9/f/9f9e61c0-b185-4704-913f-9284ed0ce250.jpg"
}

val KaldheimMountain397 = basicLand("Mountain") {
    collectorNumber = "397"
    artist = "Sam Burley"
    imageUri = "https://cards.scryfall.io/normal/front/6/9/69419307-53d5-40d7-82da-cab2e7bfbda4.jpg"
}

val KaldheimForest398 = basicLand("Forest") {
    collectorNumber = "398"
    artist = "Sam Burley"
    imageUri = "https://cards.scryfall.io/normal/front/7/7/771e307c-b2e3-47ac-aac2-59f0c3542fa6.jpg"
}

/**
 * All Kaldheim basic land variants.
 */
val KaldheimBasicLands = listOf(
    KaldheimPlains394,
    KaldheimIsland395,
    KaldheimSwamp396,
    KaldheimMountain397,
    KaldheimForest398
)
