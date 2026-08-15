package com.wingedsheep.mtg.sets.definitions.ltr.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.EntersWithCounters
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.events.CounterTypeFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * The Watcher in the Water
 * {3}{U}{U}
 * Legendary Creature — Kraken
 * 9/9
 *
 * The Watcher in the Water enters tapped with nine stun counters on it. (If a
 * permanent with a stun counter would become untapped, remove one from it instead.)
 * Whenever you draw a card during an opponent's turn, create a 1/1 blue Tentacle
 * creature token.
 * Whenever a Tentacle you control dies, untap up to one target Kraken and put a
 * stun counter on up to one target nonland permanent.
 */
val TheWatcherInTheWater = card("The Watcher in the Water") {
    manaCost = "{3}{U}{U}"
    colorIdentity = "U"
    typeLine = "Legendary Creature — Kraken"
    power = 9
    toughness = 9
    oracleText = "The Watcher in the Water enters tapped with nine stun counters on it. " +
        "(If a permanent with a stun counter would become untapped, remove one from it instead.)\n" +
        "Whenever you draw a card during an opponent's turn, create a 1/1 blue Tentacle creature token.\n" +
        "Whenever a Tentacle you control dies, untap up to one target Kraken and put a stun counter on up to one target nonland permanent."

    // Enters tapped with nine stun counters.
    replacementEffect(EntersTapped())
    replacementEffect(
        EntersWithCounters(
            counterType = CounterTypeFilter.Named(Counters.STUN),
            count = 9,
            selfOnly = true
        )
    )

    // Whenever you draw a card during an opponent's turn, create a 1/1 blue Tentacle creature token.
    triggeredAbility {
        trigger = Triggers.YouDraw
        triggerCondition = Conditions.IsNotYourTurn
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.BLUE),
            creatureTypes = setOf("Tentacle"),
            imageUri = "https://cards.scryfall.io/normal/front/e/e/eea4e628-4ae2-4e33-a3b6-176ed87c1840.jpg?1686336819"
        )
    }

    // Whenever a Tentacle you control dies, untap up to one target Kraken and put
    // a stun counter on up to one target nonland permanent.
    triggeredAbility {
        trigger = Triggers.leavesBattlefield(
            filter = GameObjectFilter.Creature.withSubtype("Tentacle").youControl(),
            to = Zone.GRAVEYARD,
            binding = TriggerBinding.ANY
        )
        val kraken = target(
            "up to one target Kraken",
            TargetCreature(
                count = 1,
                optional = true,
                filter = TargetFilter(GameObjectFilter.Creature.withSubtype("Kraken"))
            )
        )
        val permanent = target(
            "up to one target nonland permanent",
            TargetPermanent(
                count = 1,
                optional = true,
                filter = TargetFilter(GameObjectFilter.NonlandPermanent)
            )
        )
        effect = Effects.Untap(kraken)
            .then(Effects.AddCounters(Counters.STUN, 1, permanent))
        description = "Whenever a Tentacle you control dies, untap up to one target Kraken and put a stun counter on up to one target nonland permanent."
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "75"
        artist = "Chris Cold"
        imageUri = "https://cards.scryfall.io/normal/front/1/c/1cb8e8bb-75a0-4b5e-b4b3-5d8f3795032d.jpg?1686968354"
    }
}
