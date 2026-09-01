package com.wingedsheep.mtg.sets.definitions.rtr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.RegenerateEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Trestle Troll
 * {1}{B}{G}
 * Creature — Troll
 * 1/4
 *
 * Defender
 * Reach (This creature can block creatures with flying.)
 * {1}{B}{G}: Regenerate this creature.
 *
 * Canonical printing: Return to Ravnica, the card's earliest real printing.
 *
 * Two evergreen keywords plus the regeneration ability — [RegenerateEffect] is the shipped
 * spelling; there is no `Effects.Regenerate` facade.
 */
val TrestleTroll = card("Trestle Troll") {
    manaCost = "{1}{B}{G}"
    colorIdentity = "BG"
    typeLine = "Creature — Troll"
    oracleText = "Defender\n" +
        "Reach (This creature can block creatures with flying.)\n" +
        "{1}{B}{G}: Regenerate this creature."
    power = 1
    toughness = 4

    keywords(Keyword.DEFENDER, Keyword.REACH)

    activatedAbility {
        cost = Costs.Mana("{1}{B}{G}")
        effect = RegenerateEffect(EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "205"
        artist = "Peter Mohrbacher"
        flavorText = "Unwelcome in Golgari colonies, he found his own dark place from which to represent the Swarm."
        imageUri = "https://cards.scryfall.io/normal/front/6/d/6d224279-83f3-4a29-9fd9-86b72407b87a.jpg?1783940330"
    }
}
