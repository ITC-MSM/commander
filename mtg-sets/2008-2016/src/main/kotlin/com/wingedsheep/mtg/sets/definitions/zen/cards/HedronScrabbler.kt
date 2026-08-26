package com.wingedsheep.mtg.sets.definitions.zen.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Hedron Scrabbler
 * {2}
 * Artifact Creature — Construct
 * 1/1
 * Landfall — Whenever a land you control enters, this creature gets +1/+1 until end of turn.
 *
 * Landfall is [Triggers.LandYouControlEnters] — the `ZoneChangeEvent` over
 * `GameObjectFilter.Land.youControl()` with `TriggerBinding.ANY`.
 */
val HedronScrabbler = card("Hedron Scrabbler") {
    manaCost = "{2}"
    typeLine = "Artifact Creature — Construct"
    power = 1
    toughness = 1
    oracleText = "Landfall — Whenever a land you control enters, this creature gets +1/+1 until end of turn."

    triggeredAbility {
        trigger = Triggers.LandYouControlEnters
        effect = Effects.ModifyStats(1, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "204"
        artist = "Jason Felix"
        flavorText = "\"I don't care when the hedrons awoke. *Why* is the question that really matters.\"\n—Anowon, the Ruin Sage"
        imageUri = "https://cards.scryfall.io/normal/front/6/4/6422e062-0ce1-4239-8c2d-06449627a55a.jpg"
    }
}
