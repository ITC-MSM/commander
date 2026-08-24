package com.wingedsheep.mtg.sets.definitions.som.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Blistergrub
 * {2}{B}
 * Creature — Phyrexian Horror
 * 2/2
 *
 * Swampwalk (This creature can't be blocked as long as defending player controls a Swamp.)
 * When this creature dies, each opponent loses 2 life.
 */
val Blistergrub = card("Blistergrub") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Phyrexian Horror"
    power = 2
    toughness = 2
    oracleText = "Swampwalk (This creature can't be blocked as long as defending player controls a Swamp.)\n" +
        "When this creature dies, each opponent loses 2 life."

    keywords(Keyword.SWAMPWALK)

    triggeredAbility {
        trigger = Triggers.Dies
        effect = Effects.LoseLife(2, EffectTarget.PlayerRef(Player.EachOpponent))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "56"
        artist = "Daarken"
        flavorText = "\"The sooner you join Phyrexia, the sooner you'll forget your painful rebirth.\"\n—Sheoldred, Whispering One"
        imageUri = "https://cards.scryfall.io/normal/front/5/4/5431debc-0037-49ff-a38f-3fa2f9f5ee33.jpg?1783941734"
    }
}
