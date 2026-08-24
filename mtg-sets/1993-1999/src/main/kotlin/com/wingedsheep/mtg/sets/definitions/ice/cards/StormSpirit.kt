package com.wingedsheep.mtg.sets.definitions.ice.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Storm Spirit
 * {3}{G}{W}{U}
 * Creature — Elemental Spirit
 * 3/3
 *
 * Flying
 * {T}: This creature deals 2 damage to target creature.
 *
 * A pinger: `Costs.Tap` plus `Effects.DealDamage` at the ability's own bound target. The damage
 * source is left unset so it defaults to the ability's source, this creature.
 */
val StormSpirit = card("Storm Spirit") {
    manaCost = "{3}{G}{W}{U}"
    colorIdentity = "GUW"
    typeLine = "Creature — Elemental Spirit"
    power = 3
    toughness = 3
    oracleText = "Flying\n" +
        "{T}: This creature deals 2 damage to target creature."

    keywords(Keyword.FLYING)

    activatedAbility {
        cost = Costs.Tap
        val t = target("target", Targets.Creature)
        effect = Effects.DealDamage(2, t)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "303"
        artist = "Pete Venters"
        flavorText = "\"Come to us, with your lightning. Come to us, with your thunder. Serve us with your strength, and smite our foes with your power.\"\n—Steinar Icefist, Balduvian Shaman"
        imageUri = "https://cards.scryfall.io/normal/front/7/a/7a383a5f-4814-4b92-aa80-2a6440a719bc.jpg"
    }
}
