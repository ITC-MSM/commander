package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Crimson Manticore
 * {2}{R}{R}
 * Creature — Manticore
 * 2/2
 *
 * Flying
 * {R}, {T}: This creature deals 1 damage to target attacking or blocking creature.
 */
val CrimsonManticore = card("Crimson Manticore") {
    manaCost = "{2}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Manticore"
    power = 2
    toughness = 2
    oracleText = "Flying\n{R}, {T}: This creature deals 1 damage to target attacking or blocking creature."

    keywords(Keyword.FLYING)
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{R}"), Costs.Tap)
        val creature = target(
            "target attacking or blocking creature",
            TargetPermanent(filter = TargetFilter.AttackingOrBlockingCreature),
        )
        effect = Effects.DealDamage(1, creature)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "140"
        artist = "Daniel Gelon"
        flavorText = "Noted neither for their good looks nor their charm, Crimson Manticores can be fearsome " +
            "allies. As dinner companions, however, they are best left alone."
        imageUri = "https://cards.scryfall.io/normal/front/9/6/96f73f9c-1c4e-4343-bfa0-cc5c4a7a562e.jpg?1783948057"
    }
}
