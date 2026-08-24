package com.wingedsheep.mtg.sets.definitions.ice.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Brine Shaman
 * {1}{B}
 * Creature — Human Cleric Shaman
 * 1/1
 *
 * {T}, Sacrifice a creature: Target creature gets +2/+2 until end of turn.
 * {1}{U}{U}, Sacrifice a creature: Counter target creature spell.
 *
 * Two independent activated abilities, each a [Costs.Composite] whose printed comma joins the
 * atoms in printed order — the first is Blighted Shaman's pump verbatim, the second pays mana
 * instead of tapping and swaps the payoff for [Effects.CounterSpell]. "Target creature spell" is
 * the stack-zone filter [Targets.CreatureSpell], so no extra condition is needed.
 */
val BrineShaman = card("Brine Shaman") {
    manaCost = "{1}{B}"
    colorIdentity = "BU"
    typeLine = "Creature — Human Cleric Shaman"
    power = 1
    toughness = 1
    oracleText = "{T}, Sacrifice a creature: Target creature gets +2/+2 until end of turn.\n" +
        "{1}{U}{U}, Sacrifice a creature: Counter target creature spell."

    activatedAbility {
        cost = Costs.Composite(Costs.Tap, Costs.Sacrifice(GameObjectFilter.Creature))
        val t = target("target", Targets.Creature)
        effect = Effects.ModifyStats(2, 2, t)
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}{U}{U}"), Costs.Sacrifice(GameObjectFilter.Creature))
        target("target", Targets.CreatureSpell)
        effect = Effects.CounterSpell()
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "115"
        artist = "Cornelius Brudi"
        flavorText = "\"The Shamans of Marit Lage do her bidding in secret, but they do it gladly.\"\n—Halvor Arenson, Kjeldoran Priest"
        imageUri = "https://cards.scryfall.io/normal/front/f/4/f445962c-44a1-4f3f-88d4-17048f8ca9dc.jpg"
    }
}
