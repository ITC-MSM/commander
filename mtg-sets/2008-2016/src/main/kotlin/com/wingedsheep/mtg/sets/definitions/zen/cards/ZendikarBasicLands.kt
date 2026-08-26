package com.wingedsheep.mtg.sets.definitions.zen.cards

import com.wingedsheep.sdk.dsl.basicLand

/**
 * Zendikar Basic Lands
 *
 * Zendikar printed four full-art variants of each basic land type (cards 230–249). Our Scryfall
 * cache retains one canonical variant per type, so we register that representative printing for
 * each: Plains 230, Island 234, Swamp 238, Mountain 242, Forest 246.
 */

val ZenPlains230 = basicLand("Plains") {
    collectorNumber = "230"
    artist = "John Avon"
    imageUri = "https://cards.scryfall.io/normal/front/b/c/bc4f4b6d-ff35-4b1f-974b-f39569e6b3c7.jpg"
}

val ZenIsland234 = basicLand("Island") {
    collectorNumber = "234"
    artist = "John Avon"
    imageUri = "https://cards.scryfall.io/normal/front/e/f/efd86f2a-bd28-4731-838d-78e67be8b49e.jpg"
}

val ZenSwamp238 = basicLand("Swamp") {
    collectorNumber = "238"
    artist = "John Avon"
    imageUri = "https://cards.scryfall.io/normal/front/7/c/7cd6becc-f06a-4fd3-8305-70604b92a187.jpg"
}

val ZenMountain242 = basicLand("Mountain") {
    collectorNumber = "242"
    artist = "John Avon"
    imageUri = "https://cards.scryfall.io/normal/front/2/3/232ee129-0db1-4a03-9eda-4692a8495b53.jpg"
}

val ZenForest246 = basicLand("Forest") {
    collectorNumber = "246"
    artist = "John Avon"
    imageUri = "https://cards.scryfall.io/normal/front/f/0/f0ca4b9f-4ee6-4ad8-a95f-326ada9de3cd.jpg"
}
