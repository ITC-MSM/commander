package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * Skateboard
 * {1}
 * Artifact — Equipment
 *
 * When this Equipment enters, tap target permanent.
 * Equipped creature gets +1/+0 and has haste.
 * Equip {1}
 */
val Skateboard = card("Skateboard") {
    manaCost = "{1}"
    typeLine = "Artifact — Equipment"
    oracleText = "When this Equipment enters, tap target permanent.\nEquipped creature gets +1/+0 and has haste.\nEquip {1} ({1}: Attach to target creature you control. Equip only as a sorcery.)"

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val perm = target("target permanent", Targets.Permanent)
        effect = Effects.Tap(perm)
    }

    staticAbility {
        ability = ModifyStats(1, 0, Filters.EquippedCreature)
    }

    staticAbility {
        ability = GrantKeyword(Keyword.HASTE, Filters.EquippedCreature)
    }

    equipAbility("{1}")

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "178"
        artist = "Hokyoung Kim"
        flavorText = "\"Why skate a half-pipe when you can skate a sewer pipe?\""
        imageUri = "https://cards.scryfall.io/normal/front/d/e/deadb6d8-3eea-4261-a07c-8536df89e85c.jpg?1771502813"
    }
}
