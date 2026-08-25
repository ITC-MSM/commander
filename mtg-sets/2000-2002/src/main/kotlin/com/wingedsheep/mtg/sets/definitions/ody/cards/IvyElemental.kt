package com.wingedsheep.mtg.sets.definitions.ody.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersWithDynamicCounters
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Ivy Elemental
 * {X}{G}
 * Creature — Elemental
 * 0/0
 * This creature enters with X +1/+1 counters on it.
 *
 * The counters are an [EntersWithDynamicCounters] replacement (CR 614.1c) rather than an
 * enters trigger, so the body is never a 0/0 on the battlefield.
 */
val IvyElemental = card("Ivy Elemental") {
    manaCost = "{X}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elemental"
    power = 0
    toughness = 0
    oracleText = "This creature enters with X +1/+1 counters on it."

    replacementEffect(EntersWithDynamicCounters(count = DynamicAmount.XValue))

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "245"
        artist = "Ron Spencer"
        flavorText = "In the gardens of the centaurs, many travelers have mysteriously vanished while admiring the elaborate topiaries."
        imageUri = "https://cards.scryfall.io/normal/front/f/c/fc441d3a-e917-4dd6-b5f9-f99075ec398f.jpg"
    }
}
