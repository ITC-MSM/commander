package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.TriggeredAbilityBuilder
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.AddCountersEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.scripting.values.EntityNumericProperty
import com.wingedsheep.sdk.scripting.values.EntityReference

/**
 * Queen's Bay Paladin
 * {3}{B}{B}
 * Creature — Vampire Knight
 * 5/4
 *
 * Whenever this creature enters or attacks, return up to one target Vampire card from your
 * graveyard to the battlefield with a finality counter on it. You lose life equal to its
 * mana value. (If a creature with a finality counter on it would die, exile it instead.)
 *
 * Modeled as two triggered abilities (enters + attacks) sharing the same rider (extracted
 * into [returnVampireRider]), mirroring Sentinel of the Nameless City's "enters or attacks"
 * idiom. Each ability targets up to one Vampire card in the controller's graveyard
 * (`optional = true` → "up to one target"),
 * returns it to the battlefield, drops a finality counter on it (Rite of the Moth pattern:
 * Move then AddCounters), and the controller loses life equal to the returned card's mana
 * value (Phyrexian Delver pattern: `EntityProperty(Target(0), ManaValue)`). When no target
 * is chosen the Move/AddCounters are no-ops and the life loss reads 0.
 */
val QueensBayPaladin = card("Queen's Bay Paladin") {
    manaCost = "{3}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Vampire Knight"
    power = 5
    toughness = 4
    oracleText = "Whenever this creature enters or attacks, return up to one target Vampire card " +
        "from your graveyard to the battlefield with a finality counter on it. You lose life equal " +
        "to its mana value. (If a creature with a finality counter on it would die, exile it instead.)"

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        returnVampireRider()
    }

    triggeredAbility {
        trigger = Triggers.Attacks
        returnVampireRider()
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "115"
        artist = "Slawomir Maniak"
        imageUri = "https://cards.scryfall.io/normal/front/d/0/d0a3339d-6067-4af5-9536-7e2d5ddd8918.jpg?1782694518"
    }
}

/**
 * The rider shared by both trigger conditions: return up to one target Vampire card from
 * your graveyard to the battlefield with a finality counter on it, then lose life equal
 * to its mana value.
 */
private fun TriggeredAbilityBuilder.returnVampireRider() {
    val returned = target(
        "up to one target Vampire card from your graveyard",
        TargetObject(
            count = 1,
            optional = true,
            filter = TargetFilter(
                GameObjectFilter.Any.withSubtype("Vampire").ownedByYou(),
                zone = Zone.GRAVEYARD,
            ),
        ),
    )
    effect = Effects.Composite(
        Effects.Move(returned, Zone.BATTLEFIELD, fromZone = Zone.GRAVEYARD),
        AddCountersEffect(counterType = Counters.FINALITY, count = 1, target = returned),
        Effects.LoseLife(
            DynamicAmount.EntityProperty(EntityReference.Target(0), EntityNumericProperty.ManaValue),
            EffectTarget.Controller,
        ),
    )
}
