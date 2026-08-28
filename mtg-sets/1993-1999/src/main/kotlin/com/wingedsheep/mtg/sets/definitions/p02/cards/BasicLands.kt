package com.wingedsheep.mtg.sets.definitions.p02.cards

import com.wingedsheep.sdk.dsl.basicLand

/**
 * Portal Second Age Basic Lands
 *
 * Portal Second Age contains 3 art variants of each basic land type.
 * Cards 151-165 (Plains 151-153, Island 154-156, Swamp 157-159, Mountain 160-162, Forest 163-165)
 */

// =============================================================================
// Plains (Cards 151-153)
// =============================================================================

val Plains151 = basicLand("Plains") {
    collectorNumber = "151"
    artist = "Fred Fields"
    imageUri = "https://cards.scryfall.io/normal/front/2/7/27ecf285-c48f-4ac3-9b75-0ee0ff052767.jpg"
}

val Plains152 = basicLand("Plains") {
    collectorNumber = "152"
    artist = "Fred Fields"
    imageUri = "https://cards.scryfall.io/normal/front/8/a/8a136975-e513-4c50-9d4a-855d03630470.jpg"
}

val Plains153 = basicLand("Plains") {
    collectorNumber = "153"
    artist = "Fred Fields"
    imageUri = "https://cards.scryfall.io/normal/front/f/6/f67f2ecd-d5fb-406b-b14d-34eb087eb08b.jpg"
}

// =============================================================================
// Island (Cards 154-156)
// =============================================================================

val Island154 = basicLand("Island") {
    collectorNumber = "154"
    artist = "John Avon"
    imageUri = "https://cards.scryfall.io/normal/front/b/0/b0a83b1a-a734-4726-9d14-c75ef04798d1.jpg"
}

val Island155 = basicLand("Island") {
    collectorNumber = "155"
    artist = "John Avon"
    imageUri = "https://cards.scryfall.io/normal/front/b/1/b165ada0-7f52-4047-8596-c54247d13704.jpg"
}

val Island156 = basicLand("Island") {
    collectorNumber = "156"
    artist = "John Avon"
    imageUri = "https://cards.scryfall.io/normal/front/3/c/3cf8ecec-8925-470b-994d-79694d056834.jpg"
}

// =============================================================================
// Swamp (Cards 157-159)
// =============================================================================

val Swamp157 = basicLand("Swamp") {
    collectorNumber = "157"
    artist = "Susan Van Camp"
    imageUri = "https://cards.scryfall.io/normal/front/8/0/80c0bd18-fdee-4cb2-8a7e-dd86bb8a6bbe.jpg"
}

val Swamp158 = basicLand("Swamp") {
    collectorNumber = "158"
    artist = "Susan Van Camp"
    imageUri = "https://cards.scryfall.io/normal/front/a/d/adb62dd9-266b-4de9-8a11-3228b5f1e03a.jpg"
}

val Swamp159 = basicLand("Swamp") {
    collectorNumber = "159"
    artist = "Susan Van Camp"
    imageUri = "https://cards.scryfall.io/normal/front/c/1/c103ed69-b73b-4789-a28d-1fd071bc9029.jpg"
}

// =============================================================================
// Mountain (Cards 160-162)
// =============================================================================

val Mountain160 = basicLand("Mountain") {
    collectorNumber = "160"
    artist = "Rob Alexander"
    imageUri = "https://cards.scryfall.io/normal/front/b/d/bdfbb15a-d7f4-470a-9d36-78c710a6d397.jpg"
}

val Mountain161 = basicLand("Mountain") {
    collectorNumber = "161"
    artist = "Rob Alexander"
    imageUri = "https://cards.scryfall.io/normal/front/4/4/4453a5c0-76f2-422d-8cbc-4b21c17cdf1e.jpg"
}

val Mountain162 = basicLand("Mountain") {
    collectorNumber = "162"
    artist = "Rob Alexander"
    imageUri = "https://cards.scryfall.io/normal/front/9/0/9006aa97-884f-404a-9ead-af8437ea9596.jpg"
}

// =============================================================================
// Forest (Cards 163-165)
// =============================================================================

val Forest163 = basicLand("Forest") {
    collectorNumber = "163"
    artist = "Quinton Hoover"
    imageUri = "https://cards.scryfall.io/normal/front/8/9/89f4fcbb-86a8-475f-a547-d59014bb7a98.jpg"
}

val Forest164 = basicLand("Forest") {
    collectorNumber = "164"
    artist = "Quinton Hoover"
    imageUri = "https://cards.scryfall.io/normal/front/a/a/aa863815-1dcc-43d8-8076-a35037688b20.jpg"
}

val Forest165 = basicLand("Forest") {
    collectorNumber = "165"
    artist = "Quinton Hoover"
    imageUri = "https://cards.scryfall.io/normal/front/a/b/abebee47-5205-4855-ae72-475b58a02240.jpg"
}

/**
 * All Portal Second Age basic land variants.
 */
val PortalSecondAgeBasicLands = listOf(
    Plains151, Plains152, Plains153,
    Island154, Island155, Island156,
    Swamp157, Swamp158, Swamp159,
    Mountain160, Mountain161, Mountain162,
    Forest163, Forest164, Forest165
)
