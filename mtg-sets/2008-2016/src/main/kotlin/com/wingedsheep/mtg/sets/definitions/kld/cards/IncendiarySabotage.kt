package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Incendiary Sabotage
 * {2}{R}{R}
 * Instant
 * As an additional cost to cast this spell, sacrifice an artifact.
 * Incendiary Sabotage deals 3 damage to each creature.
 *
 * "Each creature" is [Effects.ForEachInGroup] over an unrestricted creature [GroupFilter], with
 * the body pointed at [EffectTarget.Self] — the iterated permanent, not the spell's source.
 */
val IncendiarySabotage = card("Incendiary Sabotage") {
    manaCost = "{2}{R}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "As an additional cost to cast this spell, sacrifice an artifact.\n" +
        "Incendiary Sabotage deals 3 damage to each creature."

    additionalCost(Costs.additional.SacrificePermanent(GameObjectFilter.Artifact))

    spell {
        effect = Effects.ForEachInGroup(
            GroupFilter(GameObjectFilter.Creature),
            Effects.DealDamage(3, EffectTarget.Self)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "119"
        artist = "Ben Maier"
        flavorText = "\"All of those Consulate safety measures undone with one simple tweak.\""
        imageUri = "https://cards.scryfall.io/normal/front/0/e/0ee44ca0-1989-42fa-8024-b6b3e5c3883c.jpg?1783937193"
    }
}
