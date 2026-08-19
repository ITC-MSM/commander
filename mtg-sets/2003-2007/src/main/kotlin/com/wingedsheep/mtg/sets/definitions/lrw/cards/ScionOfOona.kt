package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Scion of Oona
 * {2}{U}
 * Creature — Faerie Soldier
 * 1/1
 * Flash
 * Flying
 * Other Faerie creatures you control get +1/+1.
 * Other Faeries you control have shroud.
 *
 * Two separate printed abilities with two different scopes: the pump names Faerie *creatures*, the
 * shroud grant names Faeries — any Faerie permanent. Both exclude Scion itself, which is why it can
 * still be targeted (and why a second Scion protects the first).
 */
val ScionOfOona = card("Scion of Oona") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Faerie Soldier"
    power = 1
    toughness = 1
    oracleText = "Flash\nFlying\nOther Faerie creatures you control get +1/+1.\nOther Faeries you " +
        "control have shroud. (They can't be the targets of spells or abilities.)"

    keywords(Keyword.FLASH, Keyword.FLYING)

    staticAbility {
        ability = ModifyStats(
            powerBonus = 1,
            toughnessBonus = 1,
            filter = GroupFilter(
                GameObjectFilter.Creature.withSubtype(Subtype.FAERIE).youControl(),
                excludeSelf = true
            )
        )
    }

    staticAbility {
        ability = GrantKeyword(
            Keyword.SHROUD,
            GroupFilter(
                GameObjectFilter.Permanent.withSubtype(Subtype.FAERIE).youControl(),
                excludeSelf = true
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "83"
        artist = "Eric Fortune"
        imageUri = "https://cards.scryfall.io/normal/front/f/7/f75fc1eb-09ac-4800-a55d-c0e723f5786b.jpg?1783942898"
    }
}
