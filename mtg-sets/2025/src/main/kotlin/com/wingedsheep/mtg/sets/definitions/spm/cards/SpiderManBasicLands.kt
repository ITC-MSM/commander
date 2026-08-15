package com.wingedsheep.mtg.sets.definitions.spm.cards

import com.wingedsheep.sdk.dsl.basicLand

/**
 * Marvel's Spider-Man Basic Lands
 *
 * Two full-art variants of each basic land type, collector numbers 189-198.
 */

val SpmPlains189 = basicLand("Plains") {
    collectorNumber = "189"
    artist = "Sarah Finnigan"
    imageUri = "https://cards.scryfall.io/normal/front/1/1/1164f7ec-7b2f-4cc9-90bb-7eaaa331b4cd.jpg?1783905295"
}

val SpmPlains194 = basicLand("Plains") {
    collectorNumber = "194"
    artist = "Jonas De Ro"
    imageUri = "https://cards.scryfall.io/normal/front/9/4/94e88862-d53d-49b6-8aa5-95f07507c6e1.jpg?1783905294"
}

val SpmIsland190 = basicLand("Island") {
    collectorNumber = "190"
    artist = "Sarah Finnigan"
    imageUri = "https://cards.scryfall.io/normal/front/d/5/d59cb0b5-fd4f-4dde-a69f-7ca6aa12b89f.jpg?1783905295"
}

val SpmIsland195 = basicLand("Island") {
    collectorNumber = "195"
    artist = "Jonas De Ro"
    imageUri = "https://cards.scryfall.io/normal/front/1/4/14fcdc57-12e2-429b-8916-4df752e462d4.jpg?1783905292"
}

val SpmSwamp191 = basicLand("Swamp") {
    collectorNumber = "191"
    artist = "Sarah Finnigan"
    imageUri = "https://cards.scryfall.io/normal/front/5/c/5cb03b18-d74c-4c89-9539-3549d2e8ff5f.jpg?1783905295"
}

val SpmSwamp196 = basicLand("Swamp") {
    collectorNumber = "196"
    artist = "Jonas De Ro"
    imageUri = "https://cards.scryfall.io/normal/front/a/7/a7150a6e-240f-4628-acdc-153d404370ff.jpg?1783905294"
}

val SpmMountain192 = basicLand("Mountain") {
    collectorNumber = "192"
    artist = "Sarah Finnigan"
    imageUri = "https://cards.scryfall.io/normal/front/b/0/b044630d-50e7-431b-8e91-bd53e967f594.jpg?1783905295"
}

val SpmMountain197 = basicLand("Mountain") {
    collectorNumber = "197"
    artist = "Jonas De Ro"
    imageUri = "https://cards.scryfall.io/normal/front/f/c/fc5004db-8db3-4506-bb01-f41be7968824.jpg?1783905292"
}

val SpmForest193 = basicLand("Forest") {
    collectorNumber = "193"
    artist = "Sarah Finnigan"
    imageUri = "https://cards.scryfall.io/normal/front/7/b/7b6c2532-be5a-4f1f-893c-36bcda2a699d.jpg?1783905294"
}

val SpmForest198 = basicLand("Forest") {
    collectorNumber = "198"
    artist = "Jonas De Ro"
    imageUri = "https://cards.scryfall.io/normal/front/c/4/c4fec728-8e3f-427d-81ab-e06114910223.jpg?1783905292"
}

/**
 * All Marvel's Spider-Man basic land variants.
 */
val SpiderManBasicLands = listOf(
    SpmPlains189, SpmPlains194,
    SpmIsland190, SpmIsland195,
    SpmSwamp191, SpmSwamp196,
    SpmMountain192, SpmMountain197,
    SpmForest193, SpmForest198,
)
