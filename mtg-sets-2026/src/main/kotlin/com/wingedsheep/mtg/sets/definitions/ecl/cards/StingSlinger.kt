package com.wingedsheep.mtg.sets.definitions.ecl.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Sting-Slinger
 * {2}{R}
 * Creature — Goblin Warrior
 * 3/3
 *
 * {1}{R}, {T}, Blight 1: This creature deals 2 damage to each opponent.
 * (To blight 1, put a -1/-1 counter on a creature you control.)
 */
val StingSlinger = card("Sting-Slinger") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Goblin Warrior"
    power = 3
    toughness = 3
    oracleText = "{1}{R}, {T}, Blight 1: This creature deals 2 damage to each opponent. " +
        "(To blight 1, put a -1/-1 counter on a creature you control.)"

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}{R}"), Costs.Tap, Costs.Blight(1))
        effect = Effects.DealDamage(
            amount = 2,
            target = EffectTarget.PlayerRef(Player.EachOpponent),
            damageSource = EffectTarget.Self
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "161"
        artist = "Ralph Horsley"
        flavorText = "\"The first step is to wuzzle the buzzers.\""
        imageUri = "https://cards.scryfall.io/normal/front/3/8/386c5f73-fb8f-46c8-ad45-56e2c19b7d1f.jpg?1767957220"
    }
}
