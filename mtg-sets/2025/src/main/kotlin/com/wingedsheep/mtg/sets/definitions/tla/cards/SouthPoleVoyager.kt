package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EventPattern.ZoneChangeEvent
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.TriggerSpec
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.effects.IncrementAbilityResolutionCountEffect

/**
 * South Pole Voyager
 * {1}{W}
 * Creature — Human Scout Ally
 * 2/2
 *
 * Whenever this creature or another Ally you control enters, you gain 1 life. If this is the
 * second time this ability has resolved this turn, draw a card.
 */
val SouthPoleVoyager = card("South Pole Voyager") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Scout Ally"
    power = 2
    toughness = 2
    oracleText = "Whenever this creature or another Ally you control enters, you gain 1 life. If this is the second time this ability has resolved this turn, draw a card."

    triggeredAbility {
        trigger = TriggerSpec(
            event = ZoneChangeEvent(
                filter = GameObjectFilter.Creature
                    .withSubtype(Subtype("Ally"))
                    .youControl(),
                to = Zone.BATTLEFIELD
            ),
            binding = TriggerBinding.ANY
        )
        effect = Effects.GainLife(1)
            .then(IncrementAbilityResolutionCountEffect)
            .then(
                ConditionalEffect(
                    condition = Conditions.SourceAbilityResolvedNTimes(2),
                    effect = Effects.DrawCards(1)
                )
            )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "35"
        artist = "Tition"
        flavorText = "\"The winds can be brutal, so be brave.\""
        imageUri = "https://cards.scryfall.io/normal/front/4/b/4b5ad895-be8d-476b-91ca-22fad7a3cc58.jpg?1764120126"
    }
}
