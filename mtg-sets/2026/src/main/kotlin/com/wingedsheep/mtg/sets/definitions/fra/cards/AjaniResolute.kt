package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TriggeredAbility
import com.wingedsheep.sdk.scripting.effects.CreateTokenEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Ajani Resolute
 * {1}{W}
 * Legendary Planeswalker — Ajani
 * Starting Loyalty: 2
 *
 * - The static-looking first line is a triggered ability on the planeswalker itself: each life-gain
 *   event is one trigger, one loyalty counter ([Triggers.YouGainLife]).
 * - −4's token is a *named* token ("Ajani's Pridemate") carrying its own Pridemate trigger, the same
 *   shape Teferi, Temporal Pilgrim uses for its Spirit token.
 * - −10 is a permanent emblem over creatures you control, so creatures that arrive later also get
 *   +2/+2.
 */
val AjaniResolute = card("Ajani Resolute") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Legendary Planeswalker — Ajani"
    startingLoyalty = 2
    oracleText = "Whenever you gain life, put a loyalty counter on Ajani.\n" +
        "0: You gain 1 life.\n" +
        "−4: Create a 2/2 white Cat Soldier creature token named Ajani's Pridemate with " +
        "\"Whenever you gain life, put a +1/+1 counter on this token.\"\n" +
        "−10: You get an emblem with \"Creatures you control get +2/+2.\""

    triggeredAbility {
        trigger = Triggers.YouGainLife
        effect = Effects.AddCounters(Counters.LOYALTY, 1, EffectTarget.Self)
        description = "Whenever you gain life, put a loyalty counter on Ajani."
    }

    loyaltyAbility(0) {
        effect = Effects.GainLife(1)
        description = "You gain 1 life."
    }

    loyaltyAbility(-4) {
        effect = CreateTokenEffect(
            power = 2,
            toughness = 2,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Cat", "Soldier"),
            name = "Ajani's Pridemate",
            triggeredAbilities = listOf(
                TriggeredAbility.create(
                    trigger = Triggers.YouGainLife.event,
                    binding = Triggers.YouGainLife.binding,
                    effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self),
                    descriptionOverride = "Whenever you gain life, put a +1/+1 counter on this token.",
                )
            ),
            imageUri = "https://cards.scryfall.io/normal/front/7/8/7801c326-d740-4cec-9268-05348ace8302.jpg?1789735461",
        )
        description = "Create a 2/2 white Cat Soldier creature token named Ajani's Pridemate with " +
            "\"Whenever you gain life, put a +1/+1 counter on this token.\""
    }

    loyaltyAbility(-10) {
        effect = Effects.CreatePermanentEmblem(
            groupFilter = GroupFilter.AllCreaturesYouControl,
            powerBonus = 2,
            toughnessBonus = 2,
            emblemDescription = "Creatures you control get +2/+2.",
        )
        description = "You get an emblem with \"Creatures you control get +2/+2.\""
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "195"
        artist = "Tyler Jacobson"
        imageUri = "https://cards.scryfall.io/normal/front/a/5/a5e1a7dd-8c49-4435-935c-bcc78704082b.jpg?1788329189"
        inBooster = false
    }
}
