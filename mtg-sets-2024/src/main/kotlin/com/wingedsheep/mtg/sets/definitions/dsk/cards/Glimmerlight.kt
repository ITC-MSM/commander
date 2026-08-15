package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * Glimmerlight
 * {2}
 * Artifact — Equipment
 *
 * When this Equipment enters, create a 1/1 white Glimmer enchantment creature token.
 * Equipped creature gets +1/+1.
 * Equip {1}
 */
val Glimmerlight = card("Glimmerlight") {
    manaCost = "{2}"
    colorIdentity = ""
    typeLine = "Artifact — Equipment"
    oracleText = "When this Equipment enters, create a 1/1 white Glimmer enchantment creature token.\n" +
        "Equipped creature gets +1/+1.\n" +
        "Equip {1} ({1}: Attach to target creature you control. Equip only as a sorcery.)"

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Glimmer"),
            enchantmentToken = true,
            imageUri = "https://cards.scryfall.io/normal/front/4/7/475c7449-2c95-4873-94de-68a5e06cdfb8.jpg?1754930946"
        )
    }

    staticAbility {
        ability = ModifyStats(1, 1)
    }

    equipAbility("{1}")

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "249"
        artist = "Wero Gallo"
        flavorText = "Hope and reliable lighting are in short supply in the House. Glimmers provide both."
        imageUri = "https://cards.scryfall.io/normal/front/1/0/1071691c-5c65-42d4-ac96-d302185ca678.jpg?1726286804"
    }
}
