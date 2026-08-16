package com.wingedsheep.mtg.sets.definitions.war.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Divine Arrow
 * {1}{W}
 * Instant
 * Divine Arrow deals 4 damage to target attacking or blocking creature.
 */
val DivineArrow = card("Divine Arrow") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Divine Arrow deals 4 damage to target attacking or blocking creature."

    spell {
        val t = target(
            "target attacking or blocking creature",
            TargetCreature(filter = TargetFilter.AttackingOrBlockingCreature)
        )
        effect = Effects.DealDamage(4, t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "10"
        artist = "Kieran Yanner"
        flavorText = "Ravnica's defenders watched in horror as Oketra's shot pierced the body of the pegasus. Gideon tumbled through the air, Blackblade in hand."
        imageUri = "https://cards.scryfall.io/normal/front/7/3/73cbb58a-1b00-4883-9b45-da7ded7317e3.jpg?1783933485"
    }
}
