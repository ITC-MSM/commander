package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Moroii
 * {2}{U}{B}
 * Creature — Vampire
 * 4/4
 * Flying
 * At the beginning of your upkeep, you lose 1 life.
 */
val Moroii = card("Moroii") {
    manaCost = "{2}{U}{B}"
    colorIdentity = "UB"
    typeLine = "Creature — Vampire"
    oracleText = "Flying\n" +
        "At the beginning of your upkeep, you lose 1 life."
    power = 4
    toughness = 4

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.YourUpkeep
        effect = Effects.LoseLife(1, EffectTarget.Controller)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "216"
        artist = "Dan Murayama Scott"
        flavorText = "\"Touched by moroii\"\n—Undercity slang meaning \"to grow old\""
        imageUri = "https://cards.scryfall.io/normal/front/3/d/3de9ce70-67d1-4007-94ed-4545867e90c8.jpg"
    }
}
