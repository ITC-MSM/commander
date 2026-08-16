package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget

/**
 * Pearl Medallion
 * {2}
 * Artifact
 *
 * White spells you cast cost {1} less to cast.
 *
 * Modelling: the same `ModifySpellCost` static the Invasion Leeches use, with the sign flipped —
 * `YouCast(Any.withColor(WHITE))` + `ReduceGeneric(1)`. `CardPredicate.HasColor` is evaluated
 * against the spell's *colors*, not its color identity, so a multicolored spell that is partly
 * white is a white spell and gets the discount. `ReduceGeneric` only shaves generic mana, which
 * is exactly what the rulings require.
 */
val PearlMedallion = card("Pearl Medallion") {
    manaCost = "{2}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "White spells you cast cost {1} less to cast."

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.YouCast(GameObjectFilter.Any.withColor(Color.WHITE)),
            modification = CostModification.ReduceGeneric(1),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "300"
        artist = "Sue Ellen Brown"
        imageUri = "https://cards.scryfall.io/normal/front/4/4/44588d53-7cce-406a-8e61-cd9866691966.jpg?1783946602"

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
