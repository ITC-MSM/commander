package com.wingedsheep.mtg.sets.definitions.m19.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Goblin Trashmaster
 * {2}{R}{R}
 * Creature — Goblin Warrior
 * 3/3
 *
 * Other Goblins you control get +1/+1.
 * Sacrifice a Goblin: Destroy target artifact.
 *
 * The anthem is a layer-7c [ModifyStats] over *Goblin permanents* you control — the printed line
 * says "Goblins", not "Goblin creatures", so the affected set is every Goblin permanent (a Goblin
 * noncreature artifact would still be pumped, and would matter the moment one is animated).
 * `excludeSelf` carries the "Other".
 *
 * The activation cost is an unrestricted "Sacrifice a Goblin" — Goblin Trashmaster itself is a
 * legal sacrifice, so the cost filter is deliberately *not* `SacrificeAnother`.
 */
val GoblinTrashmaster = card("Goblin Trashmaster") {
    manaCost = "{2}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Goblin Warrior"
    power = 3
    toughness = 3
    oracleText = "Other Goblins you control get +1/+1.\n" +
        "Sacrifice a Goblin: Destroy target artifact."

    staticAbility {
        ability = ModifyStats(
            powerBonus = 1,
            toughnessBonus = 1,
            filter = GroupFilter(
                GameObjectFilter.Permanent.withSubtype(Subtype.GOBLIN).youControl(),
                excludeSelf = true
            )
        )
    }

    activatedAbility {
        cost = Costs.Sacrifice(GameObjectFilter.Permanent.withSubtype(Subtype.GOBLIN))
        val artifact = target("target", Targets.Artifact)
        effect = Effects.Destroy(artifact)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "144"
        artist = "Jakub Kasper"
        flavorText = "\"Folks 'round here are too in love with their contraptions. Does them some good if we smash one every so often.\""
        imageUri = "https://cards.scryfall.io/normal/front/2/b/2bc69988-3c2d-4b76-a8c0-05926b9bbd08.jpg"
    }
}
