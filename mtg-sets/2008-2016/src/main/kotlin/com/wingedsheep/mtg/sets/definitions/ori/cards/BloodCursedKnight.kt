package com.wingedsheep.mtg.sets.definitions.ori.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Blood-Cursed Knight
 * {1}{W}{B}
 * Creature — Vampire Knight
 * 3/2
 *
 * As long as you control an enchantment, this creature gets +1/+1 and has lifelink. (Damage dealt by this creature also causes you to gain that much life.)
 *
 * One printed sentence, two continuous modifications, so it is two `staticAbility { }` blocks —
 * a `StaticAbility` is one modification and the builder takes one per block. Both carry the same
 * [Conditions.YouControl] gate and [GroupFilter.source], the self-scoped group, so each applies
 * only to this creature.
 */
val BloodCursedKnight = card("Blood-Cursed Knight") {
    manaCost = "{1}{W}{B}"
    colorIdentity = "WB"
    typeLine = "Creature — Vampire Knight"
    oracleText = "As long as you control an enchantment, this creature gets +1/+1 and has lifelink. (Damage dealt by this creature also causes you to gain that much life.)"
    power = 3
    toughness = 2

    staticAbility {
        condition = Conditions.YouControl(GameObjectFilter.Enchantment)
        ability = ModifyStats(powerBonus = 1, toughnessBonus = 1, filter = GroupFilter.source())
    }

    staticAbility {
        condition = Conditions.YouControl(GameObjectFilter.Enchantment)
        ability = GrantKeyword(keyword = Keyword.LIFELINK, filter = GroupFilter.source())
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "211"
        artist = "Winona Nelson"
        flavorText = "\"The bloodlust shall not control me, for my oath is my greatest compulsion.\""
        imageUri = "https://cards.scryfall.io/normal/front/6/4/644bb558-113e-4e49-a395-7e0036c3419c.jpg"
    }
}
