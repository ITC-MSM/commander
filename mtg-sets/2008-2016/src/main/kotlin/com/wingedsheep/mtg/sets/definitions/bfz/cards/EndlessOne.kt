package com.wingedsheep.mtg.sets.definitions.bfz.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersWithDynamicCounters
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Endless One
 * {X}
 * Creature — Eldrazi
 * 0/0
 * This creature enters with X +1/+1 counters on it.
 *
 * The X is the value announced as the spell was cast ([DynamicAmount.XValue]), so a copy of
 * Endless One made on the stack keeps it and one made on the battlefield enters as a 0/0.
 */
val EndlessOne = card("Endless One") {
    manaCost = "{X}"
    colorIdentity = ""
    typeLine = "Creature — Eldrazi"
    power = 0
    toughness = 0
    oracleText = "This creature enters with X +1/+1 counters on it."

    replacementEffect(EntersWithDynamicCounters(count = DynamicAmount.XValue))

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "8"
        artist = "Jason Felix"
        flavorText = "It embodies all possible meanings of the word \"infinite.\""
        imageUri = "https://cards.scryfall.io/normal/front/6/4/64820f4f-1f78-4338-beb8-5ed5a447cfe4.jpg?1783938224"
    }
}
