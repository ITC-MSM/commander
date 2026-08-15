package com.wingedsheep.mtg.sets.definitions.spm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Taxi Driver
 * {1}{R}
 * Creature — Human Pilot
 * 3/1
 * {1}, {T}: Target creature gains haste until end of turn.
 */
val TaxiDriver = card("Taxi Driver") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Human Pilot"
    oracleText = "{1}, {T}: Target creature gains haste until end of turn."
    power = 3
    toughness = 1
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}"), Costs.Tap)
        val t = target("target", TargetCreature(filter = TargetFilter.Creature))
        effect = Effects.GrantKeyword(Keyword.HASTE, t)
    }
    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "97"
        artist = "Néstor Ossandón Leal"
        flavorText = "\"I got two speeds, buddy: fast and off duty.\""
        imageUri = "https://cards.scryfall.io/normal/front/a/8/a80d3ed9-5e81-41b7-bb74-ab86cba841c8.jpg?1757377401"
    }
}
