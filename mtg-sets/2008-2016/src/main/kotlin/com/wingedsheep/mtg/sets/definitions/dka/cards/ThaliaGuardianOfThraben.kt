package com.wingedsheep.mtg.sets.definitions.dka.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget

/**
 * Thalia, Guardian of Thraben
 * {1}{W}
 * Legendary Creature — Human Soldier
 * 2/1
 * First strike
 * Noncreature spells cost {1} more to cast.
 *
 * Dark Ascension is Thalia's earliest real-expansion printing, so the canonical
 * CardDefinition lives here; VOW gets a Printing(...) row.
 *
 * The tax applies to every caster's noncreature spells (a symmetric effect), so it
 * uses SpellCostTarget.AnyCaster(GameObjectFilter.Noncreature) — the same shape as
 * Glowrider (LGN). CostModification.IncreaseGeneric(1) adds {1} generic.
 */
val ThaliaGuardianOfThraben = card("Thalia, Guardian of Thraben") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Legendary Creature — Human Soldier"
    power = 2
    toughness = 1
    oracleText = "First strike\n" +
        "Noncreature spells cost {1} more to cast."

    keywords(Keyword.FIRST_STRIKE)

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.AnyCaster(GameObjectFilter.Noncreature),
            modification = CostModification.IncreaseGeneric(1),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "24"
        artist = "Jana Schirmer & Johannes Voss"
        flavorText = "\"Thraben is our home and I will not see it fall to this unhallowed horde.\""
        imageUri = "https://cards.scryfall.io/normal/front/8/2/824423ff-6441-4be6-b754-810adf9ca6a2.jpg?1783940849"
    }
}
