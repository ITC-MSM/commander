package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.DealDamageEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Harpoon Sniper
 * {2}{W}
 * Creature — Merfolk Archer
 * 2/2
 * {W}, {T}: This creature deals X damage to target attacking or blocking creature, where X is the
 * number of Merfolk you control.
 *
 * "Attacking or blocking creature" is one printed noun phrase and one shipped filter,
 * [TargetFilter.AttackingOrBlockingCreature] — not an or of two clauses.
 *
 * X counts Merfolk *permanents* you control, so the filter is [GameObjectFilter.Permanent] rather
 * than `.Creature`: Lorwyn's Kindred noncreature permanents carry creature types, and a Kindred
 * Enchantment — Merfolk you control is a Merfolk. Note the scope has to be
 * `battlefield(Player.You, …)` and not `DynamicAmounts.creaturesWithSubtype`, which is
 * [Player.Each] and would count an opponent's Merfolk too. The count is read on resolution, so
 * the Sniper itself is included and a Merfolk that died in response is not.
 */
val HarpoonSniper = card("Harpoon Sniper") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Merfolk Archer"
    power = 2
    toughness = 2
    oracleText = "{W}, {T}: This creature deals X damage to target attacking or blocking " +
        "creature, where X is the number of Merfolk you control."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{W}"), Costs.Tap)
        val t = target(
            "target attacking or blocking creature",
            TargetCreature(filter = TargetFilter.AttackingOrBlockingCreature)
        )
        effect = DealDamageEffect(
            DynamicAmounts.battlefield(
                Player.You,
                GameObjectFilter.Permanent.withSubtype(Subtype.MERFOLK)
            ).count(),
            t
        )
        description = "{W}, {T}: This creature deals X damage to target attacking or blocking " +
            "creature, where X is the number of Merfolk you control."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "19"
        artist = "Dominick Domingo"
        flavorText = "Made from whiskergill bones, merrow spinebows can fire bolts through tree trunks."
        imageUri = "https://cards.scryfall.io/normal/front/c/1/c10ccaef-2a75-43d6-95f2-c3690ae5c87a.jpg?1783942914"
    }
}
