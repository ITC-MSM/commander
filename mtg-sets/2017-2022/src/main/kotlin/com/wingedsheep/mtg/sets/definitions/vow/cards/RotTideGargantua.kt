package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.exploit
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Rot-Tide Gargantua
 * {3}{B}{B}
 * Creature — Zombie Kraken
 * 5/4
 * Exploit (When this creature enters, you may sacrifice a creature.)
 * When this creature exploits a creature, each opponent sacrifices a creature of their choice.
 *
 * The payoff is untargeted (an edict resolved at resolution time), so it's baked straight into the
 * exploit reflexive ([exploit]'s `onExploit`). [Effects.Sacrifice] aimed at [Player.EachOpponent]
 * makes each opponent choose one of their own creatures to sacrifice (same shape as Tithing Blade).
 */
val RotTideGargantua = card("Rot-Tide Gargantua") {
    manaCost = "{3}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Zombie Kraken"
    power = 5
    toughness = 4
    oracleText = "Exploit (When this creature enters, you may sacrifice a creature.)\n" +
        "When this creature exploits a creature, each opponent sacrifices a creature of their choice."

    exploit(
        onExploit = Effects.Sacrifice(
            GameObjectFilter.Creature,
            target = EffectTarget.PlayerRef(Player.EachOpponent)
        )
    )

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "129"
        artist = "Filip Burburan"
        imageUri = "https://cards.scryfall.io/normal/front/1/e/1eafefd0-3a9e-400e-8c75-0825aeb2ded1.jpg?1783924850"
    }
}
