package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.RevealTopOfLibrary

/**
 * Goblin Spy
 * {R}
 * Creature — Goblin Rogue
 * 1/1
 * Play with the top card of your library revealed.
 *
 * Unlike Future Sight, Goblin Spy grants no permission to play the revealed card — it only
 * makes the top card public. Modeled with [RevealTopOfLibrary] (the public-reveal-only sibling
 * of [com.wingedsheep.sdk.scripting.PlayFromTopOfLibrary]).
 */
val GoblinSpy = card("Goblin Spy") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Creature — Goblin Rogue"
    power = 1
    toughness = 1
    oracleText = "Play with the top card of your library revealed."

    staticAbility {
        ability = RevealTopOfLibrary
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "145"
        artist = "Scott M. Fischer"
        flavorText = "\"Isn't he on our side?\"\n\"Yep.\"\n\"Why's he spyin' on us?\"\n\"Don't ask.\""
        imageUri = "https://cards.scryfall.io/normal/front/2/a/2a89a099-8805-4b26-babd-5d9f48ee406a.jpg?1595438179"
    }
}
