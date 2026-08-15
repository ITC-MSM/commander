package com.wingedsheep.mtg.sets.definitions.dom.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.ManaRestriction

/**
 * Vodalian Arcanist
 * {1}{U}
 * Creature — Merfolk Wizard
 * 1/3
 * {T}: Add {C}. Spend this mana only to cast an instant or sorcery spell.
 *
 */
val VodalianArcanist = card("Vodalian Arcanist") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Merfolk Wizard"
    power = 1
    toughness = 3
    oracleText = "{T}: Add {C}. Spend this mana only to cast an instant or sorcery spell."

    activatedAbility {
        cost = AbilityCost.Tap
        effect = Effects.AddColorlessMana(1, ManaRestriction.InstantOrSorceryOnly)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "73"
        artist = "Tyler Walpole"
        flavorText = "The Vodalian Empire tends to be insular. The scholarly Volshe caste are the exception, using education as an antidote to xenophobia."
        imageUri = "https://cards.scryfall.io/normal/front/5/c/5c7795fb-2995-4dff-bc1b-7809bfbb1657.jpg?1562736340"
    }
}
