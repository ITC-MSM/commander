package com.wingedsheep.mtg.sets.definitions.som.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Strider Harness — Scars of Mirrodin #207
 * {3} · Artifact — Equipment
 *
 * Equipped creature gets +1/+1 and has haste.
 * Equip {1} ({1}: Attach to target creature you control. Equip only as a sorcery.)
 *
 * The pump and the keyword are two separate statics over [GroupFilter.attachedCreature]; the equip
 * ability is the sorcery-speed attach of CR 702.6.
 */
val StriderHarness = card("Strider Harness") {
    manaCost = "{3}"
    colorIdentity = ""
    typeLine = "Artifact — Equipment"
    oracleText = "Equipped creature gets +1/+1 and has haste.\n" +
        "Equip {1} ({1}: Attach to target creature you control. Equip only as a sorcery.)"

    staticAbility {
        ability = ModifyStats(1, 1, GroupFilter.attachedCreature())
    }

    staticAbility {
        ability = GrantKeyword(Keyword.HASTE, GroupFilter.attachedCreature())
    }

    equipAbility("{1}")

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "207"
        artist = "Matt Stewart"
        flavorText = "Each journey begins with a single step—and sometimes ends with that single step as well."
        imageUri = "https://cards.scryfall.io/normal/front/9/d/9d7b9e54-b3ef-44fb-9240-0d67c1c4b7f6.jpg?1783941695"
    }
}
