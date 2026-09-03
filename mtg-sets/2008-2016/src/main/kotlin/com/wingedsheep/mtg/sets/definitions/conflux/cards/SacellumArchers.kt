package com.wingedsheep.mtg.sets.definitions.conflux.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Sacellum Archers
 * {2}{G}
 * Creature — Elf Archer
 * 2/3
 * {R}{W}, {T}: This creature deals 2 damage to target attacking or blocking creature.
 *
 * A single activated ability: [Costs.Composite] of mana plus [Costs.Tap], one [TargetObject]
 * narrowed by `GameObjectFilter.Creature.attackingOrBlocking()` — which is exactly the
 * `StatePredicate.Or(IsAttacking, IsBlocking)` the printed line means — and [Effects.DealDamage]
 * pointed at that binding.
 */
val SacellumArchers = card("Sacellum Archers") {
    manaCost = "{2}{G}"
    colorIdentity = "GRW"
    typeLine = "Creature — Elf Archer"
    power = 2
    toughness = 3
    oracleText = "{R}{W}, {T}: This creature deals 2 damage to target attacking or blocking creature."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{R}{W}"), Costs.Tap)
        val creature = target(
            "target",
            TargetObject(filter = TargetFilter(GameObjectFilter.Creature.attackingOrBlocking()))
        )
        effect = Effects.DealDamage(2, creature)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "89"
        artist = "Kev Walker"
        flavorText = "\"Our arrows are aimed not at the sacred behemoths but at those who dare to dream of such a trophy.\""
        imageUri = "https://cards.scryfall.io/normal/front/1/6/160335df-8377-4f72-9d3f-4b1492bd23ea.jpg"
    }
}
