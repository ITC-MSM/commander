package com.wingedsheep.mtg.sets.definitions.thb.cards

import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * Mire's Grasp
 * {1}{B}
 * Enchantment — Aura
 *
 * Enchant creature
 * Enchanted creature gets -3/-3.
 *
 * "Enchant creature" is a bare creature restriction — any creature, either side of the table — so the
 * aura target is [Targets.Creature] with no controller predicate. [ModifyStats] defaults its filter to
 * the attached creature, which is exactly the "enchanted creature" the printed line names.
 */
val MiresGrasp = card("Mire's Grasp") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\n" +
        "Enchanted creature gets -3/-3."

    auraTarget = Targets.Creature

    staticAbility {
        ability = ModifyStats(-3, -3)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "106"
        artist = "Chris Rallis"
        flavorText = "Those caught attempting to escape the Underworld spend the rest of their " +
            "existence trapped in the Mire of Punishment."
        imageUri = "https://cards.scryfall.io/normal/front/0/a/0ae0c536-612d-4916-a8da-5aaaf14218b1.jpg"
    }
}
