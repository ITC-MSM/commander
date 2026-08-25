package com.wingedsheep.mtg.sets.definitions.mh1.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Farmstead Gleaner
 * {3}
 * Artifact Creature — Scarecrow
 * 2/2
 * This creature doesn't untap during your untap step.
 * {2}, {Q}: Put a +1/+1 counter on this creature. ({Q} is the untap symbol.)
 *
 * The two halves are one engine: [AbilityFlag.DOESNT_UNTAP] is the self-suppression flag the untap
 * step filters on (cf. Basalt Monolith), and `{Q}` — [Costs.Untap] — is the only way the Gleaner
 * ever untaps, so every counter costs it a turn's worth of attacking or blocking. `{Q}` is a cost
 * atom like `{T}`, not an effect: it can only be paid while the creature is untapped.
 */
val FarmsteadGleaner = card("Farmstead Gleaner") {
    manaCost = "{3}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Scarecrow"
    power = 2
    toughness = 2
    oracleText = "This creature doesn't untap during your untap step.\n" +
        "{2}, {Q}: Put a +1/+1 counter on this creature. ({Q} is the untap symbol.)"

    flags(AbilityFlag.DOESNT_UNTAP)

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}"), Costs.Untap)
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
        description = "{2}, {Q}: Put a +1/+1 counter on this creature."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "222"
        artist = "Josh Hass"
        flavorText = "When it finishes the harvest, you'll have nowhere to hide."
        imageUri = "https://cards.scryfall.io/normal/front/e/d/edafd52f-2dda-4981-baee-404f47ee8969.jpg?1783933074"
    }
}
