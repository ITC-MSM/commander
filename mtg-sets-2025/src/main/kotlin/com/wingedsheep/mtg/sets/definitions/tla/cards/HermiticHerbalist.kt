package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.ManaRestriction

/**
 * Hermitic Herbalist
 * Creature — Human Druid Ally
 * {T}: Add one mana of any color.
 * {T}: Add two mana in any combination of colors. Spend this mana only to cast Lesson spells.
 */
val HermiticHerbalist = card("Hermitic Herbalist") {
    typeLine = "Creature — Human Druid Ally"
    manaCost = "{G}{U}"
    colorIdentity = "GU"
    power = 2
    toughness = 3
    oracleText = "{T}: Add one mana of any color.\n" +
        "{T}: Add two mana in any combination of colors. Spend this mana only to cast Lesson spells."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddAnyColorMana(1)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddManaInAnyCombination(
            amount = 2,
            restriction = ManaRestriction.SubtypeSpellsOnly(setOf("Lesson"))
        )
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "226"
        artist = "Kozato"
        flavorText = "\"Wounded Earth Kingdom troops still come by now and again—brave boys—and " +
            "thanks to my remedies they always leave in better shape than when they arrive.\""
        imageUri = "https://cards.scryfall.io/normal/front/5/d/5d0ee441-a057-41b5-abb8-dc86864ac248.jpg?1764121653"
    }
}
