package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Wizened Cenn
 * {W}{W}
 * Creature — Kithkin Cleric
 * 2/2
 * Other Kithkin creatures you control get +1/+1.
 */
val WizenedCenn = card("Wizened Cenn") {
    manaCost = "{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Kithkin Cleric"
    power = 2
    toughness = 2
    oracleText = "Other Kithkin creatures you control get +1/+1."

    staticAbility {
        ability = ModifyStats(
            powerBonus = 1,
            toughnessBonus = 1,
            filter = GroupFilter(
                GameObjectFilter.Creature.withSubtype(Subtype.KITHKIN).youControl(),
                excludeSelf = true
            )
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "49"
        artist = "Kev Walker"
        flavorText = "\"Thoughtweft binds us together as one, part of an intricate pattern that would unravel if even one thread came loose.\""
        imageUri = "https://cards.scryfall.io/normal/front/5/8/585f1c8e-6898-4def-8e3f-d45cd263f776.jpg?1783942908"
    }
}
