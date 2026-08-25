package com.wingedsheep.mtg.sets.definitions.iko.cards

import com.wingedsheep.sdk.dsl.basicLand

/**
 * Ikoria: Lair of Behemoths Basic Lands
 *
 * Ikoria contains 3 art variants of each basic land type.
 * Cards 260-274 (Plains 260-262, Island 263-265, Swamp 266-268, Mountain 269-271, Forest 272-274)
 */


// =============================================================================
// Plains (Cards 260-262)
// =============================================================================

val Plains260 = basicLand("Plains") {
    collectorNumber = "260"
    artist = "Cliff Childs"
    imageUri = "https://cards.scryfall.io/normal/front/1/6/16ebbce9-fd10-4c14-b52d-cf82c0c1a58c.jpg"
}

val Plains261 = basicLand("Plains") {
    collectorNumber = "261"
    artist = "Alayna Danner"
    imageUri = "https://cards.scryfall.io/normal/front/e/8/e84f2b9b-1412-476f-8739-17d35ea48a51.jpg"
}

val Plains262 = basicLand("Plains") {
    collectorNumber = "262"
    artist = "Jesper Ejsing"
    imageUri = "https://cards.scryfall.io/normal/front/8/a/8a65b379-47a9-48f2-be6e-abb4e7868ad0.jpg"
}


// =============================================================================
// Island (Cards 263-265)
// =============================================================================

val Island263 = basicLand("Island") {
    collectorNumber = "263"
    artist = "Alayna Danner"
    imageUri = "https://cards.scryfall.io/normal/front/4/b/4b2ad5b3-7257-4521-8916-6b1cbfb89e27.jpg"
}

val Island264 = basicLand("Island") {
    collectorNumber = "264"
    artist = "Jesper Ejsing"
    imageUri = "https://cards.scryfall.io/normal/front/0/b/0b9bbc32-a89a-4bed-ab52-ac6569ec74ce.jpg"
}

val Island265 = basicLand("Island") {
    collectorNumber = "265"
    artist = "Nick Southam"
    imageUri = "https://cards.scryfall.io/normal/front/b/e/be169f8c-6472-40ec-9b4a-0edcb63e9e2f.jpg"
}


// =============================================================================
// Swamp (Cards 266-268)
// =============================================================================

val Swamp266 = basicLand("Swamp") {
    collectorNumber = "266"
    artist = "Alayna Danner"
    imageUri = "https://cards.scryfall.io/normal/front/6/c/6c8c3f0e-7af4-410b-a675-9ea84f51e812.jpg"
}

val Swamp267 = basicLand("Swamp") {
    collectorNumber = "267"
    artist = "Jesper Ejsing"
    imageUri = "https://cards.scryfall.io/normal/front/e/1/e1d99025-46bc-4848-bcbc-bee858ed906c.jpg"
}

val Swamp268 = basicLand("Swamp") {
    collectorNumber = "268"
    artist = "Svetlin Velinov"
    imageUri = "https://cards.scryfall.io/normal/front/4/5/45991831-1018-4f98-a4ad-0998a9577d97.jpg"
}


// =============================================================================
// Mountain (Cards 269-271)
// =============================================================================

val Mountain269 = basicLand("Mountain") {
    collectorNumber = "269"
    artist = "Alayna Danner"
    imageUri = "https://cards.scryfall.io/normal/front/a/e/ae3d2fcd-11e0-4071-8c53-cb3315b7360a.jpg"
}

val Mountain270 = basicLand("Mountain") {
    collectorNumber = "270"
    artist = "Jesper Ejsing"
    imageUri = "https://cards.scryfall.io/normal/front/e/b/eb1ba09d-ecdd-48c0-af0b-3dc5ef908f9e.jpg"
}

val Mountain271 = basicLand("Mountain") {
    collectorNumber = "271"
    artist = "Adam Paquette"
    imageUri = "https://cards.scryfall.io/normal/front/e/6/e609028b-43b8-4aea-9f9a-25aa127482db.jpg"
}


// =============================================================================
// Forest (Cards 272-274)
// =============================================================================

val Forest272 = basicLand("Forest") {
    collectorNumber = "272"
    artist = "Alayna Danner"
    imageUri = "https://cards.scryfall.io/normal/front/9/c/9c348494-f60c-4bd1-9077-bff24f2e634b.jpg"
}

val Forest273 = basicLand("Forest") {
    collectorNumber = "273"
    artist = "Jesper Ejsing"
    imageUri = "https://cards.scryfall.io/normal/front/e/b/eb61e54a-646b-4c0a-88fe-f55d514202aa.jpg"
}

val Forest274 = basicLand("Forest") {
    collectorNumber = "274"
    artist = "Yeong-Hao Han"
    imageUri = "https://cards.scryfall.io/normal/front/f/7/f7cf3bbf-acc0-4fe7-a631-aca698190ce2.jpg"
}

/**
 * All Ikoria basic land variants.
 */
val IkoriaBasicLands = listOf(
    Plains260, Plains261, Plains262,
    Island263, Island264, Island265,
    Swamp266, Swamp267, Swamp268,
    Mountain269, Mountain270, Mountain271,
    Forest272, Forest273, Forest274,
)
