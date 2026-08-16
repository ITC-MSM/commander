package com.wingedsheep.mtg.sets.definitions.akh.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Liliana's Mastery
 * {3}{B}{B}
 * Enchantment
 * Zombies you control get +1/+1.
 * When this enchantment enters, create two 2/2 black Zombie creature tokens.
 *
 * The anthem is not "other" — Liliana's Mastery is an Enchantment, never a Zombie, so the group has
 * no self to exclude and the two tokens it makes are pumped by it.
 */
val LilianasMastery = card("Liliana's Mastery") {
    manaCost = "{3}{B}{B}"
    colorIdentity = "B"
    typeLine = "Enchantment"
    oracleText = "Zombies you control get +1/+1.\nWhen this enchantment enters, create two 2/2 black Zombie creature tokens."

    staticAbility {
        ability = ModifyStats(
            powerBonus = 1,
            toughnessBonus = 1,
            filter = GroupFilter(
                GameObjectFilter.Permanent.withSubtype(Subtype.ZOMBIE).youControl()
            )
        )
    }

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateToken(
            power = 2,
            toughness = 2,
            colors = setOf(Color.BLACK),
            creatureTypes = setOf("Zombie"),
            count = 2,
            imageUri = "https://cards.scryfall.io/normal/front/b/5/b5bd6905-79be-4d2c-a343-f6e6a181b3e6.jpg?1783936411"
        )
        description = "When this enchantment enters, create two 2/2 black Zombie creature tokens."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "98"
        artist = "Kieran Yanner"
        flavorText = "\"There are so many of them. It seems they've just been waiting for someone to serve.\""
        imageUri = "https://cards.scryfall.io/normal/front/5/c/5c57b3b8-71f1-479c-b7f7-508fee2b5b0f.jpg?1783936503"
    }
}
