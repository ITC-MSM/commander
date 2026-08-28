package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.ManaRestriction

/**
 * Smokebraider
 * {1}{R}
 * Creature — Elemental Shaman
 * 1/1
 * {T}: Add two mana in any combination of colors. Spend this mana only to cast Elemental
 *      spells or activate abilities of Elementals.
 *
 * "In any combination of colors" colours each pip independently, so this is
 * [Effects.AddManaInAnyCombination] (all five colours, the default) rather than
 * `AddAnyColorMana`, which would force both pips to the same colour. The restriction admits both
 * Elemental spells and activated abilities of Elemental sources, which is
 * [ManaRestriction.SubtypeSpellsOrAbilitiesOnly]'s default (`creatureOnly = false`).
 */
val Smokebraider = card("Smokebraider") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Elemental Shaman"
    power = 1
    toughness = 1
    oracleText = "{T}: Add two mana in any combination of colors. Spend this mana only to cast " +
        "Elemental spells or activate abilities of Elementals."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddManaInAnyCombination(
            amount = 2,
            restriction = ManaRestriction.SubtypeSpellsOrAbilitiesOnly("Elemental"),
        )
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "189"
        artist = "Anthony S. Waters"
        flavorText = "\"Be silent and listen to your inner fire. Only then can you walk the Path of Flame.\""
        imageUri = "https://cards.scryfall.io/normal/front/e/3/e3c6227b-bd43-47e8-927f-f8a78c532591.jpg?1783942871"
    }
}
