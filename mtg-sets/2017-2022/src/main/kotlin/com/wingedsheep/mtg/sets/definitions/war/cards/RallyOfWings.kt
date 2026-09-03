package com.wingedsheep.mtg.sets.definitions.war.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Rally of Wings
 * {1}{W}
 * Instant
 * Untap all creatures you control. Creatures you control with flying get +2/+2 until end of turn.
 */
val RallyOfWings = card("Rally of Wings") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Untap all creatures you control. Creatures you control with flying get +2/+2 until end of turn."

    spell {
        effect = Effects.Composite(
            Effects.ForEachInGroup(
                GroupFilter(GameObjectFilter.Creature.youControl()),
                Effects.Untap(EffectTarget.Self)
            ),
            Effects.ForEachInGroup(
                GroupFilter(GameObjectFilter.Creature.withKeyword(Keyword.FLYING).youControl()),
                Effects.ModifyStats(2, 2, EffectTarget.Self)
            )
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "27"
        artist = "Magali Villeneuve"
        flavorText = "Even the clouds became a field of battle. Known as the Sky Theater, it saw the Eternals clash with squadrons of angels."
        imageUri = "https://cards.scryfall.io/normal/front/f/0/f043642d-35fe-4ea9-a1d3-78ddfdddeaf4.jpg"
    }
}
