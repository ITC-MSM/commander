package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * The enters trigger reads [DynamicAmount.CastX] rather than `XValue`: the X belongs to the spell
 * that became this enchantment, and only the durable CastX reading survives onto the permanent
 * (same shape as Lost in the Maze).
 */
val AjanisAnguish = card("Ajani's Anguish") {
    manaCost = "{X}{R}"
    colorIdentity = "R"
    typeLine = "Enchantment"
    oracleText = "When this enchantment enters, it deals X damage to any target.\n" +
        "Creatures you control have trample."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val t = target("any target", Targets.Any)
        effect = Effects.DealDamage(DynamicAmount.CastX, t)
    }

    staticAbility {
        ability = GrantKeyword(Keyword.TRAMPLE, GroupFilter(GameObjectFilter.Creature.youControl()))
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "72"
        artist = "Mathias Kollros"
        flavorText = "The memory trap tormented Ajani. \"Time's up, brother,\" said Jazal's voice inside his head. \"Your vengeance, or your friends. What will it be?\""
        imageUri = "https://cards.scryfall.io/normal/front/d/9/d9039a58-2f17-4b8a-b714-3a2f0b46f057.jpg?1789470674"
        inBooster = false
    }
}
