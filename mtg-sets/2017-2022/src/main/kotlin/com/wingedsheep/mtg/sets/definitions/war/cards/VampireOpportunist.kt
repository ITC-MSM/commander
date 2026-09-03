package com.wingedsheep.mtg.sets.definitions.war.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Vampire Opportunist — War of the Spark #110 (canonical printing)
 * {1}{B}
 * Creature — Vampire
 * 2/1
 * {6}{B}: Each opponent loses 2 life and you gain 2 life.
 *
 * The drain is two effects, not one transfer: the loss fans out over every opponent
 * ([Player.EachOpponent]) while the gain stays a flat 2 for the controller regardless of how
 * many opponents there are. In a two-player game the two numbers look linked; in multiplayer
 * they are not.
 */
val VampireOpportunist = card("Vampire Opportunist") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Vampire"
    oracleText = "{6}{B}: Each opponent loses 2 life and you gain 2 life."
    power = 2
    toughness = 1

    activatedAbility {
        cost = Costs.Mana("{6}{B}")
        effect = Effects.Composite(
            Effects.LoseLife(2, EffectTarget.PlayerRef(Player.EachOpponent)),
            Effects.GainLife(2)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "110"
        artist = "Jason Rainville"
        flavorText = "\"I think I cracked a fang.\""
        imageUri = "https://cards.scryfall.io/normal/front/a/8/a8cf21a4-616d-48a5-a104-180c24491761.jpg"
    }
}
