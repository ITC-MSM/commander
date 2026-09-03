package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.effects.DealDamageEffect
import com.wingedsheep.sdk.scripting.effects.IncrementAbilityResolutionCountEffect
import com.wingedsheep.sdk.scripting.events.CounterTypeFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Ashling the Pilgrim — Lorwyn #149
 * {1}{R} · Legendary Creature — Elemental Shaman · 1/1
 *
 * {1}{R}: Put a +1/+1 counter on Ashling. If this is the third time this ability has resolved
 * this turn, remove all +1/+1 counters from Ashling, and it deals that much damage to each
 * creature and each player.
 *
 * The per-turn tally is Lorwyn's own [IncrementAbilityResolutionCountEffect] /
 * [Conditions.SourceAbilityResolvedNTimes] pair, shared with [SoulbrightFlamekin] and
 * [InnerFlameIgniter]. The equality (not a threshold) is what makes the ruling "you won't get
 * the bonus the fourth, fifth, sixth …" fall out for free, and counting *resolutions* rather
 * than activations is the executor's own contract — a countered activation never reaches it.
 *
 * **"That much" has to be read before the counters are gone**, so the count is captured with
 * [Effects.StoreNumber] and the damage reads it back through
 * [DynamicAmount.VariableReference] (Lightning Coils' shape). Reading
 * `countersOnSelf` directly in the damage step would evaluate *after* the removal and deal 0.
 * The order on the card is load-bearing for a second reason: Ashling is a 1/1 again by the time
 * the damage lands, so three counters kill it — the ruling's "it's likely that the damage will
 * kill Ashling".
 *
 * The counter is put on *first* and the tally bumped after it, so the third activation's counter
 * is included in "that much": three activations mean three counters and three damage.
 *
 * Damage to "each creature and each player" is the Earthquake shape — a
 * [Effects.ForEachInGroup] over [GroupFilter.AllCreatures] (Ashling itself included) followed by
 * [Effects.ForEachPlayer] over [Player.Each]. The stored number survives the player loop: only
 * stored *collections* are reset per iteration, not stored numbers.
 */
val AshlingThePilgrim = card("Ashling the Pilgrim") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Legendary Creature — Elemental Shaman"
    power = 1
    toughness = 1
    oracleText = "{1}{R}: Put a +1/+1 counter on Ashling. If this is the third time this ability " +
        "has resolved this turn, remove all +1/+1 counters from Ashling, and it deals that much " +
        "damage to each creature and each player."

    activatedAbility {
        cost = Costs.Mana("{1}{R}")
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
            .then(IncrementAbilityResolutionCountEffect)
            .then(
                ConditionalEffect(
                    condition = Conditions.SourceAbilityResolvedNTimes(3),
                    effect = Effects.Composite(
                        listOf(
                            Effects.StoreNumber(
                                "ashlingRemovedCounters",
                                DynamicAmounts.countersOnSelf(
                                    CounterTypeFilter.Named(Counters.PLUS_ONE_PLUS_ONE)
                                ),
                            ),
                            Effects.RemoveAllCountersOfType(
                                Counters.PLUS_ONE_PLUS_ONE,
                                EffectTarget.Self,
                            ),
                            Effects.ForEachInGroup(
                                GroupFilter.AllCreatures,
                                DealDamageEffect(
                                    DynamicAmount.VariableReference("ashlingRemovedCounters"),
                                    EffectTarget.Self,
                                ),
                            ),
                            Effects.ForEachPlayer(
                                Player.Each,
                                listOf(
                                    Effects.DealDamage(
                                        DynamicAmount.VariableReference("ashlingRemovedCounters"),
                                        EffectTarget.Controller,
                                    )
                                ),
                            ),
                        )
                    )
                )
            )
        description = "Put a +1/+1 counter on Ashling. If this is the third time this ability has " +
            "resolved this turn, remove all +1/+1 counters from Ashling, and it deals that much " +
            "damage to each creature and each player."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "149"
        artist = "Wayne Reynolds"
        imageUri = "https://cards.scryfall.io/normal/front/6/3/63056eb9-4257-4530-8ff4-6909a2cedf47.jpg?1783942881"
        ruling("2023-07-28", "\"That much damage\" refers to the number of +1/+1 counters removed from Ashling the Pilgrim. Since the counters are removed, it's likely that the damage will kill Ashling.")
        ruling("2023-07-28", "Ashling the Pilgrim's ability counts resolutions, not activations. Any such abilities that are still on the stack won't count toward the total.")
        ruling("2023-07-28", "When the ability resolves, it counts the number of times that same ability from that this creature has already resolved that turn. It doesn't matter who controlled the creature or the previous abilities when they resolved. A copy of this ability (created by Rings of Brighthearth, for example) will count toward the total. Abilities from other creatures with the same name don't count towards the total. Neither does an ability that's been countered.")
        ruling("2023-07-28", "You get the bonus only the third time the ability resolves. You won't get the bonus the fourth, fifth, sixth, or any subsequent times.")
    }
}
