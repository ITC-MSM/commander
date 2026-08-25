package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * D'Avenant Archer
 * {2}{W}
 * Creature — Human Soldier Archer
 * 1/2
 *
 * {T}: This creature deals 1 damage to target attacking or blocking creature.
 */
val DAvenantArcher = card("D'Avenant Archer") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Soldier Archer"
    power = 1
    toughness = 2
    oracleText = "{T}: This creature deals 1 damage to target attacking or blocking creature."

    activatedAbility {
        cost = Costs.Tap
        val creature = target(
            "target attacking or blocking creature",
            TargetPermanent(filter = TargetFilter.AttackingOrBlockingCreature),
        )
        effect = Effects.DealDamage(1, creature)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "7"
        artist = "Douglas Shuler"
        imageUri = "https://cards.scryfall.io/normal/front/b/0/b09aee5c-8b9e-46c2-b4d4-508062f8af05.jpg?1783948087"
    }
}
