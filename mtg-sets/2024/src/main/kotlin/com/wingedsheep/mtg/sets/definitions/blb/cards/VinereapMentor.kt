package com.wingedsheep.mtg.sets.definitions.blb.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Vinereap Mentor
 * {B}{G}
 * Creature — Squirrel Druid
 * 3/2
 * When this creature enters or dies, create a Food token.
 */
val VinereapMentor = card("Vinereap Mentor") {
    manaCost = "{B}{G}"
    colorIdentity = "BG"
    typeLine = "Creature — Squirrel Druid"
    oracleText = "When this creature enters or dies, create a Food token. (It's an artifact with \"{2}, {T}, Sacrifice this token: You gain 3 life.\")"
    power = 3
    toughness = 2

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateFood(1)
    }

    triggeredAbility {
        trigger = Triggers.Dies
        effect = Effects.CreateFood(1)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "238"
        artist = "Valera Lutfullina"
        flavorText = "\"In youth, we reap. In death, we sow.\""
        imageUri = "https://cards.scryfall.io/normal/front/2/9/29b615ba-45c4-42a1-8525-1535f0b55300.jpg?1721427225"
    }
}
