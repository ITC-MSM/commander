package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.effects.CreateTokenEffect

/**
 * Mouser Attack!
 * {1}{R}
 * Instant
 *
 * Choose one —
 * • Create a 1/1 colorless Robot artifact creature token.
 * • Target creature gets +3/+0 and gains first strike until end of turn.
 */
val MouserAttack = card("Mouser Attack!") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Choose one —\n• Create a 1/1 colorless Robot artifact creature token.\n• Target creature gets +3/+0 and gains first strike until end of turn."

    spell {
        modal(chooseCount = 1) {
            mode("Create a 1/1 colorless Robot artifact creature token") {
                effect = CreateTokenEffect(
                    power = 1,
                    toughness = 1,
                    colors = setOf(),
                    creatureTypes = setOf("Robot"),
                    artifactToken = true,
                    imageUri = "https://cards.scryfall.io/normal/front/0/8/08497fc5-1c0e-4c3c-a356-bf4b34bd4c45.jpg?1771590585"
                )
            }
            mode("Target creature gets +3/+0 and gains first strike until end of turn") {
                val creature = target("target creature", Targets.Creature)
                effect = Effects.ModifyStats(3, 0, creature)
                    .then(Effects.GrantKeyword(Keyword.FIRST_STRIKE, creature, Duration.EndOfTurn))
            }
        }
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "95"
        artist = "Erikas Perl"
        flavorText = "\"For me, all things remain strictly business. And today's business happens to be extermination!\"\n—Baxter Stockman"
        imageUri = "https://cards.scryfall.io/normal/front/0/5/058490f4-0ada-45e6-b4f0-e433537f52d6.jpg?1771502642"
    }
}
