package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * By Invitation Only
 * {3}{W}{W}
 * Sorcery
 *
 * Choose a number between 0 and 13. Each player sacrifices that many creatures of their choice.
 *
 * Blasphemous Edict with the 13 made a choice: the same `ForceSacrificeEffect` over
 * `Player.Each`, but with [DynamicAmount.XValue] where that card writes `count = 13`.
 * [Effects.ChooseNumberThen] stamps the chosen number onto the resolution context as X, which is
 * exactly what `XValue` reads, so "that many" needs no reference of its own — the number is one
 * value shared by every player's sacrifice, not a fresh choice per player.
 *
 * The count is a *maximum*, not a requirement: a player controlling fewer creatures than the
 * chosen number sacrifices all of them (second ruling), which is `ForceSacrificeEffect`'s own
 * behaviour, and 0 is a legal choice that does nothing.
 */
val ByInvitationOnly = card("By Invitation Only") {
    manaCost = "{3}{W}{W}"
    colorIdentity = "W"
    typeLine = "Sorcery"
    oracleText = "Choose a number between 0 and 13. Each player sacrifices that many creatures of " +
        "their choice."

    spell {
        effect = Effects.ChooseNumberThen(
            then = Effects.Sacrifice(
                GameObjectFilter.Creature,
                count = DynamicAmount.XValue,
                target = EffectTarget.PlayerRef(Player.Each),
            ),
            minValue = 0,
            maxValue = 13,
            prompt = "Choose a number between 0 and 13",
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "5"
        artist = "Micah Epstein"
        flavorText = "The red envelopes guarantee safe passage through the Lurenbraum barrier. " +
            "Olivia left strict instructions: no invitation, no entry, no exceptions."
        imageUri = "https://cards.scryfall.io/normal/front/4/6/46764e49-64da-4a94-b61c-75e006b2c5a9.jpg?1783924927"
        ruling("2021-11-19", "The numbers you may choose include 0 and 13.")
        ruling(
            "2021-11-19",
            "If a player controls fewer than the chosen number of creatures, they will sacrifice " +
                "all their creatures.",
        )
        ruling(
            "2021-11-19",
            "Starting with the player whose turn it is, each player in turn order chooses which " +
                "creatures they will sacrifice, then all the creatures chosen by all players are " +
                "sacrificed at the same time. Players get to know the choices made by players who " +
                "chose before them.",
        )
    }
}
