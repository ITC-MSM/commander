package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.ManaRestriction

/**
 * White Lotus Hideout
 * Land
 * {T}: Add {C}.
 * {T}: Add one mana of any color. Spend this mana only to cast a Lesson or Shrine spell.
 * {1}, {T}: Add one mana of any color.
 */
val WhiteLotusHideout = card("White Lotus Hideout") {
    typeLine = "Land"
    colorIdentity = ""
    oracleText = "{T}: Add {C}.\n" +
        "{T}: Add one mana of any color. Spend this mana only to cast a Lesson or Shrine spell.\n" +
        "{1}, {T}: Add one mana of any color."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddColorlessMana(1)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddAnyColorMana(
            amount = 1,
            restriction = ManaRestriction.SubtypeSpellsOnly(setOf("Lesson", "Shrine"))
        )
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}"), Costs.Tap)
        effect = Effects.AddAnyColorMana(1)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "281"
        artist = "Luc Courtois"
        flavorText = "\"The White Lotus opens wide to those who know her secrets.\"\n" +
            "—Fung, White Lotus member"
        imageUri = "https://cards.scryfall.io/normal/front/2/4/24d1e22c-0d99-4ed5-94f0-fd055fd8e2be.jpg?1764122044"
    }
}
