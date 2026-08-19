package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget

/**
 * Thorn of Amethyst
 * {2}
 * Artifact
 * Noncreature spells cost {1} more to cast.
 *
 * The tax is symmetrical — [SpellCostTarget.AnyCaster], so it hits its own controller too.
 */
val ThornOfAmethyst = card("Thorn of Amethyst") {
    manaCost = "{2}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "Noncreature spells cost {1} more to cast."

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.AnyCaster(GameObjectFilter.Noncreature),
            modification = CostModification.IncreaseGeneric(1)
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "262"
        artist = "Chuck Lukacs"
        flavorText = "Mined from a cave in the Dark Meanders, it shines brightest when no one is looking."
        imageUri = "https://cards.scryfall.io/normal/front/e/4/e472d4f5-add4-4de3-8718-31a47a35277c.jpg?1783942851"
    }
}
