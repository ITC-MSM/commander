package com.wingedsheep.mtg.sets.definitions.otj.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostGating
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget

/**
 * Geyser Drake
 * {2}{U}
 * Creature — Drake
 * 2/3
 * Flying
 * During turns other than yours, spells you cast cost {1} less to cast.
 *
 * The reduction touches only the generic mana component (CostModification.ReduceGeneric),
 * matching the ruling that it can't reduce colored mana. The OnlyIf(IsNotYourTurn) gate
 * scopes it to opponents' turns.
 */
val GeyserDrake = card("Geyser Drake") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Drake"
    power = 2
    toughness = 3
    oracleText = "Flying\nDuring turns other than yours, spells you cast cost {1} less to cast."

    keywords(Keyword.FLYING)

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.YouCast(GameObjectFilter.Any),
            modification = CostModification.ReduceGeneric(1),
            gating = CostGating.OnlyIf(Conditions.IsNotYourTurn),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "51"
        artist = "Daniel Romanovsky"
        imageUri = "https://cards.scryfall.io/normal/front/b/2/b270377b-33eb-4e5e-9d14-0da2876da74f.jpg?1712355434"

        ruling("2024-04-12", "Geyser Drake's last ability doesn't change the mana cost or mana value of any spell. It changes only the total cost you pay to cast spells.")
        ruling("2024-04-12", "Geyser Drake's last ability can't reduce the amount of colored mana you pay for a spell. It reduces only the generic mana component of that spell's cost.")
    }
}
