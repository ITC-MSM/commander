package com.wingedsheep.mtg.sets.definitions.war.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Chainwhip Cyclops — War of the Spark #118 (canonical printing)
 * {4}{R}
 * Creature — Cyclops Warrior
 * 4 / 4
 * {3}{R}: Target creature can't block this turn.
 *
 * The Hall Monitor shape without the tap: a bare [Costs.Mana] and [Effects.CantBlock] over the
 * single bound creature target, whose duration defaults to end of turn. Nothing restricts whose
 * creature it may be, so the plain [Targets.Creature] requirement is correct — the whip is just as
 * happy keeping your own blocker out of the way.
 */
val ChainwhipCyclops = card("Chainwhip Cyclops") {
    manaCost = "{4}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Cyclops Warrior"
    oracleText = "{3}{R}: Target creature can't block this turn."
    power = 4
    toughness = 4

    activatedAbility {
        cost = Costs.Mana("{3}{R}")
        val creature = target("target", Targets.Creature)
        effect = Effects.CantBlock(creature)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "118"
        artist = "Johann Bodin"
        flavorText = "\"You say this Tenth District, not Rubblebelt. But where smash happen, that Rubblebelt. Rubblebelt state of mind.\"\n—Urgdar, cyclops philosopher"
        imageUri = "https://cards.scryfall.io/normal/front/d/2/d2f18df2-5910-4c5d-8b04-6fe5d04e8150.jpg"
    }
}
