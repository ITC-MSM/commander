package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Mudbutton Torchrunner
 * {2}{R}
 * Creature — Goblin Warrior
 * 1/1
 * When this creature dies, it deals 3 damage to any target.
 */
val MudbuttonTorchrunner = card("Mudbutton Torchrunner") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Goblin Warrior"
    power = 1
    toughness = 1
    oracleText = "When this creature dies, it deals 3 damage to any target."

    triggeredAbility {
        trigger = Triggers.Dies
        val damaged = target("any target", Targets.Any)
        effect = Effects.DealDamage(3, damaged)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "185"
        artist = "Steve Ellis"
        flavorText = "The oil sloshes against his skull as he nears his destination: the Frogtosser Games and the lighting of the Flaming Boggart."
        imageUri = "https://cards.scryfall.io/normal/front/5/0/50575eab-6c23-4f63-9667-458416c5caa5.jpg?1783942871"
    }
}
