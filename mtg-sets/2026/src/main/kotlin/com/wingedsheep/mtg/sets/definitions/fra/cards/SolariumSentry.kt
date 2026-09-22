package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

val SolariumSentry = card("Solarium Sentry") {
    manaCost = "{G}{W}"
    colorIdentity = "GW"
    typeLine = "Creature — Cat Soldier"
    oracleText = "Whenever an opponent casts a spell with mana value 2 or less, you gain 2 life."
    power = 3
    toughness = 3

    triggeredAbility {
        trigger = Triggers.opponentCasts(GameObjectFilter.Any.manaValueAtMost(2))
        effect = Effects.GainLife(2)
        description = "Whenever an opponent casts a spell with mana value 2 or less, you gain 2 life."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "148"
        artist = "Chris Rallis"
        flavorText = "\"I've seen hay fever hit harder than that!\""
        imageUri = "https://cards.scryfall.io/normal/front/1/e/1ef12dcf-df50-4da6-8c4c-e2937ba9698e.jpg?1789127651"
        inBooster = false
    }
}
