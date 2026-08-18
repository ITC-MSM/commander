package com.wingedsheep.mtg.sets.definitions.mkm.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding

/**
 * Teysa, Opulent Oligarch — Murders at Karlov Manor #234
 * {1}{W}{B} · Legendary Creature — Human Advisor · 2/3
 *
 * The end-step count is evaluated on resolution, so every opponent who lost life this turn
 * contributes one investigation even if that opponent subsequently left the game. The Clue trigger
 * observes every battlefield-to-graveyard move rather than only sacrifices, and its once-per-turn
 * cap is spent when the first qualifying event triggers.
 */
val TeysaOpulentOligarch = card("Teysa, Opulent Oligarch") {
    manaCost = "{1}{W}{B}"
    colorIdentity = "WB"
    typeLine = "Legendary Creature — Human Advisor"
    oracleText = "Deathtouch\n" +
        "At the beginning of your end step, investigate for each opponent who lost life this turn.\n" +
        "Whenever a Clue you control is put into a graveyard from the battlefield, create a 1/1 " +
        "white and black Spirit creature token with flying. This ability triggers only once each turn."
    power = 2
    toughness = 3

    keywords(Keyword.DEATHTOUCH)

    triggeredAbility {
        trigger = Triggers.YourEndStep
        effect = Effects.Investigate(DynamicAmounts.opponentsWhoLostLifeThisTurn())
    }

    triggeredAbility {
        trigger = Triggers.leavesBattlefield(
            filter = GameObjectFilter.Artifact.withSubtype("Clue").youControl(),
            to = Zone.GRAVEYARD,
            binding = TriggerBinding.ANY,
        )
        oncePerTurn = true
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.WHITE, Color.BLACK),
            creatureTypes = setOf("Spirit"),
            keywords = setOf(Keyword.FLYING),
            name = "Spirit",
            imageUri = "https://cards.scryfall.io/normal/front/f/4/" +
                "f4588570-bde4-4c2f-8469-81a3e15fb57b.jpg?1783912607",
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "234"
        artist = "Chris Rallis"
        imageUri = "https://cards.scryfall.io/normal/front/9/b/" +
            "9b5a13dd-c2fd-432e-bc49-cc62d94d62a0.jpg?1783912836"

        ruling(
            "2024-02-02",
            "If an opponent lost life and subsequently lost the game, Teysa's second ability " +
                "still counts that player when determining how many times to investigate.",
        )
        ruling(
            "2024-02-02",
            "If an effect refers to a Clue, it means any Clue artifact, not just a Clue artifact " +
                "token.",
        )
    }
}
