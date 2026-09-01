package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityId
import com.wingedsheep.sdk.scripting.ActivatedAbility
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.effects.GrantActivatedAbilityEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.AnyTarget
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Flame Fusillade — Ravnica: City of Guilds #123
 * {3}{R} · Sorcery
 *
 * Until end of turn, permanents you control gain "{T}: This permanent deals 1 damage to any target."
 *
 * The Viridian Longbow grant, fanned over a group and time-boxed: `ForEachInGroup` snapshots the
 * permanents you control *as this resolves* (CR 611.2c — a permanent that enters later does not
 * gain the ability) and hands each one the same `{T}` pinger for the turn. `EffectTarget.Self`
 * means the iterated permanent at grant time, and the *host* again inside the granted ability, so
 * the `{T}` taps that permanent and it is the source of the damage (CR 113.7).
 *
 * Summoning sickness gates only creatures, so a land or artifact that entered this turn can shoot
 * immediately — the card's own ruling.
 */
val FlameFusillade = card("Flame Fusillade") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Until end of turn, permanents you control gain \"{T}: This permanent deals 1 damage to any target.\""

    spell {
        effect = Effects.ForEachInGroup(
            GroupFilter.AllPermanentsYouControl,
            GrantActivatedAbilityEffect(
                ability = ActivatedAbility(
                    id = AbilityId.generate(),
                    cost = Costs.Tap,
                    effect = Effects.DealDamage(
                        amount = 1,
                        target = EffectTarget.ContextTarget(0),
                        damageSource = EffectTarget.Self
                    ),
                    targetRequirements = listOf(AnyTarget())
                ),
                target = EffectTarget.Self,
                duration = Duration.EndOfTurn
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "123"
        artist = "Dany Orizio"
        flavorText = "\"A single item acting as lantern and lance? Now *that's* military efficiency.\"\n" +
            "—Brev Grezar, Boros lieutenant"
        imageUri = "https://cards.scryfall.io/normal/front/7/f/7fcad5f0-53c4-4b72-ac05-ef9ca5b55611.jpg?1783943654"

        ruling(
            "2005-10-01",
            "\"Summoning sickness\" applies only to creatures, not to other permanents. You may tap a " +
                "noncreature permanent to pay for an ability with {T} in its cost even if the permanent " +
                "entered that turn."
        )
        ruling(
            "2005-10-01",
            "Tapping an Aura or Equipment doesn't tap the creature it's attached to and vice versa. " +
                "Tapped enchantments work normally. Tapped artifacts work normally unless they say otherwise."
        )
    }
}
