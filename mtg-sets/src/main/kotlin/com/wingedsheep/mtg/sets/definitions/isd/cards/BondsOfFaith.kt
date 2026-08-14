package com.wingedsheep.mtg.sets.definitions.isd.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantAttack
import com.wingedsheep.sdk.scripting.CantBlock
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.conditions.EnchantedCreatureHasSubtype
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Bonds of Faith
 * {1}{W}
 * Enchantment — Aura
 * Enchant creature
 * Enchanted creature gets +2/+2 as long as it's a Human. Otherwise, it can't attack or block.
 */
val BondsOfFaith = card("Bonds of Faith") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment — Aura"
    oracleText =
        "Enchant creature\n" +
            "Enchanted creature gets +2/+2 as long as it's a Human. Otherwise, it can't attack or block."

    auraTarget = Targets.Creature

    staticAbility {
        ability = ModifyStats(2, 2, GroupFilter.attachedCreature())
        condition = EnchantedCreatureHasSubtype(Subtype.HUMAN)
    }

    staticAbility {
        ability = CantAttack(filter = GroupFilter.attachedCreature())
        condition = Conditions.Not(EnchantedCreatureHasSubtype(Subtype.HUMAN))
    }

    staticAbility {
        ability = CantBlock(filter = GroupFilter.attachedCreature())
        condition = Conditions.Not(EnchantedCreatureHasSubtype(Subtype.HUMAN))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "5"
        artist = "Steve Argyle"
        flavorText = "\"What cannot be destroyed will be bound.\"\n—Oath of Avacyn"
        imageUri =
            "https://cards.scryfall.io/normal/front/c/c/cc8d1ce0-78c5-4e97-9cca-33e7b6ff3440.jpg?1562835456"
    }
}
