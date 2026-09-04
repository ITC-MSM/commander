package com.wingedsheep.mtg.sets.definitions.roe.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Flame Slash
 * {R}
 * Sorcery
 *
 * Flame Slash deals 4 damage to target creature.
 *
 * Modeling notes:
 *  - The plainest burn shape there is: one cast-time creature target, one fixed-amount
 *    [Effects.DealDamage] pointed at it. Assay compiles exactly this — a `DealDamage` with a
 *    `Fixed` amount of 4 over a single `TargetObject` whose only predicate is `IsCreature` — so
 *    `Targets.Creature` is the filter, with no "you control" or other scoping the card doesn't print.
 *  - The damage source is left implicit (the spell itself), which is what the printed
 *    "Flame Slash deals" means; no `damageSource` override is needed.
 */
val FlameSlash = card("Flame Slash") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Flame Slash deals 4 damage to target creature."

    spell {
        val creature = target("target creature", Targets.Creature)
        effect = Effects.DealDamage(4, creature)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "145"
        artist = "Raymond Swanland"
        flavorText = "After millennia asleep, the Eldrazi had forgotten about Zendikar's fiery temper and dislike of strangers."
        imageUri = "https://cards.scryfall.io/normal/front/0/0/006d2bf1-20f7-4b09-8d98-8233d91682bd.jpg?1783941976"
    }
}
