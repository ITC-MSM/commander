package com.wingedsheep.mtg.sets.definitions.spm.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Aunt May
 * {W}
 * Legendary Creature — Human Citizen
 * 0/2
 * Whenever another creature you control enters, you gain 1 life. If it's a Spider,
 * put a +1/+1 counter on it.
 */
val AuntMay = card("Aunt May") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Legendary Creature — Human Citizen"
    power = 0
    toughness = 2
    oracleText = "Whenever another creature you control enters, you gain 1 life. " +
        "If it's a Spider, put a +1/+1 counter on it."

    triggeredAbility {
        trigger = Triggers.OtherCreatureEnters
        effect = Effects.GainLife(1)
            .then(
                ConditionalEffect(
                    condition = Conditions.EntityMatches(
                        EffectTarget.TriggeringEntity,
                        GameObjectFilter.Creature.withSubtype(Subtype.SPIDER),
                    ),
                    effect = Effects.AddCounters(
                        Counters.PLUS_ONE_PLUS_ONE,
                        1,
                        EffectTarget.TriggeringEntity,
                    ),
                ),
            )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "3"
        artist = "Randy Gallegos"
        flavorText = "\"I cooked your favorite breakfast, Petey—wheatcakes!\""
        imageUri = "https://cards.scryfall.io/normal/front/a/d/ad96343b-baac-428c-8270-fcffbbbe9fb8.jpg?1757376751"
    }
}
