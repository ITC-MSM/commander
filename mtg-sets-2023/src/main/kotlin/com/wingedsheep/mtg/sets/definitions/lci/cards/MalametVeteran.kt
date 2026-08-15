package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Malamet Veteran — {4}{G}
 * Creature — Cat Warrior
 * 5/4
 *
 * Trample
 * Descend 4 — Whenever this creature attacks, if there are four or more permanent cards in your
 * graveyard, put a +1/+1 counter on target creature.
 *
 * "Descend 4" is an ability word (CR 207.2c — no rules meaning of its own); the rules content is
 * the intervening-if clause (CR 603.4): four or more permanent cards in your graveyard. Modeled as
 * an attack trigger with an intervening-if [Conditions.CardsInGraveyardMatchingAtLeast]: the
 * ability goes on the stack only when the condition holds at trigger time. Known engine gap: CR
 * 603.4 also requires the condition to be rechecked as the ability resolves, but the engine
 * evaluates `triggerCondition` only at detection time (`TriggerMatcher.filterByTriggerCondition`),
 * so a graveyard emptied in response still resolves the trigger.
 *
 * Targeting is mandatory (no "may"): the controller must choose any creature (theirs or an
 * opponent's) to receive the +1/+1 counter. If no legal target exists the ability doesn't go on the
 * stack; if the sole target becomes illegal before resolution the ability fizzles.
 */
val MalametVeteran = card("Malamet Veteran") {
    manaCost = "{4}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Cat Warrior"
    power = 5
    toughness = 4
    oracleText = "Trample\n" +
        "Descend 4 — Whenever this creature attacks, if there are four or more permanent cards in " +
        "your graveyard, put a +1/+1 counter on target creature."

    keywords(Keyword.TRAMPLE)

    triggeredAbility {
        trigger = Triggers.Attacks
        triggerCondition = Conditions.CardsInGraveyardMatchingAtLeast(4, GameObjectFilter.Permanent)
        val t = target("target creature", TargetCreature())
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "201"
        artist = "Steve Prescott"
        flavorText = "The mightiest Malamet warriors train as titan-killers, elite champions capable of " +
            "facing down the caverns' most fearsome beasts."
        imageUri = "https://cards.scryfall.io/normal/front/4/b/4b9b16b3-1cbd-4b63-85b2-e4053dfc1e93.jpg?1782694449"
    }
}
