package com.wingedsheep.mtg.sets.definitions.blb.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Teapot Slinger
 * {3}{R}
 * Creature — Raccoon Warrior
 * 3/4
 *
 * Menace
 * Whenever you expend 4, this creature deals 2 damage to each opponent.
 * (You expend 4 as you spend your fourth total mana to cast spells during a turn.)
 */
val TeapotSlinger = card("Teapot Slinger") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Raccoon Warrior"
    power = 3
    toughness = 4
    oracleText = "Menace\nWhenever you expend 4, this creature deals 2 damage to each opponent. (You expend 4 as you spend your fourth total mana to cast spells during a turn.)"

    keywords(Keyword.MENACE)

    triggeredAbility {
        trigger = Triggers.Expend(4)
        effect = Effects.DealDamage(2, EffectTarget.PlayerRef(Player.EachOpponent))
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "157"
        artist = "Wisnu Tan"
        flavorText = "\"For the last time it's *porcelain*, not stoneware, you ignorant fool!\""
        imageUri = "https://cards.scryfall.io/normal/front/3/0/30506844-349f-4b68-8cc1-d028c1611cc7.jpg?1721431263"
    }
}
