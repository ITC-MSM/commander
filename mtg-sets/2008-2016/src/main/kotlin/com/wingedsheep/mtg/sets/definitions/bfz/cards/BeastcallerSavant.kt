package com.wingedsheep.mtg.sets.definitions.bfz.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.ManaRestriction

/**
 * Beastcaller Savant
 * {1}{G}
 * Creature — Elf Shaman Ally
 * 1/1
 * Haste
 * {T}: Add one mana of any color. Spend this mana only to cast a creature spell.
 */
val BeastcallerSavant = card("Beastcaller Savant") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf Shaman Ally"
    power = 1
    toughness = 1
    oracleText = "Haste\n" +
        "{T}: Add one mana of any color. Spend this mana only to cast a creature spell."

    keywords(Keyword.HASTE)

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddManaOfChoice(restriction = ManaRestriction.CreatureSpellsOnly)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "170"
        artist = "Anthony Palumbo"
        flavorText = "\"They come because I call. They stay because I listen.\""
        imageUri = "https://cards.scryfall.io/normal/front/c/c/cca9f5da-986c-43ab-a2ec-efb8b098c17c.jpg?1783938191"
    }
}
