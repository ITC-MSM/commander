package com.wingedsheep.mtg.sets.definitions.j22.cards

import com.wingedsheep.sdk.dsl.basicLand

/**
 * Jumpstart 2022 Basic Lands
 *
 * One art variant per basic land type. `CardDiscovery.findBasicLandsIn` picks these up from
 * the set object and stamps them with the J22 set code.
 */

val Jumpstart2022Plains98 = basicLand("Plains") {
    collectorNumber = "98"
    artist = "John Avon"
    imageUri = "https://cards.scryfall.io/normal/front/0/a/0a479838-b6b1-4d83-9104-0a79f3b934c7.jpg?1783919156"
}

val Jumpstart2022Plains99 = basicLand("Plains") {
    collectorNumber = "99"
    artist = "Alayna Danner"
    imageUri = "https://cards.scryfall.io/normal/front/4/6/464fa166-82be-4f28-8fd8-0c9cc4393012.jpg?1783919154"
}

val Jumpstart2022Plains100 = basicLand("Plains") {
    collectorNumber = "100"
    artist = "Daniel Ljunggren"
    imageUri = "https://cards.scryfall.io/normal/front/9/d/9d4b8bde-412b-4d12-b296-7364c95e0510.jpg?1783919153"
}

val Jumpstart2022Island101 = basicLand("Island") {
    collectorNumber = "101"
    artist = "Cliff Childs"
    imageUri = "https://cards.scryfall.io/normal/front/b/0/b0559d67-e151-4c6b-b8eb-5e9b5603f487.jpg?1783919151"
}

val Jumpstart2022Island102 = basicLand("Island") {
    collectorNumber = "102"
    artist = "Alayna Danner"
    imageUri = "https://cards.scryfall.io/normal/front/4/9/49050cb4-1d8b-4f2f-ba48-206a107eb09c.jpg?1783919152"
}

val Jumpstart2022Island103 = basicLand("Island") {
    collectorNumber = "103"
    artist = "Piotr Dura"
    imageUri = "https://cards.scryfall.io/normal/front/4/2/4237eaa8-1169-47b2-9009-ac529831a36e.jpg?1783919151"
}

val Jumpstart2022Swamp104 = basicLand("Swamp") {
    collectorNumber = "104"
    artist = "Mike Bierek"
    imageUri = "https://cards.scryfall.io/normal/front/4/2/42fec83d-8260-478e-a20f-94d265036e31.jpg?1783919151"
}

val Jumpstart2022Swamp105 = basicLand("Swamp") {
    collectorNumber = "105"
    artist = "Jonas De Ro"
    imageUri = "https://cards.scryfall.io/normal/front/7/b/7b7a442a-16a0-459a-8c24-cd0c72edee03.jpg?1783919150"
}

val Jumpstart2022Swamp106 = basicLand("Swamp") {
    collectorNumber = "106"
    artist = "Alexander Forssberg"
    imageUri = "https://cards.scryfall.io/normal/front/c/8/c802ec1c-4d12-45d0-930b-6b4ca77487e8.jpg?1783919149"
}

val Jumpstart2022Mountain107 = basicLand("Mountain") {
    collectorNumber = "107"
    artist = "Sam Burley"
    imageUri = "https://cards.scryfall.io/normal/front/5/7/577aff75-67f7-42c8-b31f-50026c5ba5c6.jpg?1783919149"
}

val Jumpstart2022Mountain108 = basicLand("Mountain") {
    collectorNumber = "108"
    artist = "Piotr Dura"
    imageUri = "https://cards.scryfall.io/normal/front/5/2/52c79d57-a8eb-427d-86ac-b203c0bb7cda.jpg?1783919149"
}

val Jumpstart2022Mountain109 = basicLand("Mountain") {
    collectorNumber = "109"
    artist = "Andreas Rocha"
    imageUri = "https://cards.scryfall.io/normal/front/a/1/a1cfd796-50a5-404f-acc1-21dbf543ccc1.jpg?1783919149"
}

val Jumpstart2022Forest110 = basicLand("Forest") {
    collectorNumber = "110"
    artist = "Volkan Baǵa"
    imageUri = "https://cards.scryfall.io/normal/front/c/e/cee97ab6-8e81-4396-a788-db0ed0da62d2.jpg?1783919150"
}

val Jumpstart2022Forest111 = basicLand("Forest") {
    collectorNumber = "111"
    artist = "Steven Belledin"
    imageUri = "https://cards.scryfall.io/normal/front/e/e/ee56689a-da7b-4cd7-8767-e3f514b6db9e.jpg?1783919148"
}

val Jumpstart2022Forest112 = basicLand("Forest") {
    collectorNumber = "112"
    artist = "Jim Nelson"
    imageUri = "https://cards.scryfall.io/normal/front/9/0/900eedf8-71b0-4b82-9709-019018bc29fe.jpg?1783919147"
}

val Jumpstart2022Wastes834 = basicLand("Wastes") {
    collectorNumber = "834"
    artist = "Jason Felix"
    imageUri = "https://cards.scryfall.io/normal/front/5/6/56ef693d-7c48-46cb-8f52-abc660a2736e.jpg?1783918782"
}

/**
 * All Jumpstart 2022 basic land variants.
 */
val Jumpstart2022BasicLands = listOf(
    Jumpstart2022Plains98,
    Jumpstart2022Plains99,
    Jumpstart2022Plains100,
    Jumpstart2022Island101,
    Jumpstart2022Island102,
    Jumpstart2022Island103,
    Jumpstart2022Swamp104,
    Jumpstart2022Swamp105,
    Jumpstart2022Swamp106,
    Jumpstart2022Mountain107,
    Jumpstart2022Mountain108,
    Jumpstart2022Mountain109,
    Jumpstart2022Forest110,
    Jumpstart2022Forest111,
    Jumpstart2022Forest112,
    Jumpstart2022Wastes834,
)
