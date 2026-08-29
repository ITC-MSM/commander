package com.wingedsheep.mtg.sets.definitions.ori.cards

import com.wingedsheep.sdk.dsl.basicLand

/**
 * Magic Origins Basic Lands
 *
 * One printing of each basic land type, collector numbers 253/257/261/265/269. Discovered by
 * `MagicOriginsSet.basicLands` via `CardDiscovery.findBasicLandsIn` — basics are never `Printing`
 * rows.
 */

val OriPlains = basicLand("Plains") {
    collectorNumber = "253"
    artist = "Michael Komarck"
    imageUri = "https://cards.scryfall.io/normal/front/8/8/88f96591-4f22-451e-bfb5-32561cc4640d.jpg"
}

val OriIsland = basicLand("Island") {
    collectorNumber = "257"
    artist = "Jung Park"
    imageUri = "https://cards.scryfall.io/normal/front/d/3/d3a9df3b-b542-4915-96ac-0c9027f6870a.jpg"
}

val OriSwamp = basicLand("Swamp") {
    collectorNumber = "261"
    artist = "Larry Elmore"
    imageUri = "https://cards.scryfall.io/normal/front/e/1/e1ae0b13-c9f6-4659-ba85-a87a81f6e730.jpg"
}

val OriMountain = basicLand("Mountain") {
    collectorNumber = "265"
    artist = "Noah Bradley"
    imageUri = "https://cards.scryfall.io/normal/front/e/7/e7b95522-a9de-4ec1-8158-c66813919e62.jpg"
}

val OriForest = basicLand("Forest") {
    collectorNumber = "269"
    artist = "John Avon"
    imageUri = "https://cards.scryfall.io/normal/front/b/4/b4ee8330-f1bf-460b-9345-00d08665e9cf.jpg"
}
