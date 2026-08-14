package com.wingedsheep.mtg.sets.definitions.isd.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Sharpened Pitchfork
 * {2}
 * Artifact — Equipment
 * Equipped creature has first strike.
 * As long as equipped creature is a Human, it gets +1/+1.
 * Equip {1}
 */
val SharpenedPitchfork = card("Sharpened Pitchfork") {
    manaCost = "{2}"
    colorIdentity = ""
    typeLine = "Artifact — Equipment"
    oracleText =
        "Equipped creature has first strike.\n" +
            "As long as equipped creature is a Human, it gets +1/+1.\n" +
            "Equip {1}"

    staticAbility {
        ability = GrantKeyword(Keyword.FIRST_STRIKE, Filters.EquippedCreature)
    }
    staticAbility {
        condition = Conditions.EntityMatches(
            EffectTarget.EquippedCreature,
            GameObjectFilter.Creature.withSubtype(Subtype.HUMAN),
        )
        ability = ModifyStats(1, 1, Filters.EquippedCreature)
    }
    equipAbility("{1}")

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "232"
        artist = "Winona Nelson"
        flavorText = "Not everyone can have a sword of blessed silver. Not everyone needs one, either."
        imageUri =
            "https://cards.scryfall.io/normal/front/4/c/4ce20f19-a159-40e6-bb67-6108872ac1e0.jpg?1782714678"
    }
}
