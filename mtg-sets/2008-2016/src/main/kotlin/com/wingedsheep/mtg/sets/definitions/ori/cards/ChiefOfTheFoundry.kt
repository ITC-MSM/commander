package com.wingedsheep.mtg.sets.definitions.ori.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Chief of the Foundry
 * {3}
 * Artifact Creature — Construct
 * 2/3
 * Other artifact creatures you control get +1/+1.
 *
 * An ordinary layer-7c lord: [ModifyStats] over a [GroupFilter] of
 * `GameObjectFilter.ArtifactCreature.youControl()` with `excludeSelf = true` for the printed
 * "other", so the Chief never pumps itself.
 */
val ChiefOfTheFoundry = card("Chief of the Foundry") {
    manaCost = "{3}"
    typeLine = "Artifact Creature — Construct"
    oracleText = "Other artifact creatures you control get +1/+1."
    power = 2
    toughness = 3

    staticAbility {
        ability = ModifyStats(
            powerBonus = 1,
            toughnessBonus = 1,
            filter = GroupFilter(
                GameObjectFilter.ArtifactCreature.youControl(),
                excludeSelf = true
            )
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "225"
        artist = "Daniel Ljunggren"
        flavorText = "The foundries of Kaladesh run like clockwork under the supervision of their formidable overseers."
        imageUri = "https://cards.scryfall.io/normal/front/f/2/f2c65947-bc1d-4f47-b60b-2f76ab5ebde9.jpg?1783938311"
    }
}
