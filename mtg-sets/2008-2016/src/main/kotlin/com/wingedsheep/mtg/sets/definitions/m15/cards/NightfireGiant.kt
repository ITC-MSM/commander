package com.wingedsheep.mtg.sets.definitions.m15.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.conditions.Exists
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Nightfire Giant
 * {4}{B}
 * Creature — Zombie Giant
 * 4/3
 * This creature gets +1/+1 as long as you control a Mountain.
 * {4}{R}: This creature deals 2 damage to any target.
 */
val NightfireGiant = card("Nightfire Giant") {
    manaCost = "{4}{B}"
    colorIdentity = "BR"
    typeLine = "Creature — Zombie Giant"
    power = 4
    toughness = 3
    oracleText =
        "This creature gets +1/+1 as long as you control a Mountain.\n" +
        "{4}{R}: This creature deals 2 damage to any target."

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = ModifyStats(+1, +1, GroupFilter.source()),
            condition = Exists(Player.You, Zone.BATTLEFIELD, Filters.MountainCard)
        )
    }

    activatedAbility {
        cost = Costs.Mana("{4}{R}")
        val victim = target("any target", Targets.Any)
        effect = Effects.DealDamage(2, victim)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "109"
        artist = "Dave Kendall"
        flavorText = "Nightfire turns the greatest weakness of the undead into formidable strength."
        imageUri = "https://cards.scryfall.io/normal/front/1/1/11da48a7-903d-43f5-8085-1b3790ed079a.jpg?1783939182"
    }
}
