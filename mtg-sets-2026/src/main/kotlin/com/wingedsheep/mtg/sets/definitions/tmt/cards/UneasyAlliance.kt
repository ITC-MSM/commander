package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantAttack
import com.wingedsheep.sdk.scripting.CantBlock
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.CreateTokenEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Uneasy Alliance
 * {1}{W}
 * Enchantment — Aura
 *
 * Enchant creature
 * Enchanted creature can't attack or block.
 * {5}, Sacrifice this Aura: Exile enchanted creature. You create a
 * 1/1 black Ninja creature token. Activate only as a sorcery.
 */
val UneasyAlliance = card("Uneasy Alliance") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\nEnchanted creature can't attack or block.\n{5}, Sacrifice this Aura: Exile enchanted creature. You create a 1/1 black Ninja creature token. Activate only as a sorcery."

    auraTarget = Targets.Creature

    staticAbility {
        ability = CantAttack(filter = GroupFilter.attachedCreature())
    }

    staticAbility {
        ability = CantBlock(filter = GroupFilter.attachedCreature())
    }

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{5}"),
            Costs.SacrificeSelf
        )
        timing = TimingRule.SorcerySpeed
        effect = Effects.Exile(EffectTarget.EnchantedCreature)
            .then(
                CreateTokenEffect(
                    power = 1,
                    toughness = 1,
                    colors = setOf(Color.BLACK),
                    creatureTypes = setOf("Ninja"),
                    imageUri = "https://cards.scryfall.io/normal/front/a/7/a7b76498-d696-40d1-b7c7-91657525b44f.jpg?1771590477"
                )
            )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "28"
        artist = "Rose Benjamin"
        flavorText = "Other perspectives can help one find the proper direction."
        imageUri = "https://cards.scryfall.io/normal/front/5/d/5d9a4f9a-1e3a-4de8-a7c0-7158c6703b4e.jpg?1771586776"
    }
}
