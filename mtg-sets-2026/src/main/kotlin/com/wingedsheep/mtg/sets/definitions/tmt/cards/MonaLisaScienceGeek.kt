package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.scripting.values.EntityNumericProperty
import com.wingedsheep.sdk.scripting.values.EntityReference

/**
 * Mona Lisa, Science Geek
 * {2}{G}
 * Legendary Creature — Lizard Mutant
 * 1/3
 *
 * Reach
 * {T}: Add X mana of any one color, where X is Mona Lisa's power.
 */
val MonaLisaScienceGeek = card("Mona Lisa, Science Geek") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Legendary Creature — Lizard Mutant"
    oracleText = "Reach\n{T}: Add X mana of any one color, where X is Mona Lisa's power."
    power = 1
    toughness = 3

    keywords(Keyword.REACH)

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddAnyColorMana(
            DynamicAmount.EntityProperty(EntityReference.Source, EntityNumericProperty.Power)
        )
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "123"
        artist = "Oriana Menendez"
        flavorText = "\"I felt kind of excited about being mutated, you know? I was getting to have this once in a lifetime experience! Maybe I could even base my thesis on it . . .\""
        imageUri = "https://cards.scryfall.io/normal/front/c/2/c2164204-120c-4dff-86ac-46ca9012ccd9.jpg?1771424712"
    }
}
