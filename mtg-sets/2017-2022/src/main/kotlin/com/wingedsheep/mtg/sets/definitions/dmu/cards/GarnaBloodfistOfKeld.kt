package com.wingedsheep.mtg.sets.definitions.dmu.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Garna, Bloodfist of Keld
 * {1}{B}{R}{R}
 * Legendary Creature — Human Berserker
 * 4/3
 *
 * Whenever another creature you control dies, draw a card if it was attacking.
 * Otherwise, Garna deals 1 damage to each opponent.
 *
 * One trigger, one branch: the two clauses are mutually exclusive halves of a single ability, so
 * this is a [ConditionalEffect] on the dying creature's combat status rather than two triggers
 * with mirrored intervening-if clauses (which would each check at both trigger and resolution
 * time, CR 603.4, and could diverge).
 *
 * "It was attacking" is last-known information (CR 608.2h): by the time the ability resolves the
 * creature is in the graveyard and its `AttackingComponent` has been torn down by the battlefield
 * exit. `StatePredicate.IsAttacking` (via `GameObjectFilter.attacking()`) reads the frozen
 * `EntitySnapshot.wasAttacking` for an entity that has left the battlefield, so the branch sees
 * the creature as it last existed. The filter is left unrestricted (`Any`) because the trigger
 * has already scoped the event to creatures you control — re-asserting `Creature` here would
 * re-derive a type from a projection that no longer covers the dead permanent.
 */
val GarnaBloodfistOfKeld = card("Garna, Bloodfist of Keld") {
    manaCost = "{1}{B}{R}{R}"
    colorIdentity = "BR"
    typeLine = "Legendary Creature — Human Berserker"
    oracleText = "Whenever another creature you control dies, draw a card if it was attacking. " +
        "Otherwise, Garna deals 1 damage to each opponent."
    power = 4
    toughness = 3

    triggeredAbility {
        trigger = Triggers.leavesBattlefield(
            filter = GameObjectFilter.Creature.youControl(),
            to = Zone.GRAVEYARD,
            binding = TriggerBinding.OTHER,
        )
        effect = ConditionalEffect(
            condition = Conditions.EntityMatches(
                EffectTarget.TriggeringEntity,
                GameObjectFilter.Any.attacking(),
            ),
            effect = Effects.DrawCards(1),
            elseEffect = Effects.DealDamage(1, EffectTarget.PlayerRef(Player.EachOpponent)),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "200"
        artist = "Andrey Kuzinskiy"
        flavorText = "\"Let's see who gets more kills, Radha.\""
        imageUri = "https://cards.scryfall.io/normal/front/2/9/294c5f08-08e7-458f-8838-ff321dc5d9f2.jpg?1783921286"
    }
}
