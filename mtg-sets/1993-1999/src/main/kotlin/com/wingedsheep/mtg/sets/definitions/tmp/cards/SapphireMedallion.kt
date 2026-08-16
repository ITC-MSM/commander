package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget

/**
 * Sapphire Medallion
 * {2}
 * Artifact
 *
 * Blue spells you cast cost {1} less to cast.
 *
 * Modelling: the same `ModifySpellCost` static the Invasion Leeches use, with the sign flipped —
 * `YouCast(Any.withColor(BLUE))` + `ReduceGeneric(1)`. `CardPredicate.HasColor` is evaluated
 * against the spell's *colors*, not its color identity, so a multicolored spell that is partly
 * blue is a blue spell and gets the discount. `ReduceGeneric` only shaves generic mana, which is
 * exactly what the rulings require.
 */
val SapphireMedallion = card("Sapphire Medallion") {
    manaCost = "{2}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "Blue spells you cast cost {1} less to cast."

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.YouCast(GameObjectFilter.Any.withColor(Color.BLUE)),
            modification = CostModification.ReduceGeneric(1),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "306"
        artist = "Sue Ellen Brown"
        imageUri = "https://cards.scryfall.io/normal/front/3/a/3ab1e253-47cb-4089-87d5-0f998025d98c.jpg?1783946601"

        ruling(
            "2023-07-28",
            "The ability doesn't change the mana cost or mana value of any spell. It changes " +
                "only the total cost you pay."
        )
        ruling(
            "2023-07-28",
            "The ability can't reduce the amount of colored mana you pay for a spell. It " +
                "reduces only the generic mana component of that cost."
        )
        ruling(
            "2023-07-28",
            "If there are additional costs to cast a spell, or if the cost to cast a spell is " +
                "increased by an effect (such as the one created by Thalia, Guardian of " +
                "Thraben's ability), apply those increases before applying cost reductions."
        )
        ruling(
            "2023-07-28",
            "The cost reduction can apply to alternative costs such as flashback costs."
        )
        ruling(
            "2023-07-28",
            "If a spell you cast has {X} in its mana cost, you choose the value of X before " +
                "calculating the spell's total cost."
        )
    }
}
