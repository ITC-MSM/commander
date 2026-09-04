package com.wingedsheep.mtg.sets.definitions.rix.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Legion Lieutenant
 * {W}{B}
 * Creature — Vampire Knight
 * 2/2
 * Other Vampires you control get +1/+1.
 *
 * A Vampire lord. The filter is [GameObjectFilter.Permanent] rather than `.Creature` because the
 * printed line says "Other Vampires", which reaches a noncreature Vampire permanent too;
 * `excludeSelf = true` carries the printed "Other".
 */
val LegionLieutenant = card("Legion Lieutenant") {
    manaCost = "{W}{B}"
    colorIdentity = "BW"
    typeLine = "Creature — Vampire Knight"
    oracleText = "Other Vampires you control get +1/+1."
    power = 2
    toughness = 2

    staticAbility {
        ability = ModifyStats(
            powerBonus = 1,
            toughnessBonus = 1,
            filter = GroupFilter(
                GameObjectFilter.Permanent.withSubtype(Subtype.VAMPIRE).youControl(),
                excludeSelf = true
            )
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "163"
        artist = "Zezhou Chen"
        flavorText = "\"We long ago abandoned the things that make humans weak: friendship, " +
            "marriage, family. All that remains is the strength of our devotion.\""
        imageUri = "https://cards.scryfall.io/normal/front/3/3/33f7ff99-65d6-4e97-bdfa-b6e6eac0588f.jpg?1783935272"
        ruling(
            "2018-01-19",
            "Because damage remains marked on a creature until it's removed as the turn ends, " +
                "nonlethal damage dealt to another Vampire you control may become lethal if " +
                "Legion Lieutenant leaves the battlefield during that turn."
        )
    }
}
