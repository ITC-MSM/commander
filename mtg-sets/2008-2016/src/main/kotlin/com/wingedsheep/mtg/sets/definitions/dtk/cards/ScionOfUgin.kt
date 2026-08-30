package com.wingedsheep.mtg.sets.definitions.dtk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Scion of Ugin
 * {6}
 * Creature — Dragon Spirit
 * 4 / 4
 *
 * Flying
 *
 * A colorless flier — the empty `colorIdentity` is the card's own, not an omission: its mana
 * cost is generic and it has no colour indicator.
 */
val ScionOfUgin = card("Scion of Ugin") {
    manaCost = "{6}"
    colorIdentity = ""
    typeLine = "Creature — Dragon Spirit"
    power = 4
    toughness = 4
    oracleText = "Flying"

    keywords(Keyword.FLYING)

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "1"
        artist = "Cliff Childs"
        flavorText = "For hundreds of years Ugin slept, encased in the cocoon of stone and magic Sarkhan had created using a shard of a Zendikari hedron. As Ugin lay dormant, his spectral guardians kept vigil."
        imageUri = "https://cards.scryfall.io/normal/front/3/0/302d62d6-8abf-4693-974c-bef6841b394f.jpg?1783938621"
    }
}
