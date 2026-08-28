package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Abyssal Gorestalker
 * {4}{B}{B}
 * Creature — Horror
 * 6/6
 *
 * When this creature enters, each player sacrifices two creatures of their choice.
 *
 * `ForceSacrificeEffect` already names the players who must sacrifice, and `ForceSacrificeExecutor`
 * walks a multi-player reference itself — prompting each player in turn for their own two, and
 * auto-sacrificing without a prompt for anyone controlling two or fewer. So `Player.Each` goes
 * straight on the effect, which is what Barter in Blood, Blasphemous Edict, Bringer of the Last Gift
 * and By Invitation Only all write.
 *
 * This card used to wrap the same effect in a `ForEachPlayerEffect` over `Player.Each` with the
 * inner target left as the (rebound) controller — a hand-rolled restatement of the loop the executor
 * performs. Argentum Assay's differential is what reported the two spellings.
 */
val AbyssalGorestalker = card("Abyssal Gorestalker") {
    manaCost = "{4}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Horror"
    power = 6
    toughness = 6
    oracleText = "When this creature enters, each player sacrifices two creatures of their choice."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.Sacrifice(
            GameObjectFilter.Creature,
            count = 2,
            target = EffectTarget.PlayerRef(Player.Each),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "87"
        artist = "Maxime Minard"
        imageUri = "https://cards.scryfall.io/normal/front/a/5/a559f77f-1f10-475b-9361-7f297d50f254.jpg?1782694541"
    }
}
