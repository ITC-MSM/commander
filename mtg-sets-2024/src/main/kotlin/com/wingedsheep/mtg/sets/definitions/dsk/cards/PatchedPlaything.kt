package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersWithCounters
import com.wingedsheep.sdk.scripting.events.CounterTypeFilter

/**
 * Patched Plaything
 * {2}{W}
 * Artifact Creature — Toy
 * 4/3
 * Double strike
 * This creature enters with two -1/-1 counters on it if you cast it from your hand.
 *
 * The conditional enter-with-counters is a replacement effect (CR 614.12) whose condition is
 * evaluated as the permanent enters: [Conditions.WasCastFromHand] reads the cast-origin marker
 * stamped on the resolving permanent, so the two -1/-1 counters appear only on a hand-cast (not when
 * it enters via a token-copy, reanimation, or any effect that puts it onto the battlefield without
 * casting it from hand).
 */
val PatchedPlaything = card("Patched Plaything") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Artifact Creature — Toy"
    power = 4
    toughness = 3
    oracleText = "Double strike\n" +
        "This creature enters with two -1/-1 counters on it if you cast it from your hand."

    keywords(Keyword.DOUBLE_STRIKE)

    replacementEffect(
        EntersWithCounters(
            counterType = CounterTypeFilter.MinusOneMinusOne,
            count = 2,
            selfOnly = true,
            condition = Conditions.WasCastFromHand
        )
    )

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "24"
        artist = "Domenico Cava"
        flavorText = "When it ripped, its owner attempted to fix it. Something went... wrong."
        imageUri = "https://cards.scryfall.io/normal/front/5/1/513da431-4f3a-4f4a-8be4-7e162dd93307.jpg?1726285949"
    }
}
