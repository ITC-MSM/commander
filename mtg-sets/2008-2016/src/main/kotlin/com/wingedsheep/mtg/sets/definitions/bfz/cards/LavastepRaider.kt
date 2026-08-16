package com.wingedsheep.mtg.sets.definitions.bfz.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Lavastep Raider
 * {R}
 * Creature — Goblin Warrior
 * 1/2
 * {2}{R}: This creature gets +2/+0 until end of turn.
 */
val LavastepRaider = card("Lavastep Raider") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Creature — Goblin Warrior"
    power = 1
    toughness = 2
    oracleText = "{2}{R}: This creature gets +2/+0 until end of turn."

    activatedAbility {
        cost = Costs.Mana("{2}{R}")
        effect = Effects.ModifyStats(2, 0, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "147"
        artist = "Matt Stewart"
        flavorText = "Goblins were first to see the potential of hedrons in the fight against the Eldrazi, for the magical stones came ready-made with pointy bits."
        imageUri = "https://cards.scryfall.io/normal/front/2/4/2428f13f-c445-4eb4-bab1-309f27cab208.jpg?1783938194"
    }
}
