package com.wingedsheep.mtg.sets.definitions.roe.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Goblin Tunneler — Rise of the Eldrazi #148
 * {1}{R} · Creature — Goblin Rogue · 1 / 1
 *
 * {T}: Target creature with power 2 or less can't be blocked this turn.
 *
 * The Crafty Pathmage shape: the power restriction lives on the target requirement
 * ([Targets.CreatureWithPowerAtMost]), so it is checked on activation and again on resolution
 * (CR 608.2b), while the effect itself is a flat [AbilityFlag.CANT_BE_BLOCKED] grant for the turn.
 * Growing the creature's power after the grant does not strip it — that falls out of granting the
 * flag rather than re-checking power during combat.
 */
val GoblinTunneler = card("Goblin Tunneler") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Goblin Rogue"
    power = 1
    toughness = 1
    oracleText = "{T}: Target creature with power 2 or less can't be blocked this turn."

    activatedAbility {
        cost = Costs.Tap
        val creature = target("target creature", Targets.CreatureWithPowerAtMost(2))
        effect = Effects.GrantKeyword(AbilityFlag.CANT_BE_BLOCKED, creature)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "148"
        artist = "Jesper Ejsing"
        flavorText = "\"You never know what's going to be in goblin tunnels. Even in the best case, there will still be goblins.\"\n" +
            "—Sachir, Akoum Expeditionary House"
        imageUri = "https://cards.scryfall.io/normal/front/0/b/0b2e4a34-6255-4f89-a62d-941996c573e1.jpg?1783941975"
    }
}
