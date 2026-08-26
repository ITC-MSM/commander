package com.wingedsheep.mtg.sets.definitions.ths.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword

/**
 * Fleetfeather Sandals
 * {2}
 * Artifact — Equipment
 *
 * Equipped creature has flying and haste.
 * Equip {2} ({2}: Attach to target creature you control. Equip only as a sorcery.)
 */
val FleetfeatherSandals = card("Fleetfeather Sandals") {
    manaCost = "{2}"
    colorIdentity = ""
    typeLine = "Artifact — Equipment"
    oracleText = "Equipped creature has flying and haste.\nEquip {2} ({2}: Attach to target creature you control. Equip only as a sorcery.)"

    staticAbility {
        ability = GrantKeyword(Keyword.FLYING)
    }

    staticAbility {
        ability = GrantKeyword(Keyword.HASTE)
    }

    equipAbility("{2}")

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "216"
        artist = "Steve Prescott"
        flavorText = "\"The gods gave us no wings to fly, but they gave us an even greater gift: imagination.\"\n—Daxos of Meletis"
        imageUri = "https://cards.scryfall.io/normal/front/2/2/2222f499-09f9-45a6-8255-9de79df76f1c.jpg"
    }
}
