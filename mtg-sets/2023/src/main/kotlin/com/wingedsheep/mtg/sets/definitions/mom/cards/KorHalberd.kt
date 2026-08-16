package com.wingedsheep.mtg.sets.definitions.mom.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * Kor Halberd
 * {W}
 * Artifact — Equipment
 * Equipped creature gets +1/+1 and has vigilance.
 * Equip {1}
 */
val KorHalberd = card("Kor Halberd") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Artifact — Equipment"
    oracleText = "Equipped creature gets +1/+1 and has vigilance.\n" +
        "Equip {1} ({1}: Attach to target creature you control. Equip only as a sorcery.)"

    staticAbility {
        ability = ModifyStats(1, 1, Filters.EquippedCreature)
    }

    staticAbility {
        ability = GrantKeyword(Keyword.VIGILANCE, Filters.EquippedCreature)
    }

    equipAbility("{1}")

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "27"
        artist = "Bastien L. Deharme"
        flavorText = "An ancient weapon for a modern war."
        imageUri = "https://cards.scryfall.io/normal/front/a/4/a4705327-ada6-4575-82fc-351e183d060e.jpg?1783917057"
    }
}
