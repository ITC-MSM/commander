package com.wingedsheep.mtg.sets.definitions.war.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Lazotep Behemoth
 * {4}{B}
 * Creature — Zombie Hippo
 * 5/4
 *
 * Vanilla — no rules text.
 */
val LazotepBehemoth = card("Lazotep Behemoth") {
    manaCost = "{4}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Zombie Hippo"
    power = 5
    toughness = 4

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "95"
        artist = "Zezhou Chen"
        flavorText = "\"I know I should be more concerned. But a big, blue zombie-potamus from beyond the stars? This is what they're invading us with?\"\n—Mileva, Boros legionnaire"
        imageUri = "https://cards.scryfall.io/normal/front/b/4/b4be6f22-e9e8-462a-956b-e1c78bbadacc.jpg?1783933444"
    }
}
