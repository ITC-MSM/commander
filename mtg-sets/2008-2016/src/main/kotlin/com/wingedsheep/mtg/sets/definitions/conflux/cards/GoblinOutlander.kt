package com.wingedsheep.mtg.sets.definitions.conflux.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.ProtectionScope

/**
 * Goblin Outlander — Conflux #109
 * {B}{R} · Creature — Goblin Scout · 2/2
 *
 * Protection from white
 *
 * One of the five "Outlander" commons, each an enemy-coloured 2/2 Scout with protection from the
 * colour its two colours are allied against. The whole card is the printed protection keyword:
 * [KeywordAbility.Protection] with a [ProtectionScope.Color] scope, which the engine projects as a
 * protection keyword read by targeting, blocking, combat and non-combat damage, and aura fall-off
 * alike — no per-card wiring.
 */
val GoblinOutlander = card("Goblin Outlander") {
    manaCost = "{B}{R}"
    colorIdentity = "BR"
    typeLine = "Creature — Goblin Scout"
    power = 2
    toughness = 2
    oracleText = "Protection from white"

    keywordAbility(KeywordAbility.Protection(ProtectionScope.Color(Color.WHITE)))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "109"
        artist = "Trevor Claxton"
        flavorText = "Egbol stared in wonder at Naya's landscape. So much to eat. So much to steal."
        imageUri = "https://cards.scryfall.io/normal/front/5/e/5e33683b-8bda-4bb9-beb4-fc0cd5ed79ae.jpg"
    }
}
