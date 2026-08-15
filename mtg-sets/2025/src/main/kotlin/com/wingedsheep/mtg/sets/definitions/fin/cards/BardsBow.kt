package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.jobSelect
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.GrantSubtype
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * Bard's Bow
 * {2}{G}
 * Artifact — Equipment
 * Job select (When this Equipment enters, create a 1/1 colorless Hero creature token,
 *   then attach this to it.)
 * Equipped creature gets +2/+2, has reach, and is a Bard in addition to its other types.
 * Perseus's Bow — Equip {6}
 */
val BardsBow = card("Bard's Bow") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Artifact — Equipment"
    oracleText = "Job select (When this Equipment enters, create a 1/1 colorless Hero creature token, then attach this to it.)\n" +
        "Equipped creature gets +2/+2, has reach, and is a Bard in addition to its other types.\n" +
        "Perseus's Bow — Equip {6} ({6}: Attach to target creature you control. Equip only as a sorcery.)"

    jobSelect()

    staticAbility {
        ability = ModifyStats(2, 2, Filters.EquippedCreature)
    }
    staticAbility {
        ability = GrantKeyword(Keyword.REACH, Filters.EquippedCreature)
    }
    staticAbility {
        ability = GrantSubtype("Bard", Filters.EquippedCreature)
    }

    equipAbility("{6}")

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "174"
        artist = "Josephine Chang"
        imageUri = "https://cards.scryfall.io/normal/front/2/a/2ac03e90-1e16-453f-88e4-a0448db73403.jpg?1748706414"
    }
}
