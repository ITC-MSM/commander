package com.wingedsheep.mtg.sets.definitions.ltr.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Samwise Gamgee
 * {G}{W}
 * Legendary Creature — Halfling Peasant
 * 2/2
 *
 * Whenever another nontoken creature you control enters, create a Food token. (It's an
 * artifact with "{2}, {T}, Sacrifice this token: You gain 3 life.")
 * Sacrifice three Foods: Return target historic card from your graveyard to your hand.
 * (Artifacts, legendaries, and Sagas are historic.)
 */
val SamwiseGamgee = card("Samwise Gamgee") {
    manaCost = "{G}{W}"
    colorIdentity = "GW"
    typeLine = "Legendary Creature — Halfling Peasant"
    power = 2
    toughness = 2
    oracleText = "Whenever another nontoken creature you control enters, create a Food token. (It's an " +
        "artifact with \"{2}, {T}, Sacrifice this token: You gain 3 life.\")\n" +
        "Sacrifice three Foods: Return target historic card from your graveyard to your hand. " +
        "(Artifacts, legendaries, and Sagas are historic.)"

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Creature.nontoken().youControl(),
            binding = TriggerBinding.OTHER
        )
        effect = Effects.CreateFood()
    }

    activatedAbility {
        cost = Costs.SacrificeMultiple(3, GameObjectFilter.Any.withSubtype("Food"))
        val t = target(
            "historic card from your graveyard",
            TargetObject(filter = TargetFilter(GameObjectFilter.Historic.ownedByYou(), zone = Zone.GRAVEYARD))
        )
        effect = Effects.ReturnToHand(t)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "222"
        artist = "Ekaterina Burmak"
        imageUri = "https://cards.scryfall.io/normal/front/a/1/a1b6f13e-63d0-46bf-aa57-23c2dbdf62dd.jpg?1686969973"
    }
}
