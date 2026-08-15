package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter

/**
 * Hunting Kavu
 * {1}{R}{G}
 * Creature — Kavu
 * 2/3
 * {1}{R}{G}, {T}: Exile this creature and target creature without flying that's attacking you.
 *
 * "Attacking you" is modeled as the attacking state; in the engine an attacking creature is
 * attacking the defending player, so the IsAttacking predicate is the relevant restriction.
 */
val HuntingKavu = card("Hunting Kavu") {
    manaCost = "{1}{R}{G}"
    colorIdentity = "RG"
    typeLine = "Creature — Kavu"
    power = 2
    toughness = 3
    oracleText = "{1}{R}{G}, {T}: Exile this creature and target creature without flying that's attacking you."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}{R}{G}"), Costs.Tap)
        val attacker = target(
            "target creature without flying that's attacking you",
            TargetCreature(
                filter = TargetFilter(
                    GameObjectFilter.Creature.attacking().withoutKeyword(Keyword.FLYING)
                )
            )
        )
        effect = Effects.Composite(
            Effects.Exile(EffectTarget.Self),
            Effects.Exile(attacker)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "252"
        artist = "Scott M. Fischer"
        flavorText = "Dominarians gladly fought alongside kavu . . . until the kavu figured out some Dominarians were quite tasty."
        imageUri = "https://cards.scryfall.io/normal/front/8/9/8943304a-89c9-48b0-97b4-3e1aa690ca4d.jpg?1562922751"
    }
}
