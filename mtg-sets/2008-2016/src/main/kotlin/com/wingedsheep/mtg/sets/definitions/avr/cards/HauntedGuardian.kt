package com.wingedsheep.mtg.sets.definitions.avr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Haunted Guardian
 * {2}
 * Artifact Creature — Construct
 * 2 / 1
 *
 * Defender, first strike
 *
 * Both lines are printed [Keyword]s — nothing but `keywords(...)`.
 */
val HauntedGuardian = card("Haunted Guardian") {
    manaCost = "{2}"
    typeLine = "Artifact Creature — Construct"
    power = 2
    toughness = 1
    oracleText = "Defender, first strike"

    keywords(Keyword.DEFENDER, Keyword.FIRST_STRIKE)

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "216"
        artist = "Daniel Ljunggren"
        flavorText = "\"Drain the victim's blood, sell the corpse, and use the soul on guard duty. No muss, no fuss, no problems.\"\n—Olivia Voldaren"
        imageUri = "https://cards.scryfall.io/normal/front/7/d/7d97f8b8-bdb0-4d4b-b077-9affe2f9cd91.jpg?1783940652"
    }
}
