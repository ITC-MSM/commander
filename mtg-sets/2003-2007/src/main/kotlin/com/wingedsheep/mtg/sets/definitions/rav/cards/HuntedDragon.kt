package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Hunted Dragon — Ravnica: City of Guilds #131
 * {3}{R}{R} · Creature — Dragon · 6/6
 *
 * Flying, haste
 * When this creature enters, target opponent creates three 2/2 white Knight creature tokens with
 * first strike.
 *
 * The red member of the Hunted cycle. Haste is the point: the Dragon attacks for six the turn it
 * lands, before the three Knights it hands over can be used to trade with it. The Knights carry
 * first strike as a token keyword and enter under the *targeted opponent's* control via
 * [Effects.CreateToken]'s `controller`.
 */
val HuntedDragon = card("Hunted Dragon") {
    manaCost = "{3}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Dragon"
    oracleText = "Flying, haste\n" +
        "When this creature enters, target opponent creates three 2/2 white Knight creature " +
        "tokens with first strike."
    power = 6
    toughness = 6

    keywords(Keyword.FLYING, Keyword.HASTE)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val opponent = target("target opponent", Targets.Opponent)
        effect = Effects.CreateToken(
            power = 2,
            toughness = 2,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Knight"),
            keywords = setOf(Keyword.FIRST_STRIKE),
            count = 3,
            controller = opponent,
        )
        description = "When this creature enters, target opponent creates three 2/2 white Knight " +
            "creature tokens with first strike."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "131"
        artist = "Mark Zug"
        flavorText = "The knights see a mighty quarry. The dragon sees breakfast, lunch, and dinner."
        imageUri = "https://cards.scryfall.io/normal/front/d/2/d2b9cc6e-47db-47d5-84c9-975e4b618261.jpg?1783943652"
    }
}
