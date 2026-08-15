package com.wingedsheep.mtg.sets.definitions.dom.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Cabal Paladin
 * {3}{B}
 * Creature — Human Knight
 * 4/2
 * Whenever you cast a historic spell, Cabal Paladin deals 2 damage to each opponent.
 * (Artifacts, legendaries, and Sagas are historic.)
 */
val CabalPaladin = card("Cabal Paladin") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Human Knight"
    power = 4
    toughness = 2
    oracleText = "Whenever you cast a historic spell, Cabal Paladin deals 2 damage to each opponent. (Artifacts, legendaries, and Sagas are historic.)"

    triggeredAbility {
        trigger = Triggers.YouCastHistoric
        effect = Effects.DealDamage(2, EffectTarget.PlayerRef(Player.EachOpponent))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "79"
        artist = "Lius Lasahido"
        imageUri = "https://cards.scryfall.io/normal/front/2/c/2cff2cb9-f4e6-4fee-94ef-ad11e24525c1.jpg?1562733313"
    }
}
