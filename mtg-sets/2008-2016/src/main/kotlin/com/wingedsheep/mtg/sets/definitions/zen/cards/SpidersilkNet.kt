package com.wingedsheep.mtg.sets.definitions.zen.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * Spidersilk Net
 * {0}
 * Artifact — Equipment
 * Equipped creature gets +0/+2 and has reach. (It can block creatures with flying.)
 * Equip {2} ({2}: Attach to target creature you control. Equip only as a sorcery.)
 *
 * A filter-less [ModifyStats]/[GrantKeyword] on an Equipment defaults to the equipped creature.
 */
val SpidersilkNet = card("Spidersilk Net") {
    manaCost = "{0}"
    typeLine = "Artifact — Equipment"
    oracleText = "Equipped creature gets +0/+2 and has reach. (It can block creatures with flying.)\n" +
        "Equip {2} ({2}: Attach to target creature you control. Equip only as a sorcery.)"

    staticAbility {
        ability = ModifyStats(0, 2)
    }

    staticAbility {
        ability = GrantKeyword(Keyword.REACH)
    }

    equipAbility("{2}")

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "206"
        artist = "Zoltan Boros & Gabor Szikszai"
        imageUri = "https://cards.scryfall.io/normal/front/1/f/1f75f13e-43e6-4118-83a3-0446c2089d84.jpg"
    }
}
