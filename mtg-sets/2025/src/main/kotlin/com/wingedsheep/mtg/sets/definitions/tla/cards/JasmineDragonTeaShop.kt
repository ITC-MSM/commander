package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.ManaRestriction

/**
 * Jasmine Dragon Tea Shop
 * Land
 * {T}: Add {C}.
 * {T}: Add one mana of any color. Spend this mana only to cast an Ally spell or activate an
 * ability of an Ally source.
 * {5}, {T}: Create a 1/1 white Ally creature token.
 */
val JasmineDragonTeaShop = card("Jasmine Dragon Tea Shop") {
    typeLine = "Land"
    colorIdentity = ""
    oracleText = "{T}: Add {C}.\n" +
        "{T}: Add one mana of any color. Spend this mana only to cast an Ally spell or activate " +
        "an ability of an Ally source.\n" +
        "{5}, {T}: Create a 1/1 white Ally creature token."

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
            restriction = ManaRestriction.SubtypeSpellsOrAbilitiesOnly("Ally", creatureOnly = false)
        )
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{5}"), Costs.Tap)
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Ally")
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "270"
        artist = "Leanna Crossan"
        flavorText = "Where the secret ingredient truly is love."
        imageUri = "https://cards.scryfall.io/normal/front/d/a/da2c83d4-a95f-47ff-a08f-694eb78d6b9b.jpg?1764121978"
    }
}
