package com.wingedsheep.mtg.sets.definitions.ons.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget

/**
 * Dream Chisel
 * {2}
 * Artifact
 * Face-down creature spells you cast cost {1} less to cast.
 */
val DreamChisel = card("Dream Chisel") {
    manaCost = "{2}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "Face-down creature spells you cast cost {1} less to cast."

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.FaceDownYouCast,
            modification = CostModification.ReduceGeneric(1),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "308"
        artist = "Ron Spears"
        flavorText = "Ixidor's first creation was a chisel, and with it he carved a world from his dreams."
        imageUri = "https://cards.scryfall.io/normal/front/e/8/e89610e9-f1d3-4332-901a-2598bf01d61d.jpg?1562950378"
    }
}
