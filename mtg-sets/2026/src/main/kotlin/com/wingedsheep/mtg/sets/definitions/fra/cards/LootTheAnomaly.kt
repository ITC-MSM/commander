package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivationRestriction
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Loot, the Anomaly — Reality Fracture #232
 * {2}{B} · Legendary Creature — Beast Horror · -2/4
 *
 * If Loot's power is negative, he assigns combat damage as though his power were positive.
 * Threshold — Sacrifice another creature or planeswalker: Loot gets -2/-0 until end of turn.
 * Activate only if there are seven or more cards in your graveyard.
 *
 * Printed with a *negative* base power (CR 107.1b). The first ability is
 * [AbilityFlag.ASSIGNS_COMBAT_DAMAGE_AS_ABSOLUTE_POWER]: CR 510.1a would have a -2 power creature
 * assign no combat damage, the flag makes it assign 2 — and each threshold activation pushes the
 * power further down, so Loot hits *harder* (-4 assigns 4, -6 assigns 6). His power stays
 * negative for every other purpose.
 *
 * "Threshold" is an ability word; the gate is an [ActivationRestriction.OnlyIfCondition] on the
 * graveyard count, checked when activating.
 */
val LootTheAnomaly = card("Loot, the Anomaly") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Legendary Creature — Beast Horror"
    power = -2
    toughness = 4
    oracleText = "If Loot's power is negative, he assigns combat damage as though his power were positive.\n" +
        "Threshold — Sacrifice another creature or planeswalker: Loot gets -2/-0 until end of turn. " +
        "Activate only if there are seven or more cards in your graveyard."

    flags(AbilityFlag.ASSIGNS_COMBAT_DAMAGE_AS_ABSOLUTE_POWER)

    activatedAbility {
        cost = Costs.SacrificeAnother(GameObjectFilter.CreatureOrPlaneswalker)
        effect = Effects.ModifyStats(-2, 0, EffectTarget.Self)
        restrictions = listOf(
            ActivationRestriction.OnlyIfCondition(Conditions.CardsInGraveyardAtLeast(7))
        )
        description = "Threshold — Sacrifice another creature or planeswalker: Loot gets -2/-0 until end of turn."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "232"
        artist = "Filip Burburan"
        flavorText = "Loot's value is absolute in any Multiverse, so Jace kept him locked away and hidden."
        imageUri = "https://cards.scryfall.io/normal/front/4/f/4f6fd2fa-8bc8-4743-bbc8-b56475d64eff.jpg?1789568430"
        inBooster = false
    }
}
