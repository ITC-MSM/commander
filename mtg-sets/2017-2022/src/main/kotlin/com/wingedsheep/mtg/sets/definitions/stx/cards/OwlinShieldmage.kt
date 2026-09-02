package com.wingedsheep.mtg.sets.definitions.stx.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Owlin Shieldmage — Strixhaven: School of Mages #210 (canonical printing)
 * {3}{W}{B} · Creature — Bird Warlock · 3/3
 *
 * Flying
 * Ward—Pay 3 life. (Whenever this creature becomes the target of a spell or ability an opponent controls, counter it unless that player pays 3 life.)
 *
 * Flying is a plain [Keyword] marker. Ward with a life payment is the parameterized
 * [KeywordAbility.wardLife] (CR 702.21a); the bare `Keyword.WARD` marker is derived from that
 * ability by the builder, so it is not restated here.
 */
val OwlinShieldmage = card("Owlin Shieldmage") {
    manaCost = "{3}{W}{B}"
    colorIdentity = "BW"
    typeLine = "Creature — Bird Warlock"
    oracleText =
        "Flying\n" +
        "Ward—Pay 3 life. (Whenever this creature becomes the target of a spell or ability an opponent controls, counter it unless that player pays 3 life.)"
    power = 3
    toughness = 3

    keywords(Keyword.FLYING)
    keywordAbility(KeywordAbility.wardLife(3))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "210"
        artist = "Raoul Vitale"
        flavorText = "\"Aim higher next time.\""
        imageUri = "https://cards.scryfall.io/normal/front/d/3/d3350367-42bd-44af-be13-ad31c002f8ac.jpg?1783927304"
    }
}
