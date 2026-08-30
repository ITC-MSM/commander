package com.wingedsheep.mtg.sets.definitions.dtk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Conifer Strider
 * {3}{G}
 * Creature — Elemental
 * 5 / 1
 *
 * Hexproof (This creature can't be the target of spells or abilities your opponents control.)
 *
 * Hexproof is engine-live off the bare keyword — the targeting restriction lives in the
 * legality check, so the card needs no script of its own.
 */
val ConiferStrider = card("Conifer Strider") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elemental"
    power = 5
    toughness = 1
    oracleText = "Hexproof (This creature can't be the target of spells or abilities your opponents control.)"

    keywords(Keyword.HEXPROOF)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "179"
        artist = "YW Tang"
        flavorText = "Atarka's presence thaws the glaciers of Qal Sisma, forcing its elementals to migrate or adapt."
        imageUri = "https://cards.scryfall.io/normal/front/7/2/72f07879-7893-46d9-9239-8d2625355881.jpg?1783938581"
    }
}
