package com.wingedsheep.mtg.sets.definitions.snc.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword

/**
 * Gilded Pinions
 * {2}
 * Artifact — Equipment
 * When this Equipment enters, create a Treasure token. (It's an artifact with "{T}, Sacrifice this token: Add one mana of any color.")
 * Equipped creature has flying.
 * Equip {2} ({2}: Attach to target creature you control. Equip only as a sorcery.)
 *
 * The enters trigger is the shared [Effects.CreateTreasure]; the equipped-creature grant is the
 * default-filter [GrantKeyword] (Neurok Hoversail's shape), and `equipAbility("{2}")` lowers
 * CR 702.6a's sorcery-speed attach ability.
 */
val GildedPinions = card("Gilded Pinions") {
    manaCost = "{2}"
    typeLine = "Artifact — Equipment"
    oracleText = "When this Equipment enters, create a Treasure token. (It's an artifact with \"{T}, Sacrifice this token: Add one mana of any color.\")\nEquipped creature has flying.\nEquip {2} ({2}: Attach to target creature you control. Equip only as a sorcery.)"

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateTreasure(1)
        description = "When this Equipment enters, create a Treasure token."
    }

    staticAbility {
        ability = GrantKeyword(Keyword.FLYING)
    }

    equipAbility("{2}")

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "238"
        artist = "Irina Nordsol"
        imageUri = "https://cards.scryfall.io/normal/front/d/c/dca8554a-f74c-4d2d-95b6-ef6e74431d7c.jpg?1783923062"
    }
}
