package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.DealDamageEffect

/**
 * Nightscape Master
 * {2}{B}{B}
 * Creature — Zombie Wizard
 * 2/2
 * {U}{U}, {T}: Return target creature to its owner's hand.
 * {R}{R}, {T}: This creature deals 2 damage to target creature.
 */
val NightscapeMaster = card("Nightscape Master") {
    manaCost = "{2}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Zombie Wizard"
    power = 2
    toughness = 2
    oracleText = "{U}{U}, {T}: Return target creature to its owner's hand.\n" +
        "{R}{R}, {T}: This creature deals 2 damage to target creature."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{U}{U}"), Costs.Tap)
        val t = target("target", Targets.Creature)
        effect = Effects.ReturnToHand(t)
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{R}{R}"), Costs.Tap)
        val t = target("target", Targets.Creature)
        effect = DealDamageEffect(2, t)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "113"
        artist = "Andrew Goldhawk"
        imageUri = "https://cards.scryfall.io/normal/front/d/8/d86174b8-dd9e-4ece-bc23-4f9ac50bccd3.jpg?1562938505"
    }
}
