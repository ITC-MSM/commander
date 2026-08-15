package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.AddCountersEffect
import com.wingedsheep.sdk.scripting.effects.OptionalCostEffect
import com.wingedsheep.sdk.scripting.effects.SacrificeEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Vengeful Villagers
 * {3}{W}
 * Creature — Human Citizen
 * 3/3
 *
 * Whenever this creature attacks, choose target creature an opponent controls. Tap it, then
 * you may sacrifice an artifact or creature. If you do, put a stun counter on the chosen
 * creature. (If a permanent with a stun counter would become untapped, remove one from it
 * instead.)
 *
 * Attack trigger that targets an opponent's creature, taps it, then offers an optional
 * resolution-time sacrifice. The sacrifice is modeled with `OptionalCostEffect`
 * (`Gate.MayPay`) so declining (or having nothing to sacrifice) skips the stun counter —
 * matching the "you may … if you do" wording.
 */
val VengefulVillagers = card("Vengeful Villagers") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Citizen"
    power = 3
    toughness = 3
    oracleText = "Whenever this creature attacks, choose target creature an opponent controls. Tap it, " +
        "then you may sacrifice an artifact or creature. If you do, put a stun counter on the chosen " +
        "creature. (If a permanent with a stun counter would become untapped, remove one from it instead.)"

    triggeredAbility {
        trigger = Triggers.Attacks
        val chosen = target(
            "chosen creature",
            TargetPermanent(filter = TargetFilter(GameObjectFilter.Creature.opponentControls()))
        )
        effect = Effects.Composite(
            Effects.Tap(chosen),
            OptionalCostEffect(
                cost = SacrificeEffect(
                    filter = GameObjectFilter.Artifact.or(GameObjectFilter.Creature),
                    count = 1
                ),
                ifPaid = AddCountersEffect(counterType = Counters.STUN, count = 1, target = chosen)
            )
        )
        description = "Whenever this creature attacks, choose target creature an opponent controls. " +
            "Tap it, then you may sacrifice an artifact or creature. If you do, put a stun counter on it."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "40"
        artist = "Ittoku"
        imageUri = "https://cards.scryfall.io/normal/front/8/c/8cabb6ed-5c80-4dab-b96c-9d4d9fe72db7.jpg?1764120161"
    }
}
