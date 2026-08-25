package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Jund Battlemage
 * {2}{R}
 * Creature — Human Shaman
 * 2 / 2
 * {B}, {T}: Target player loses 1 life.
 * {G}, {T}: Create a 1/1 green Saproling creature token.
 *
 * One of the Alara shard battlemages: a mono-coloured body with two off-colour activated abilities,
 * so each is a [Costs.Composite] of a single [Costs.Mana] pip plus [Costs.Tap]. The tap is shared,
 * which is what makes the two modes exclusive on any given turn. "Target player" is unrestricted
 * ([Targets.Player]) — it may be pointed at yourself — and the Saproling is spelled entirely by its
 * printed characteristics through the shared [Effects.CreateToken] facade, so no bespoke token
 * definition is needed.
 */
val JundBattlemage = card("Jund Battlemage") {
    manaCost = "{2}{R}"
    colorIdentity = "BGR"
    typeLine = "Creature — Human Shaman"
    power = 2
    toughness = 2
    oracleText = "{B}, {T}: Target player loses 1 life.\n" +
        "{G}, {T}: Create a 1/1 green Saproling creature token."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{B}"), Costs.Tap)
        val t = target("target", Targets.Player)
        effect = Effects.LoseLife(1, t)
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{G}"), Costs.Tap)
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.GREEN),
            creatureTypes = setOf("Saproling"),
            imageUri = "https://cards.scryfall.io/normal/front/6/2/622759a9-e68b-48c1-8e03-beaab0a52556.jpg"
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "106"
        artist = "Vance Kovacs"
        flavorText = "\"Of your blood I will make my mead, an offering to the thirsty jungle.\""
        imageUri = "https://cards.scryfall.io/normal/front/e/e/ee8c962f-11b0-48f7-bbba-a2212e41990f.jpg"
    }
}
