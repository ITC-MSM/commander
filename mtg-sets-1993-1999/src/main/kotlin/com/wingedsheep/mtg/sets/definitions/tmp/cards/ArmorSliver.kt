package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityId
import com.wingedsheep.sdk.scripting.ActivatedAbility
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantActivatedAbility
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Armor Sliver
 * {2}{W}
 * Creature — Sliver
 * 2/2
 * All Sliver creatures have "{2}: This creature gets +0/+1 until end of turn."
 */
val ArmorSliver = card("Armor Sliver") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Sliver"
    power = 2
    toughness = 2
    oracleText = "All Sliver creatures have \"{2}: This creature gets +0/+1 until end of turn.\""

    val sliverFilter = GroupFilter(GameObjectFilter.Creature.withSubtype("Sliver"))

    staticAbility {
        ability = GrantActivatedAbility(
            ability = ActivatedAbility(
                id = AbilityId.generate(),
                cost = Costs.Mana("{2}"),
                effect = Effects.ModifyStats(0, 1, EffectTarget.Self)
            ),
            filter = sliverFilter
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "4"
        artist = "Scott Kirschner"
        flavorText = "Hanna: \"We must learn how they protect each other.\"\nMirri: \"After they're done trying to kill us, all right?\""
        imageUri = "https://cards.scryfall.io/normal/front/c/2/c275aba7-cac6-48e8-b12c-6bd77a5c38fe.jpg?1708088318"
    }
}
