package com.wingedsheep.mtg.sets.definitions.som.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.effects.RegenerateEffect

/**
 * Asceticism
 * {3}{G}{G}
 * Enchantment
 *
 * Creatures you control have hexproof.
 * {1}{G}: Regenerate target creature. (The next time it would be destroyed this turn, instead tap it, remove it from combat, and heal all damage on it.)
 */
val Asceticism = card("Asceticism") {
    manaCost = "{3}{G}{G}"
    colorIdentity = "G"
    typeLine = "Enchantment"
    oracleText = "Creatures you control have hexproof.\n" +
        "{1}{G}: Regenerate target creature. (The next time it would be destroyed this turn, instead tap it, remove it from combat, and heal all damage on it.)"

    staticAbility {
        ability = GrantKeyword(Keyword.HEXPROOF, Filters.Group.creaturesYouControl)
    }

    activatedAbility {
        cost = Costs.Mana("{1}{G}")
        val creature = target("target creature", Targets.Creature)
        effect = RegenerateEffect(creature)
        description = "{1}{G}: Regenerate target creature."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "110"
        artist = "Daarken"
        flavorText = "\"Let my ignominy build walls thicker than iron and stronger than darksteel.\"\n—Thrun, the last troll"
        imageUri = "https://cards.scryfall.io/normal/front/e/c/ec2b56b0-126c-411b-8c43-b690fc8c194b.jpg?1783941722"
    }
}
