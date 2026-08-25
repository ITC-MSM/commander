package com.wingedsheep.mtg.sets.definitions.iko.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersWithCounters

/**
 * Adaptive Shimmerer — Ikoria: Lair of Behemoths #1
 * {5} · Creature — Insect · 0/0
 *
 * Flash
 * This creature enters with three +1/+1 counters on it.
 *
 * The printed 0/0 body is only ever a 0/0 in the abstract: the counters arrive as the permanent
 * enters, so it is a 3/3 the first time state-based actions look at it and never dies on entry.
 * That "as it enters" wording is a replacement effect (CR 614.1c), not an ETB trigger, which is
 * why it is an [EntersWithCounters] with `selfOnly = true` rather than a triggered ability.
 */
val AdaptiveShimmerer = card("Adaptive Shimmerer") {
    manaCost = "{5}"
    colorIdentity = ""
    typeLine = "Creature — Insect"
    power = 0
    toughness = 0
    oracleText = "Flash\nThis creature enters with three +1/+1 counters on it."

    keywords(Keyword.FLASH)

    replacementEffect(
        EntersWithCounters(
            count = 3,
            selfOnly = true
        )
    )

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "1"
        artist = "Jason Felix"
        flavorText = "\"You sure you want to see what emerges? You might not like it. It definitely won't like you.\"\n—Kinnan, bonder prodigy"
        imageUri = "https://cards.scryfall.io/normal/front/d/8/d8a2e243-e446-46c6-8a37-e26620951c41.jpg"
    }
}
