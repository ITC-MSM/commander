package com.wingedsheep.mtg.sets.definitions.conflux.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.ProtectionScope

/**
 * Nacatl Savage — Conflux #86
 * {1}{G} · Creature — Cat Warrior · 2/1
 *
 * Protection from artifacts
 *
 * Naya's answer to Esper, and the card-type sibling of the colour-scoped Outlander cycle
 * ([GoblinOutlander] and friends). "Protection from artifacts" is the same
 * [KeywordAbility.Protection] keyword with a [ProtectionScope.CardType] scope instead of a colour
 * one — projected as a protection keyword that targeting, blocking, damage prevention and aura
 * fall-off all read, so an artifact creature can't block it and an artifact source deals it no
 * damage without any per-card wiring.
 */
val NacatlSavage = card("Nacatl Savage") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Cat Warrior"
    power = 2
    toughness = 1
    oracleText = "Protection from artifacts"

    keywordAbility(KeywordAbility.Protection(ProtectionScope.CardType("Artifact")))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "86"
        artist = "Paolo Parente"
        flavorText = "\"Blades dull and armor dents. Marisi taught us that instinct is the only thing a true warrior needs.\" —Ajani"
        imageUri = "https://cards.scryfall.io/normal/front/a/d/ad602fba-6a73-4fd0-aff5-802c3be3100e.jpg"
    }
}
