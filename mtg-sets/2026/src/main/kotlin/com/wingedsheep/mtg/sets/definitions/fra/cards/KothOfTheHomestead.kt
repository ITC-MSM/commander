package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TriggerBinding

val KothOfTheHomestead = card("Koth of the Homestead") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Legendary Creature — Human Citizen"
    oracleText = "Landfall — Whenever a land you control enters, you gain 1 life.\n" +
        "Whenever a Plains you control enters, put a +1/+1 counter on target creature."
    power = 2
    toughness = 3

    triggeredAbility {
        trigger = Triggers.LandYouControlEnters
        effect = Effects.GainLife(1)
        description = "Landfall — Whenever a land you control enters, you gain 1 life."
    }

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = Filters.PlainsCard.youControl(),
            binding = TriggerBinding.ANY
        )
        val creature = target("target creature", Targets.Creature)
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, creature)
        description = "Whenever a Plains you control enters, put a +1/+1 counter on target creature."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "199"
        artist = "Kieran Yanner"
        flavorText = "\"Malach, tell your mother the steelfruit harvest is abundant this cycle!\""
        imageUri = "https://cards.scryfall.io/normal/front/9/2/920703fd-2a2f-454b-8829-af8f2afda4f4.jpg?1789568532"
        inBooster = false
    }
}
