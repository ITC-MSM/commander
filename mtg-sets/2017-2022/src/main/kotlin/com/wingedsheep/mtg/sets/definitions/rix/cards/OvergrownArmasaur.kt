package com.wingedsheep.mtg.sets.definitions.rix.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Overgrown Armasaur
 * {3}{G}{G}
 * Creature — Dinosaur
 * 4/4
 * Enrage — Whenever this creature is dealt damage, create a 1/1 green Saproling creature token.
 */
val OvergrownArmasaur = card("Overgrown Armasaur") {
    manaCost = "{3}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Dinosaur"
    oracleText = "Enrage — Whenever this creature is dealt damage, create a 1/1 green Saproling " +
        "creature token."
    power = 4
    toughness = 4

    triggeredAbility {
        trigger = Triggers.TakesDamage
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.GREEN),
            creatureTypes = setOf("Saproling"),
        )
        description = "Enrage — Whenever this creature is dealt damage, create a 1/1 green " +
            "Saproling creature token."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "141"
        artist = "Joseph Meehan"
        flavorText = "It embodies the riotous growth of the jungle, where creatures crowd upon " +
            "creatures, where roots sprawl and vines swarm."
        imageUri = "https://cards.scryfall.io/normal/front/f/b/fb6558db-6332-42ac-8a61-4524c200b62f.jpg?1783935284"
        ruling(
            "2018-01-19",
            "If multiple sources deal damage to a creature with an enrage ability at the same " +
                "time, most likely because multiple creatures blocked that creature, the enrage " +
                "ability triggers only once."
        )
        ruling(
            "2018-01-19",
            "If lethal damage is dealt to a creature with an enrage ability, that ability " +
                "triggers. The creature with that enrage ability leaves the battlefield before " +
                "that ability resolves, so it won't be affected by the resolving ability."
        )
    }
}
