package com.wingedsheep.mtg.sets.definitions.drk.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Bone Flute
 * {3}
 * Artifact
 * {2}, {T}: All creatures get -1/-0 until end of turn.
 */
val BoneFlute = card("Bone Flute") {
    manaCost = "{3}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "{2}, {T}: All creatures get -1/-0 until end of turn."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}"), Costs.Tap)
        effect = Effects.ForEachInGroup(
            GroupFilter.AllCreatures,
            Effects.ModifyStats(-1, 0, EffectTarget.Self)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "97"
        artist = "Christopher Rush"
        flavorText = "After the Battle of Pitdown, Lady Ursnell fashioned the first such instrument out of Lord Ursnell's left leg."
        imageUri = "https://cards.scryfall.io/normal/front/6/3/63a31de0-d764-4ff6-a85f-027e1e58d86c.jpg?1783947928"

        ruling(
            "2009-10-01",
            "The ability affects only creatures on the battlefield at the time it resolves. A creature that " +
                "enters later in the turn won't get -1/-0."
        )
    }
}
