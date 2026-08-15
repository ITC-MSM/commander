package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.PreventActivatedAbilities
import com.wingedsheep.sdk.scripting.predicates.CardPredicate

/**
 * Clarion Conqueror — Tarkir: Dragonstorm #5
 * {2}{W} · Creature — Dragon · 3/3
 *
 * Flying
 * Activated abilities of artifacts, creatures, and planeswalkers can't be activated.
 *
 * The static lockdown reuses [PreventActivatedAbilities] (the Cursed Totem primitive) with an
 * OR filter over the three affected card types. Loyalty abilities of planeswalkers are activated
 * abilities, so they are covered; abilities of lands and non-artifact enchantments are not.
 */
val ClarionConqueror = card("Clarion Conqueror") {
    manaCost = "{2}{W}"
    typeLine = "Creature — Dragon"
    power = 3
    toughness = 3
    oracleText = "Flying\n" +
        "Activated abilities of artifacts, creatures, and planeswalkers can't be activated."

    keywords(Keyword.FLYING)

    staticAbility {
        ability = PreventActivatedAbilities(
            GameObjectFilter(
                cardPredicates = listOf(
                    CardPredicate.Or(
                        listOf(
                            CardPredicate.IsArtifact,
                            CardPredicate.IsCreature,
                            CardPredicate.IsPlaneswalker,
                        )
                    )
                )
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "5"
        artist = "Nathaniel Himawan"
        imageUri = "https://cards.scryfall.io/normal/front/f/8/f892d156-371c-4391-8ae6-25513c5032b0.jpg?1761770058"
    }
}
