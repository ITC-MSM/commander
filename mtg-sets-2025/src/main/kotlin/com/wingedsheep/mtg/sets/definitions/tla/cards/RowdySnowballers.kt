package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.AddCountersEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Rowdy Snowballers
 * {2}{U}
 * Creature — Human Peasant Ally
 * 2/2
 * When this creature enters, tap target creature an opponent controls and put a
 * stun counter on it. (If a permanent with a stun counter would become untapped,
 * remove one from it instead.)
 */
val RowdySnowballers = card("Rowdy Snowballers") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Peasant Ally"
    power = 2
    toughness = 2
    oracleText = "When this creature enters, tap target creature an opponent controls and put a stun counter on it. (If a permanent with a stun counter would become untapped, remove one from it instead.)"

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val t = target(
            "creature an opponent controls",
            TargetPermanent(filter = TargetFilter(GameObjectFilter.Creature.opponentControls())),
        )
        effect = Effects.Composite(
            Effects.Tap(t),
            AddCountersEffect(counterType = Counters.STUN, count = 1, target = t),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "68"
        artist = "Mizutametori"
        flavorText = "Sokka's training was generally ineffective, but the children of Wolf Cove didn't seem to mind."
        imageUri = "https://cards.scryfall.io/normal/front/1/7/17d71522-b133-4003-b787-0c742a2fd70e.jpg?1764120416"
    }
}
