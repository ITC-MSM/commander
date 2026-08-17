package com.wingedsheep.mtg.sets.definitions.clb.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword

/**
 * Cloak of the Bat
 * {2}
 * Artifact — Equipment
 * Equipped creature has flying and haste.
 * Equip {2} ({2}: Attach to target creature you control. Equip only as a sorcery.)
 *
 * Vanilla Equipment: [equipAbility] lowers the printed equip line into the flagged, sorcery-speed
 * attach ability, and the grant is two [GrantKeyword] statics over [Filters.EquippedCreature]
 * because a single [GrantKeyword] holds exactly one keyword.
 */
val CloakOfTheBat = card("Cloak of the Bat") {
    manaCost = "{2}"
    colorIdentity = ""
    typeLine = "Artifact — Equipment"
    oracleText = "Equipped creature has flying and haste.\n" +
        "Equip {2} ({2}: Attach to target creature you control. Equip only as a sorcery.)"

    staticAbility {
        ability = GrantKeyword(Keyword.FLYING, Filters.EquippedCreature)
    }
    staticAbility {
        ability = GrantKeyword(Keyword.HASTE, Filters.EquippedCreature)
    }

    equipAbility("{2}")

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "307"
        artist = "Dominik Mayer"
        flavorText = "\"How about the power of silent flight? Does that suit your needs, cutthroat?\"\n—Rivalen Blackhand, proprietor of Sorcerous Sundries"
        imageUri = "https://cards.scryfall.io/normal/front/2/f/2f508a65-ff32-480a-b9c6-075074d0c3c3.jpg?1783922679"
    }
}
