package com.wingedsheep.mtg.sets.definitions.dmu.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Electrostatic Infantry
 * {1}{R}
 * Creature — Dwarf Wizard
 * 1/2
 * Trample
 * Whenever you cast an instant or sorcery spell, put a +1/+1 counter on this creature.
 */
val ElectrostaticInfantry = card("Electrostatic Infantry") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Dwarf Wizard"
    oracleText = "Trample\nWhenever you cast an instant or sorcery spell, put a +1/+1 counter on this creature."
    power = 1
    toughness = 2

    keywords(Keyword.TRAMPLE)

    triggeredAbility {
        trigger = Triggers.YouCastInstantOrSorcery
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "122"
        artist = "Kekai Kotaki"
        flavorText = "\"That's not exactly what I had in mind when I said 'charge,' but I like your enthusiasm!\"\n—Balmor, battlemage captain"
        imageUri = "https://cards.scryfall.io/normal/front/5/e/5ed2d72f-f1cf-45a7-adf7-969f531721ce.jpg?1783921319"
    }
}
