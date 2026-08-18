package com.wingedsheep.mtg.sets.definitions.drk.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget

/**
 * Stone Calendar
 * {5}
 * Artifact
 * Spells you cast cost {1} less to cast.
 *
 * The Medallion static with no colour filter — [CostModification.ReduceGeneric] shaves only the
 * generic part of the cost (CR 601.2f), so a one-mana coloured spell is unaffected.
 */
val StoneCalendar = card("Stone Calendar") {
    manaCost = "{5}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "Spells you cast cost {1} less to cast."

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.YouCast(GameObjectFilter.Any),
            modification = CostModification.ReduceGeneric(1),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "111"
        artist = "Amy Weber"
        flavorText = "The Pretender Mairsil ordered a great Calendar drawn up to show when the paths to the Dark Lands were strongest."
        imageUri = "https://cards.scryfall.io/normal/front/a/4/a49ba1a5-33b1-40f2-9780-26139ed829d7.jpg?1783947924"

        ruling("2004-10-04", "Does not change the mana cost of the spell, it just reduces what you pay for it.")
        ruling("2004-10-04", "You may choose not to apply Stone Calendar's cost reduction effect.")
    }
}
