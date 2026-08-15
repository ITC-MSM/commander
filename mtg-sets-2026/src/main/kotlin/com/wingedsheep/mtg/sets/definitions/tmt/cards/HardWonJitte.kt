package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword

/**
 * Hard-Won Jitte
 * {1}{R}
 * Artifact — Equipment
 *
 * Equipped creature has double strike.
 * Equip {2}
 */
val HardWonJitte = card("Hard-Won Jitte") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Artifact — Equipment"
    oracleText = "Equipped creature has double strike.\nEquip {2} ({2}: Attach to target creature you control. Equip only as a sorcery.)"

    staticAbility {
        ability = GrantKeyword(Keyword.DOUBLE_STRIKE, Filters.EquippedCreature)
    }

    equipAbility("{2}")

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "91"
        artist = "Kim Sokol"
        flavorText = "It was fitting that the most dangerous turtle bore a weapon of peace."
        imageUri = "https://cards.scryfall.io/normal/front/5/d/5d3dc219-f024-49de-b88d-dc1e9e84184c.jpg?1771342392"
    }
}
