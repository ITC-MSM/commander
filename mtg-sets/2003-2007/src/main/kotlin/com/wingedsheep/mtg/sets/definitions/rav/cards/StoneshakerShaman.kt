package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Stoneshaker Shaman
 * {2}{R}
 * Creature — Human Shaman
 * 1/1
 *
 * At the beginning of each player's end step, that player sacrifices an untapped land of their
 * choice.
 *
 * "Each player's end step" is [Triggers.EachEndStep] — a `StepEvent(END, Player.Each)`. Only the
 * active player has an end step in a given turn, so this fires once per turn and
 * [Player.TriggeringPlayer] is whoever's turn it is. The Shaman's controller is not spared: the
 * sentence names the player whose step it is, not an opponent, which is what makes this a
 * symmetric card.
 *
 * "Of their choice" is the ordinary [Effects.Sacrifice] semantics — the sacrificing player picks,
 * not the Shaman's controller — and the `untapped()` predicate keeps a player who has tapped out
 * for the turn from choosing a land they've already used. Per the ruling below, a player with no
 * untapped land simply does nothing; `ForceSacrificeEffect` sacrifices as many as it can find
 * rather than failing.
 */
val StoneshakerShaman = card("Stoneshaker Shaman") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Human Shaman"
    oracleText = "At the beginning of each player's end step, that player sacrifices an untapped " +
        "land of their choice."
    power = 1
    toughness = 1

    triggeredAbility {
        trigger = Triggers.EachEndStep
        effect = Effects.Sacrifice(
            filter = GameObjectFilter.Land.untapped(),
            count = 1,
            target = EffectTarget.PlayerRef(Player.TriggeringPlayer)
        )
        description = "At the beginning of each player's end step, that player sacrifices an " +
            "untapped land of their choice."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "145"
        artist = "Jeff Miracola"
        flavorText = "There is no place in Ravnica for stagnation, no room for lands unfilled and " +
            "untilled."
        imageUri = "https://cards.scryfall.io/normal/front/1/7/17a0edb0-697a-4e0b-872b-f3c15c19cbda.jpg?1783943646"
        ruling(
            "2005-10-01",
            "If the player doesn't have an untapped land as Stoneshaker Shaman's ability resolves, " +
                "that player does nothing."
        )
    }
}
