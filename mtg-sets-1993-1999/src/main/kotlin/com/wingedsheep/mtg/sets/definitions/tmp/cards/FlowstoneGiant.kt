package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Flowstone Giant
 * {2}{R}{R}
 * Creature — Giant (3/3)
 *
 * {R}: This creature gets +2/-2 until end of turn.
 */
val FlowstoneGiant = card("Flowstone Giant") {
    manaCost = "{2}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Giant"
    power = 3
    toughness = 3
    oracleText = "{R}: This creature gets +2/-2 until end of turn."

    activatedAbility {
        cost = Costs.Mana("{R}")
        effect = Effects.ModifyStats(2, -2, EffectTarget.Self)
        description = "{R}: This creature gets +2/-2 until end of turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "174"
        artist = "Joel Biske"
        flavorText = "When the first of these giants woke from the bedrock, he was still sleepy. " +
            "He yawned and stretched until his legs grew so thin that they snapped like icicles in the sun.\n—Vec lore"
        imageUri = "https://cards.scryfall.io/normal/front/4/6/46e8240a-d882-4f60-8960-1856284e04a0.jpg?1562053769"
    }
}
