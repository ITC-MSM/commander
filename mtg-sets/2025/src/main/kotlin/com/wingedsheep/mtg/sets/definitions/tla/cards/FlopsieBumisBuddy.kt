package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantBeBlockedByMoreThan
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.AddCountersEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Flopsie, Bumi's Buddy
 * {4}{G}{G}
 * Legendary Creature — Ape Goat
 * 4/4
 *
 * When Flopsie enters, put a +1/+1 counter on each creature you control.
 * Each creature you control with power 4 or greater can't be blocked by more than one creature.
 *
 * The ETB iterates every creature the controller has ([Effects.ForEachInGroup] over
 * [GameObjectFilter.Creature.youControl]) and drops one +1/+1 counter on each — Flopsie itself
 * included, since it is on the battlefield as the trigger resolves (mirrors The Crystal's Chosen).
 *
 * The blocking restriction is a static ability ([CantBeBlockedByMoreThan] with maxBlockers = 1)
 * scoped by a group filter to creatures you control with power 4 or greater. Combat blocker
 * validation reads projected power, so a creature dipping below 4 (or losing control) drops out of
 * the restriction automatically (mirrors Rocksteady, Crash Courser's filtered variant).
 */
val FlopsieBumisBuddy = card("Flopsie, Bumi's Buddy") {
    manaCost = "{4}{G}{G}"
    colorIdentity = "G"
    typeLine = "Legendary Creature — Ape Goat"
    power = 4
    toughness = 4
    oracleText = "When Flopsie enters, put a +1/+1 counter on each creature you control.\n" +
        "Each creature you control with power 4 or greater can't be blocked by more than one creature."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.ForEachInGroup(
            GroupFilter(GameObjectFilter.Creature.youControl()),
            AddCountersEffect(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self),
        )
        description = "When Flopsie enters, put a +1/+1 counter on each creature you control."
    }

    staticAbility {
        ability = CantBeBlockedByMoreThan(
            maxBlockers = 1,
            filter = GroupFilter(GameObjectFilter.Creature.youControl().powerAtLeast(4)),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "179"
        artist = "Alexandr Leskinen"
        flavorText = "\"Oh, that's a good boy! Yes, who has a soft belly?\"\n—Bumi, King of Omashu"
        imageUri = "https://cards.scryfall.io/normal/front/0/8/082f4abc-c09f-42cf-977f-060875dfd411.jpg?1764121216"
    }
}
