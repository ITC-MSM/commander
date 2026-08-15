package com.wingedsheep.mtg.sets.definitions.eoe.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ForEachPlayerEffect
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Edge Rover
 * {G}
 * Artifact Creature — Robot Scout
 * Reach
 * When this creature dies, each player creates a Lander token.
 * 2/2
 *
 * ForEachPlayerEffect over Player.Each runs the sub-effects with controllerId
 * rebound to the iterating player, so CreateLander() with default controller=null
 * resolves to that player.
 */
val EdgeRover = card("Edge Rover") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Artifact Creature — Robot Scout"
    power = 2
    toughness = 2
    oracleText = "Reach\n" +
        "When this creature dies, each player creates a Lander token. " +
        "(It's an artifact with \"{2}, {T}, Sacrifice this token: Search your library for a basic land card, put it onto the battlefield tapped, then shuffle.\")"

    keywords(Keyword.REACH)

    triggeredAbility {
        trigger = Triggers.Dies
        effect = ForEachPlayerEffect(
            players = Player.Each,
            effects = listOf(Effects.CreateLander())
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "179"
        artist = "Francisco Badilla"
        flavorText = "Charting the Edge, one scan at a time."
        imageUri = "https://cards.scryfall.io/normal/front/9/0/90741ec9-6893-42d4-b510-8664666094e3.jpg?1753683145"
    }
}
