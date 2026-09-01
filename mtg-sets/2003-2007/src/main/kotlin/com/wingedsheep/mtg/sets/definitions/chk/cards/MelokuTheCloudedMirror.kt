package com.wingedsheep.mtg.sets.definitions.chk.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Meloku the Clouded Mirror
 * {4}{U}
 * Legendary Creature — Moonfolk Wizard
 * 2 / 4
 *
 * Flying
 * {1}, Return a land you control to its owner's hand: Create a 1/1 blue Illusion creature token
 * with flying.
 *
 * The Moonfolk land-bounce cost is [Costs.ReturnToHand] over `GameObjectFilter.Land` composed with
 * the mana half — the atom's enumerator is already you-control-only, so "you control" needs no
 * extra predicate. The ability has no activation limit, so the whole card is one activated ability
 * whose payoff is the plain [Effects.CreateToken] facade.
 */
val MelokuTheCloudedMirror = card("Meloku the Clouded Mirror") {
    manaCost = "{4}{U}"
    colorIdentity = "U"
    typeLine = "Legendary Creature — Moonfolk Wizard"
    power = 2
    toughness = 4
    oracleText = "Flying\n" +
        "{1}, Return a land you control to its owner's hand: Create a 1/1 blue Illusion creature token with flying."

    keywords(Keyword.FLYING)

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}"), Costs.ReturnToHand(GameObjectFilter.Land))
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.BLUE),
            creatureTypes = setOf("Illusion"),
            keywords = setOf(Keyword.FLYING)
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "74"
        artist = "Scott M. Fischer"
        flavorText = "He loved his cities in the clouds. When he traveled to the lands below, he brought many reminders of his home."
        imageUri = "https://cards.scryfall.io/normal/front/1/c/1caaa733-6d4c-4e18-a64e-f95851c7063b.jpg?1783944325"
    }
}
