package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivationRestriction
import com.wingedsheep.sdk.scripting.CantAttack
import com.wingedsheep.sdk.scripting.CantBlock
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Path to Redemption
 * {1}{W}
 * Enchantment — Aura
 * Enchant creature
 * Enchanted creature can't attack or block.
 * {5}, Sacrifice this Aura: Exile enchanted creature. Create a 1/1 white Ally
 *   creature token. Activate only during your turn.
 */
val PathToRedemption = card("Path to Redemption") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\nEnchanted creature can't attack or block.\n{5}, Sacrifice this Aura: Exile enchanted creature. Create a 1/1 white Ally creature token. Activate only during your turn."

    auraTarget = Targets.Creature

    staticAbility {
        ability = CantAttack(filter = GroupFilter.attachedCreature())
    }

    staticAbility {
        ability = CantBlock(filter = GroupFilter.attachedCreature())
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{5}"), Costs.SacrificeSelf)
        effect = Effects.Composite(
            Effects.Exile(EffectTarget.EnchantedCreature),
            Effects.CreateToken(
                power = 1,
                toughness = 1,
                colors = setOf(Color.WHITE),
                creatureTypes = setOf("Ally")
            )
        )
        restrictions = listOf(ActivationRestriction.OnlyDuringYourTurn)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "31"
        artist = "Hokyoung Kim"
        flavorText = "\"You can't always see the light at the end of the tunnel, but if you just keep moving you will come to a better place.\""
        imageUri = "https://cards.scryfall.io/normal/front/f/9/f936d64a-0db4-49c1-8a57-6d99e012a555.jpg?1764120097"
    }
}
