package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration

val MultiplyByZero = card("Multiply by Zero") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "Target creature has base power and toughness 0/0 until end of turn."

    spell {
        val creature = target("creature", Targets.Creature)
        effect = Effects.SetBasePowerAndToughness(0, 0, creature, Duration.EndOfTurn)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "58"
        artist = "Florian Herold"
        flavorText = "Existence can be categorically denied. You just need the right variables."
        imageUri = "https://cards.scryfall.io/normal/front/9/0/90d684a4-9639-4792-8760-2011a7a85370.jpg?1789644823"
        inBooster = false
    }
}
