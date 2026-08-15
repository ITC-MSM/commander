package com.wingedsheep.mtg.sets.definitions.mor.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget

/**
 * Ballyrush Banneret
 * {1}{W}
 * Creature — Kithkin Soldier
 * 2/1
 *
 * Kithkin spells and Soldier spells you cast cost {1} less to cast.
 *
 * Canonical printing (earliest real set: Morningtide). The Foundations reprint
 * contributes only a [com.wingedsheep.sdk.model.Printing] row.
 */
val BallyrushBanneret = card("Ballyrush Banneret") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Kithkin Soldier"
    power = 2
    toughness = 1
    oracleText = "Kithkin spells and Soldier spells you cast cost {1} less to cast."

    staticAbility {
        // "Kithkin spells and Soldier spells" — a spell qualifies if it has EITHER
        // subtype, so an OR over the two subtypes (withAnySubtype).
        ability = ModifySpellCost(
            target = SpellCostTarget.YouCast(
                GameObjectFilter.Any.withAnySubtype("Kithkin", "Soldier")
            ),
            modification = CostModification.ReduceGeneric(1),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "1"
        artist = "Ralph Horsley"
        flavorText = "Only wool from the side of the springjack turned most often to the sun can be woven into kithkin battle standards. Jackherds record every movement of their woolly-jacks until shearing."
        imageUri = "https://cards.scryfall.io/normal/front/a/0/a029814e-d84d-43e5-b483-e918871b3333.jpg?1782716266"
    }
}
