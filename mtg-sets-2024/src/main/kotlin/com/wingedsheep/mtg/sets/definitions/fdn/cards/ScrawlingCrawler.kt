package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Scrawling Crawler
 * {3}
 * Artifact Creature — Phyrexian Construct
 * 3/2
 *
 * At the beginning of your upkeep, each player draws a card.
 * Whenever an opponent draws a card, that player loses 1 life.
 *
 * The symmetric group-draw uses `DrawCards(Player.Each)`; the punisher clause fires per card an
 * opponent draws ([Triggers.OpponentDraws], CR 121.2), draining the player who drew via
 * `Player.TriggeringPlayer`. Cards an opponent draws from the upkeep clause therefore also drain
 * them — the two abilities compose without special-casing.
 */
val ScrawlingCrawler = card("Scrawling Crawler") {
    manaCost = "{3}"
    typeLine = "Artifact Creature — Phyrexian Construct"
    power = 3
    toughness = 2
    oracleText = "At the beginning of your upkeep, each player draws a card.\n" +
        "Whenever an opponent draws a card, that player loses 1 life."

    triggeredAbility {
        trigger = Triggers.YourUpkeep
        effect = Effects.DrawCards(1, EffectTarget.PlayerRef(Player.Each))
        description = "At the beginning of your upkeep, each player draws a card."
    }

    triggeredAbility {
        trigger = Triggers.OpponentDraws
        effect = Effects.LoseLife(1, EffectTarget.PlayerRef(Player.TriggeringPlayer))
        description = "Whenever an opponent draws a card, that player loses 1 life."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "132"
        artist = "Miro Petrov"
        flavorText = "It spreads the machine gospel of New Phyrexia, its task incomplete until all surrender to perfection."
        imageUri = "https://cards.scryfall.io/normal/front/a/1/a1176dcf-40ee-4342-aa74-791b8352e99a.jpg?1782689153"
    }
}
