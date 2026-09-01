package com.wingedsheep.mtg.sets.definitions.rtr.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Gobbling Ooze
 * {4}{G}
 * Creature — Ooze
 * 3/3
 *
 * {G}, Sacrifice another creature: Put a +1/+1 counter on this creature.
 *
 * Canonical printing: Return to Ravnica, the card's earliest real printing.
 *
 * "Another creature" is [Costs.SacrificeAnother], which is [Costs.Sacrifice] with the source
 * excluded — the Ooze can never eat itself to grow.
 */
val GobblingOoze = card("Gobbling Ooze") {
    manaCost = "{4}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Ooze"
    oracleText = "{G}, Sacrifice another creature: Put a +1/+1 counter on this creature."
    power = 3
    toughness = 3

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{G}"),
            Costs.SacrificeAnother(GameObjectFilter.Creature),
        )
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "126"
        artist = "Johann Bodin"
        flavorText = "The furious citizens blamed the Simic for releasing it in their district. The Simic pointed out that rats were no longer a problem."
        imageUri = "https://cards.scryfall.io/normal/front/4/6/465d8a63-0ced-4aec-be34-2098b72c8af6.jpg?1783940348"
    }
}
