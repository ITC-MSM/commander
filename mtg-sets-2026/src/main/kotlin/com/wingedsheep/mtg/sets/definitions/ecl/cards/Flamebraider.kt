package com.wingedsheep.mtg.sets.definitions.ecl.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ManaRestriction

/**
 * Flamebraider
 * {1}{R}
 * Creature — Elemental Bard
 * 2/2
 *
 * {T}: Add two mana in any combination of colors. Spend this mana only to cast
 * Elemental spells or activate abilities of Elemental sources.
 */
val Flamebraider = card("Flamebraider") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Elemental Bard"
    power = 2
    toughness = 2
    oracleText = "{T}: Add two mana in any combination of colors. Spend this mana only to cast Elemental spells or activate abilities of Elemental sources."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddManaInAnyCombination(
            amount = 2,
            restriction = ManaRestriction.SubtypeSpellsOrAbilitiesOnly("Elemental")
        )
        manaAbility = true
        description = "{T}: Add two mana in any combination of colors. Spend this mana only to cast Elemental spells or activate abilities of Elemental sources."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "139"
        artist = "Pete Venters"
        flavorText = "\"You seek guidance on your Path. Then let me provide it: What greater flame is there than the sun?\""
        imageUri = "https://cards.scryfall.io/normal/front/b/8/b8aa428c-5a77-444f-b75e-a113e46fe4e0.jpg?1767732747"
        ruling("2025-11-17", "\"Elemental sources\" include any objects with the creature type Elemental. For example, you could spend the mana to activate the ability of an Elemental permanent you control or an Elemental card in your hand or graveyard.")
    }
}
