package com.wingedsheep.mtg.sets.definitions.conflux.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.ProtectionScope

/**
 * Valeron Outlander — Conflux #130
 * {G}{W} · Creature — Human Scout · 2/2
 *
 * Protection from black
 *
 * The Bant member of the "Outlander" cycle — see [GoblinOutlander]. The card is nothing but the
 * printed protection keyword: [KeywordAbility.Protection] with a [ProtectionScope.Color] scope,
 * which the engine projects for targeting, blocking, damage and aura fall-off.
 */
val ValeronOutlander = card("Valeron Outlander") {
    manaCost = "{G}{W}"
    colorIdentity = "GW"
    typeLine = "Creature — Human Scout"
    power = 2
    toughness = 2
    oracleText = "Protection from black"

    keywordAbility(KeywordAbility.Protection(ProtectionScope.Color(Color.BLACK)))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "130"
        artist = "Matt Stewart"
        flavorText = "After years of honing her philosophy in debate with stubborn rhoxes, Niella was ready to convert any heathen."
        imageUri = "https://cards.scryfall.io/normal/front/7/e/7e73cd46-0dec-441b-bb91-ec6defb3355e.jpg"
    }
}
