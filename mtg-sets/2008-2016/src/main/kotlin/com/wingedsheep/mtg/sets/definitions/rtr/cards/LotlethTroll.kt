package com.wingedsheep.mtg.sets.definitions.rtr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.scripting.effects.RegenerateEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Lotleth Troll
 * {B}{G}
 * Creature — Zombie Troll
 * 2/1
 *
 * Trample
 * Discard a creature card: Put a +1/+1 counter on this creature.
 * {B}: Regenerate this creature.
 *
 * Canonical printing: Return to Ravnica, the card's earliest real printing.
 *
 * The growth ability's whole cost is a filtered discard — no mana at all — which is
 * [Costs.Discard] with a creature filter. There is no `Effects.Regenerate` facade;
 * [RegenerateEffect] is the shipped spelling (Wolfir Avenger).
 */
val LotlethTroll = card("Lotleth Troll") {
    manaCost = "{B}{G}"
    colorIdentity = "BG"
    typeLine = "Creature — Zombie Troll"
    oracleText = "Trample\n" +
        "Discard a creature card: Put a +1/+1 counter on this creature.\n" +
        "{B}: Regenerate this creature."
    power = 2
    toughness = 1

    keywords(Keyword.TRAMPLE)

    activatedAbility {
        cost = Costs.Discard(GameObjectFilter.Creature)
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
    }

    activatedAbility {
        cost = Costs.Mana("{B}")
        effect = RegenerateEffect(EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "177"
        artist = "Vincent Proce"
        flavorText = "He lurks in the undercity, eager for the corpse haulers to unload their rotting cargo."
        imageUri = "https://cards.scryfall.io/normal/front/3/b/3b628197-f26c-457a-b9a4-c1f1d3e02f3d.jpg?1783940336"
    }
}
