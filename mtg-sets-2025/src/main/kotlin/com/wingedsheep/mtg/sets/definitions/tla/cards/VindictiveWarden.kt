package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.firebending
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Vindictive Warden
 * {2}{B/R}
 * Creature — Human Soldier
 * 2/3
 *
 * Menace (This creature can't be blocked except by two or more creatures.)
 * Firebending 1 (Whenever this creature attacks, add {R}. This mana lasts until end of combat.)
 * {3}: This creature deals 1 damage to each opponent.
 */
val VindictiveWarden = card("Vindictive Warden") {
    manaCost = "{2}{B/R}"
    colorIdentity = "BR"
    typeLine = "Creature — Human Soldier"
    power = 2
    toughness = 3
    oracleText = "Menace (This creature can't be blocked except by two or more creatures.)\n" +
        "Firebending 1 (Whenever this creature attacks, add {R}. This mana lasts until end of combat.)\n" +
        "{3}: This creature deals 1 damage to each opponent."

    keywords(Keyword.MENACE)

    firebending(1)

    activatedAbility {
        cost = Costs.Mana("{3}")
        effect = Effects.DealDamage(1, EffectTarget.PlayerRef(Player.EachOpponent))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "249"
        artist = "Jo Cordisco"
        imageUri = "https://cards.scryfall.io/normal/front/0/f/0f34de05-39a9-425f-a3b4-3a9d46c917ac.jpg?1764121838"
    }
}
