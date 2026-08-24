package com.wingedsheep.mtg.sets.definitions.bfz.cards

import com.wingedsheep.sdk.dsl.basicLand

/**
 * Battle for Zendikar Basic Lands
 *
 * Battle for Zendikar printed five full-art variants of each basic land type (cards 250–274),
 * each also available in a non-full-art version. Our Scryfall cache retains one canonical
 * variant per type, so we register that representative printing for each: Plains 250,
 * Island 255, Swamp 260, Mountain 265, Forest 270.
 */

val BfzPlains250 = basicLand("Plains") {
    collectorNumber = "250"
    artist = "Noah Bradley"
    imageUri = "https://cards.scryfall.io/normal/front/5/8/58a735c9-08a1-4950-bf8c-ed1cfba76765.jpg?1783938172"
}

val BfzIsland255 = basicLand("Island") {
    collectorNumber = "255"
    artist = "Noah Bradley"
    imageUri = "https://cards.scryfall.io/normal/front/8/4/8490261d-4246-4232-b7fc-23204c14a7b5.jpg?1783938169"
}

val BfzSwamp260 = basicLand("Swamp") {
    collectorNumber = "260"
    artist = "Noah Bradley"
    imageUri = "https://cards.scryfall.io/normal/front/1/3/132155ae-f7a4-4957-91cb-e51ff52716f9.jpg?1783938168"
}

val BfzMountain265 = basicLand("Mountain") {
    collectorNumber = "265"
    artist = "Noah Bradley"
    imageUri = "https://cards.scryfall.io/normal/front/0/c/0c9cbae1-6b04-4408-82fe-3fcf61dffbe2.jpg?1783938165"
}

val BfzForest270 = basicLand("Forest") {
    collectorNumber = "270"
    artist = "Noah Bradley"
    imageUri = "https://cards.scryfall.io/normal/front/6/4/642102a2-abde-4e76-a6d6-08f7befe1196.jpg?1783938163"
}
