package com.wingedsheep.mtg.sets.definitions.wwk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * Kitesail
 * {2}
 * Artifact — Equipment
 * Equipped creature gets +1/+0 and has flying.
 * Equip {2} ({2}: Attach to target creature you control. Equip only as a sorcery.)
 */
val Kitesail = card("Kitesail") {
    manaCost = "{2}"
    colorIdentity = ""
    typeLine = "Artifact — Equipment"
    oracleText = "Equipped creature gets +1/+0 and has flying.\nEquip {2} ({2}: Attach to target creature you control. Equip only as a sorcery.)"

    staticAbility {
        ability = ModifyStats(1, 0)
    }
    staticAbility {
        ability = GrantKeyword(Keyword.FLYING)
    }
    equipAbility("{2}")

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "126"
        artist = "Cyril Van Der Haegen"
        flavorText = "Kitesailing is a way of life—and without practice, the end of it."
        imageUri = "https://cards.scryfall.io/normal/front/2/1/217a05a7-557f-4879-8fd1-d6c003f1751e.jpg?1783942039"
    }
}
