package com.wingedsheep.mtg.sets.definitions.avr.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.ManaRestriction

/**
 * Somberwald Sage
 * {2}{G}
 * Creature — Human Druid
 * 0/1
 * {T}: Add three mana of any one color. Spend this mana only to cast creature spells.
 */
val SomberwaldSage = card("Somberwald Sage") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Human Druid"
    power = 0
    toughness = 1
    oracleText = "{T}: Add three mana of any one color. Spend this mana only to cast creature spells."

    activatedAbility {
        cost = AbilityCost.Tap
        effect = Effects.AddAnyColorMana(3, restriction = ManaRestriction.CreatureSpellsOnly)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "194"
        artist = "Steve Argyle"
        imageUri = "https://cards.scryfall.io/normal/front/4/0/409c0272-7a43-4a6c-ab3f-740397b1f5c8.jpg?1782714442"
    }
}
