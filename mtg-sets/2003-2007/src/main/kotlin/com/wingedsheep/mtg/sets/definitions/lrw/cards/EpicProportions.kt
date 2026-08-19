package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CompositeStaticAbility
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Epic Proportions
 * {4}{G}{G}
 * Enchantment — Aura
 * Flash
 * Enchant creature
 * Enchanted creature gets +5/+5 and has trample.
 *
 * One printed ability across two layers (P/T and keywords), so the grants share a
 * [CompositeStaticAbility] identity per CR 613.6.
 */
val EpicProportions = card("Epic Proportions") {
    manaCost = "{4}{G}{G}"
    colorIdentity = "G"
    typeLine = "Enchantment — Aura"
    oracleText = "Flash\nEnchant creature\nEnchanted creature gets +5/+5 and has trample."

    keywords(Keyword.FLASH)

    auraTarget = Targets.Creature

    staticAbility {
        ability = CompositeStaticAbility(
            listOf(
                ModifyStats(
                    powerBonus = 5,
                    toughnessBonus = 5,
                    filter = GroupFilter.attachedCreature()
                ),
                GrantKeyword(Keyword.TRAMPLE, GroupFilter.attachedCreature())
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "209"
        artist = "Jesper Ejsing"
        flavorText = "From mite to mighty."
        imageUri = "https://cards.scryfall.io/normal/front/3/d/3d7ffca1-c73c-4de1-b811-bd1876ea6d6f.jpg?1783942867"
    }
}
