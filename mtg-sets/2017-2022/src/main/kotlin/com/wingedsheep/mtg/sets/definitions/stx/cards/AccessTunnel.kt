package com.wingedsheep.mtg.sets.definitions.stx.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Access Tunnel — Strixhaven: School of Mages #262 (canonical printing)
 * (no mana cost) · Land
 *
 * {T}: Add {C}.
 * {3}, {T}: Target creature with power 3 or less can't be blocked this turn.
 *
 * A colourless utility land in the Rogue's Passage shape. The first ability is a plain colourless
 * mana ability; the second targets, so it is a regular activated ability that grants the
 * [AbilityFlag.CANT_BE_BLOCKED] flag until end of turn to a creature picked through
 * [Targets.CreatureWithPowerAtMost] — the power check is a targeting restriction, not a resolution
 * condition.
 */
val AccessTunnel = card("Access Tunnel") {
    manaCost = ""
    colorIdentity = ""
    typeLine = "Land"
    oracleText =
        "{T}: Add {C}.\n" +
        "{3}, {T}: Target creature with power 3 or less can't be blocked this turn."

    // {T}: Add {C}.
    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddColorlessMana(1)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    // {3}, {T}: Target creature with power 3 or less can't be blocked this turn.
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{3}"), Costs.Tap)
        val creature = target("target", Targets.CreatureWithPowerAtMost(3))
        effect = Effects.GrantKeyword(AbilityFlag.CANT_BE_BLOCKED, creature)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "262"
        artist = "Alayna Danner"
        flavorText = "Oriq agents stick to the dark corners of Arcavios, unseen yet ever-present."
        imageUri = "https://cards.scryfall.io/normal/front/e/d/edf8eb51-9643-4c54-b38e-e7abea92bbe1.jpg?1783927277"
    }
}
