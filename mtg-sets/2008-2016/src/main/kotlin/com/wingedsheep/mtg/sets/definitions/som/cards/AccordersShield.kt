package com.wingedsheep.mtg.sets.definitions.som.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Accorder's Shield — Scars of Mirrodin #136
 * {0} · Artifact — Equipment
 *
 * Equipped creature gets +0/+3 and has vigilance. (Attacking doesn't cause it to tap.)
 * Equip {3} ({3}: Attach to target creature you control. Equip only as a sorcery.)
 *
 * Two statics over [GroupFilter.attachedCreature] — the affected set of every "equipped creature
 * …" line — plus the printed equip ability, which `equipAbility` lowers into the sorcery-speed
 * attach that CR 702.6 describes.
 */
val AccordersShield = card("Accorder's Shield") {
    manaCost = "{0}"
    colorIdentity = ""
    typeLine = "Artifact — Equipment"
    oracleText = "Equipped creature gets +0/+3 and has vigilance. (Attacking doesn't cause it to tap.)\n" +
        "Equip {3} ({3}: Attach to target creature you control. Equip only as a sorcery.)"

    staticAbility {
        ability = ModifyStats(0, 3, GroupFilter.attachedCreature())
    }

    staticAbility {
        ability = GrantKeyword(Keyword.VIGILANCE, GroupFilter.attachedCreature())
    }

    equipAbility("{3}")

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "136"
        artist = "Alan Pollack"
        flavorText = "An Auriok shield is polished to a mirror finish even on the inside, enabling its bearer to watch foes ahead and behind."
        imageUri = "https://cards.scryfall.io/normal/front/a/7/a7305c18-5058-42dd-b62a-7f6a42624036.jpg?1783941713"
    }
}
