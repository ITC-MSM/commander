package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Thalakos Seer
 * {U}{U}
 * Creature — Thalakos Wizard
 * 1/1
 * Shadow (This creature can block or be blocked by only creatures with shadow.)
 * When this creature leaves the battlefield, draw a card.
 */
val ThalakosSeer = card("Thalakos Seer") {
    manaCost = "{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Thalakos Wizard"
    power = 1
    toughness = 1
    oracleText = "Shadow (This creature can block or be blocked by only creatures with shadow.)\n" +
        "When this creature leaves the battlefield, draw a card."

    keywords(Keyword.SHADOW)

    triggeredAbility {
        trigger = Triggers.LeavesBattlefield
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "94"
        artist = "Ron Spencer"
        flavorText = "\"You see our world when you shut your eyes so tightly that tiny shapes float before them.\"\n" +
            "—Lyna, to Ertai"
        imageUri = "https://cards.scryfall.io/normal/front/1/3/136a7d63-94ae-4d92-86ab-12bf9d78a803.jpg"
    }
}
