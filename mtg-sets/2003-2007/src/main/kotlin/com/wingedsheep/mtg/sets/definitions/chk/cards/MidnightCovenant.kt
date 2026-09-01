package com.wingedsheep.mtg.sets.definitions.chk.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityId
import com.wingedsheep.sdk.scripting.ActivatedAbility
import com.wingedsheep.sdk.scripting.GrantActivatedAbility
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Midnight Covenant
 * {1}{B}
 * Enchantment — Aura
 *
 * Enchant creature
 * Enchanted creature has "{B}: This creature gets +1/+1 until end of turn."
 *
 * "Enchant creature" is the builder's `auraTarget`. The granted ability rides
 * [GrantActivatedAbility], whose `filter` defaults to `GroupFilter.attachedCreature()` — exactly the
 * "enchanted creature" scope this card wants, so no filter is written. Inside the granted ability
 * [EffectTarget.Self] is the creature that activated it, which is what the quoted "this creature"
 * means once the ability has moved onto the enchanted permanent.
 */
val MidnightCovenant = card("Midnight Covenant") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\n" +
        "Enchanted creature has \"{B}: This creature gets +1/+1 until end of turn.\""

    auraTarget = Targets.Creature

    staticAbility {
        ability = GrantActivatedAbility(
            ability = ActivatedAbility(
                id = AbilityId.generate(),
                cost = Costs.Mana("{B}"),
                effect = Effects.ModifyStats(1, 1, EffectTarget.Self)
            )
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "125"
        artist = "Pete Venters"
        flavorText = "\"Not all mortals fought the kami. The ogres revered the oni, while some " +
            "humans made pacts based upon empty promises.\"\n—*The History of Kamigawa*"
        imageUri = "https://cards.scryfall.io/normal/front/5/d/5d797e91-3fc7-4f62-8cd8-dbd5f445e69c.jpg?1783944312"
    }
}
