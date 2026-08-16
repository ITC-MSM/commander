package com.wingedsheep.mtg.sets.definitions.mh3.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * The two further Modern Horizons 3 art treatments of Snow-Covered Wastes. The canonical
 * [com.wingedsheep.sdk.model.CardDefinition] is [SnowCoveredWastes] (collector number 229) in this
 * same package; these rows contribute only presentation data.
 *
 * The other five MH3 basics get their variants as separate `basicLand(...)` definitions
 * (`ModernHorizons3BasicLands.kt`) because that helper mints one `CardDefinition` per art. Snow-
 * Covered Wastes is a hand-written `card` — it carries the Snow supertype — so its variants take
 * the ordinary reprint shape instead.
 */
val SnowCoveredWastesFullArt = Printing(
    oracleId = "46a07b53-ff58-4bd6-80dd-ded2eb0e29a3",
    name = "Snow-Covered Wastes",
    setCode = "MH3",
    collectorNumber = "309",
    scryfallId = "ad21a874-525e-4d11-bd8e-bc44918bec40",
    artist = "Tomáš Honz",
    imageUri = "https://cards.scryfall.io/normal/front/a/d/ad21a874-525e-4d11-bd8e-bc44918bec40.jpg?1783911204",
    releaseDate = "2024-06-14",
    rarity = Rarity.UNCOMMON,
    isFullArt = true,
)

val SnowCoveredWastesBorderless = Printing(
    oracleId = "46a07b53-ff58-4bd6-80dd-ded2eb0e29a3",
    name = "Snow-Covered Wastes",
    setCode = "MH3",
    collectorNumber = "439",
    scryfallId = "d8c0de06-7782-4d2b-93ba-ef42cd5f79b4",
    artist = "Mark Poole",
    imageUri = "https://cards.scryfall.io/normal/front/d/8/d8c0de06-7782-4d2b-93ba-ef42cd5f79b4.jpg?1783911162",
    releaseDate = "2024-06-14",
    rarity = Rarity.UNCOMMON,
)
