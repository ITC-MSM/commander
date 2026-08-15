package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Mogg Fanatic
 * {R}
 * Creature — Goblin (1/1)
 *
 * Sacrifice this creature: It deals 1 damage to any target.
 */
val MoggFanatic = card("Mogg Fanatic") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Creature — Goblin"
    power = 1
    toughness = 1
    oracleText = "Sacrifice this creature: It deals 1 damage to any target."

    activatedAbility {
        cost = Costs.SacrificeSelf
        val t = target("target", Targets.Any)
        effect = Effects.DealDamage(1, t)
        description = "Sacrifice this creature: It deals 1 damage to any target."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "190"
        artist = "Brom"
        flavorText = "\"I got it! I got it! I—\""
        imageUri = "https://cards.scryfall.io/normal/front/c/a/ca2ecfd4-c874-4468-8601-87aa110d5a00.jpg?1562056411"
    }
}
