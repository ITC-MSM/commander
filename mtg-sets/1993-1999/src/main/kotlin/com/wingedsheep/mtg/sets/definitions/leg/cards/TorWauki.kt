package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Tor Wauki
 * {2}{B}{B}{R}
 * Legendary Creature — Human Archer
 * 3/3
 *
 * {T}: Tor Wauki deals 2 damage to target attacking or blocking creature.
 */
val TorWauki = card("Tor Wauki") {
    manaCost = "{2}{B}{B}{R}"
    colorIdentity = "BR"
    typeLine = "Legendary Creature — Human Archer"
    power = 3
    toughness = 3
    oracleText = "{T}: Tor Wauki deals 2 damage to target attacking or blocking creature."

    activatedAbility {
        cost = Costs.Tap
        val creature = target(
            "target attacking or blocking creature",
            TargetPermanent(filter = TargetFilter.AttackingOrBlockingCreature),
        )
        effect = Effects.DealDamage(2, creature)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "265"
        artist = "Randy Asplund-Faith"
        imageUri = "https://cards.scryfall.io/normal/front/2/4/241a4854-e62c-4be4-a9cc-1e14db4eede9.jpg?1783948031"
    }
}
