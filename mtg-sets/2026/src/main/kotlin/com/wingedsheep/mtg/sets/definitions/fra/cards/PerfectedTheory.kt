package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration

val PerfectedTheory = card("Perfected Theory") {
    manaCost = "{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Choose one —\n• Target creature has base power and toughness 1/1 until end of turn.\n• Target creature has base power and toughness 4/5 until end of turn."

    spell {
        modal(chooseCount = 1) {
            mode("Target creature has base power and toughness 1/1 until end of turn") {
                val creature = target("creature", Targets.Creature)
                effect = Effects.SetBasePowerAndToughness(1, 1, creature, Duration.EndOfTurn)
            }
            mode("Target creature has base power and toughness 4/5 until end of turn") {
                val creature = target("creature", Targets.Creature)
                effect = Effects.SetBasePowerAndToughness(4, 5, creature, Duration.EndOfTurn)
            }
        }
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "34"
        artist = "Marta Nael"
        flavorText = "In the Meditation Realm, Jace had seen himself reflected in a Multiverse complete in every detail. Now he only had to make it real."
        imageUri = "https://cards.scryfall.io/normal/front/d/0/d0ecae06-bc5a-4886-84df-c2900816f226.jpg?1788329222"
        inBooster = false
    }
}
