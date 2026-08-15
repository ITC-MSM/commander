package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.effects.IfYouDoEffect
import com.wingedsheep.sdk.scripting.effects.MayEffect

/**
 * Giott, King of the Dwarves
 * {R}{W}
 * Legendary Creature — Dwarf Noble
 * 1/1
 * Double strike
 * Whenever Giott or another Dwarf you control enters and whenever an Equipment you control enters,
 * you may discard a card. If you do, draw a card.
 *
 * The printed ability triggers on two distinct events ("whenever X and whenever Y"); modeled as two
 * triggered abilities sharing the same rummage payoff. The Dwarf trigger uses [TriggerBinding.ANY]
 * with a "Dwarf you control" filter so it also fires when Giott itself enters.
 */
val GiottKingOfTheDwarves = card("Giott, King of the Dwarves") {
    manaCost = "{R}{W}"
    colorIdentity = "WR"
    typeLine = "Legendary Creature — Dwarf Noble"
    oracleText = "Double strike\nWhenever Giott or another Dwarf you control enters and whenever an Equipment you control enters, you may discard a card. If you do, draw a card."
    power = 1
    toughness = 1

    keywords(Keyword.DOUBLE_STRIKE)

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Creature.withSubtype("Dwarf").youControl(),
            binding = TriggerBinding.ANY,
        )
        effect = MayEffect(
            effect = IfYouDoEffect(
                action = Patterns.Hand.discardCards(1),
                ifYouDo = Effects.DrawCards(1),
            ),
            descriptionOverride = "You may discard a card. If you do, draw a card.",
        )
    }

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Artifact.withSubtype("Equipment").youControl(),
            binding = TriggerBinding.ANY,
        )
        effect = MayEffect(
            effect = IfYouDoEffect(
                action = Patterns.Hand.discardCards(1),
                ifYouDo = Effects.DrawCards(1),
            ),
            descriptionOverride = "You may discard a card. If you do, draw a card.",
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "223"
        artist = "Ben Wootten"
        flavorText = "\"We will fight along with you for the planet, our home!\""
        imageUri = "https://cards.scryfall.io/normal/front/6/a/6a7784de-a10d-4ce6-98a5-aaf3e85773b6.jpg?1748706604"
    }
}
