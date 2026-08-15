package com.wingedsheep.mtg.sets.definitions.spm.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.effects.DrawCardsEffect

/**
 * Mary Jane Watson
 * {1}{G/W}
 * Legendary Creature — Human Performer
 * 2/2
 * Whenever a Spider you control enters, draw a card. This ability triggers only once each turn.
 */
val MaryJaneWatson = card("Mary Jane Watson") {
    manaCost = "{1}{G/W}"
    colorIdentity = "WG"
    typeLine = "Legendary Creature — Human Performer"
    oracleText = "Whenever a Spider you control enters, draw a card. This ability triggers only once each turn."
    power = 2
    toughness = 2
    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Creature.withSubtype(Subtype.SPIDER).youControl(),
            binding = TriggerBinding.ANY
        )
        oncePerTurn = true
        effect = DrawCardsEffect(1)
    }
    metadata {
        rarity = Rarity.RARE
        collectorNumber = "134"
        artist = "Steve Argyle"
        flavorText = "Charismatic and confident, Mary Jane was born for the limelight."
        imageUri = "https://cards.scryfall.io/normal/front/1/7/178345f7-8ccd-4e47-80f4-5bd31bab6655.jpg?1757377723"
    }
}
