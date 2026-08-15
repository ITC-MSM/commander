package com.wingedsheep.mtg.sets.definitions.m20.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Goblin Smuggler
 * {2}{R}
 * Creature — Goblin Rogue
 * 2/2
 *
 * Haste
 * {T}: Another target creature with power 2 or less can't be blocked this turn.
 *
 * The activated ability taps the source and grants [AbilityFlag.CANT_BE_BLOCKED] (default
 * end-of-turn duration) to a chosen creature. "Another target creature with power 2 or less"
 * is [TargetFilter.Creature] narrowed by `powerAtMost(2)` and `.other()` (excludes the source);
 * the source-exclusion is enforced by the legal-target enumerator.
 *
 * Canonical printing: Core Set 2020 (earliest real printing). Reprinted in Foundations (FDN #540).
 */
val GoblinSmuggler = card("Goblin Smuggler") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Goblin Rogue"
    power = 2
    toughness = 2
    oracleText = "Haste (This creature can attack and {T} as soon as it comes under your control.)\n" +
        "{T}: Another target creature with power 2 or less can't be blocked this turn."
    keywords(Keyword.HASTE)

    activatedAbility {
        cost = Costs.Tap
        val t = target("target", TargetCreature(filter = TargetFilter.Creature.powerAtMost(2).other()))
        effect = Effects.GrantKeyword(AbilityFlag.CANT_BE_BLOCKED, t)
        description = "Another target creature with power 2 or less can't be blocked this turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "144"
        artist = "Dan Murayama Scott"
        flavorText = "\"I am but a humble traveler. I have no taste for sneakery nor thiefiness.\""
        imageUri = "https://cards.scryfall.io/normal/front/9/5/95dc1a65-271c-455a-ae0c-f652444a53ac.jpg?1782708292"
    }
}
