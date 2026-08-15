package com.wingedsheep.mtg.sets.definitions.rix.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Canal Monitor
 * {4}{B}
 * Creature — Lizard
 * 5/3
 *
 * Vanilla — no rules text.
 */
val CanalMonitor = card("Canal Monitor") {
    manaCost = "{4}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Lizard"
    power = 5
    toughness = 3

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "63"
        artist = "Zezhou Chen"
        flavorText = "The first goblin tried to swim the canal. The second built a raft. The last and craftiest goblin launched herself from a firecannon and soared over the canal, trailing smoke. All were eaten, but only one was cooked."
        imageUri = "https://cards.scryfall.io/normal/front/7/8/78226edc-87dd-4c38-987c-52aefe0f9531.jpg?1783935316"
    }
}
