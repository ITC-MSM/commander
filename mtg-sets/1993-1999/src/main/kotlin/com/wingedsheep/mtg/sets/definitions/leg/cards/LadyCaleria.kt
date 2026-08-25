package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Lady Caleria
 * {3}{G}{G}{W}{W}
 * Legendary Creature — Elf Archer
 * 3/6
 *
 * {T}: Lady Caleria deals 3 damage to target attacking or blocking creature.
 */
val LadyCaleria = card("Lady Caleria") {
    manaCost = "{3}{G}{G}{W}{W}"
    colorIdentity = "GW"
    typeLine = "Legendary Creature — Elf Archer"
    power = 3
    toughness = 6
    oracleText = "{T}: Lady Caleria deals 3 damage to target attacking or blocking creature."

    activatedAbility {
        cost = Costs.Tap
        val creature = target(
            "target attacking or blocking creature",
            TargetPermanent(filter = TargetFilter.AttackingOrBlockingCreature),
        )
        effect = Effects.DealDamage(3, creature)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "239"
        artist = "Bryon Wackwitz"
        imageUri = "https://cards.scryfall.io/normal/front/d/6/d6914ed2-9207-4689-9166-11d2f8949fdd.jpg?1783948036"
    }
}
