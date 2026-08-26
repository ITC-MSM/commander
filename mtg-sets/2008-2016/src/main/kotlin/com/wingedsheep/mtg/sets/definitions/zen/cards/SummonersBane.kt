package com.wingedsheep.mtg.sets.definitions.zen.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Summoner's Bane
 * {2}{U}{U}
 * Instant
 * Counter target creature spell. Create a 2/2 blue Illusion creature token.
 *
 * The token is created even if the counter does nothing — the two effects are sequential, not
 * conditional on one another.
 */
val SummonersBane = card("Summoner's Bane") {
    manaCost = "{2}{U}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Counter target creature spell. Create a 2/2 blue Illusion creature token."

    spell {
        target("creature spell", Targets.CreatureSpell)
        effect = Effects.Composite(
            Effects.CounterSpell(),
            Effects.CreateToken(
                power = 2,
                toughness = 2,
                colors = setOf(Color.BLUE),
                creatureTypes = setOf("Illusion"),
                imageUri = "https://cards.scryfall.io/normal/front/5/d/5dcbf662-7263-414a-b64b-ccf9aab20faa.jpg?1783942179"
            ),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "71"
        artist = "Cyril Van Der Haegen"
        flavorText = "\"I don't need to have the perfect plan. My foe just has to have an imperfect one.\"\n—Jace Beleren"
        imageUri = "https://cards.scryfall.io/normal/front/e/d/ed82afba-df51-4bd9-853c-d3ef323095a6.jpg"
    }
}
