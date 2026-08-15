package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.values.ContextPropertyKey
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Curator of Sun's Creation
 * {3}{R}
 * Creature — Human Artificer
 * 3/3
 * Whenever you discover, discover again for the same value. This ability triggers only once each turn.
 */
val CuratorOfSunsCreation = card("Curator of Sun's Creation") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Human Artificer"
    power = 3
    toughness = 3
    oracleText = "Whenever you discover, discover again for the same value. This ability triggers only once each turn."
    triggeredAbility {
        trigger = Triggers.WheneverYouDiscover
        // "This ability triggers only once each turn." — prevents the discover-again from
        // recursively re-triggering this same ability (its own discover would otherwise chain).
        oncePerTurn = true
        // "discover again for the same value" — reuse the triggering discover's threshold N.
        effect = Effects.Discover(DynamicAmount.ContextProperty(ContextPropertyKey.TRIGGER_DISCOVER_VALUE))
    }
    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "141"
        artist = "Javier Charro"
        flavorText = "\"Put your hammers away and bring me some blankets! Such a gift from Tilonalli must be handled with the utmost care.\""
        imageUri = "https://cards.scryfall.io/normal/front/a/1/a144d0b3-678d-4f4c-a9b0-22af19f5cf9f.jpg?1782694496"
    }
}
