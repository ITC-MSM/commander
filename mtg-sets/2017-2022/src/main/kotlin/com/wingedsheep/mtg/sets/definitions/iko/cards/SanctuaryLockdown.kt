package com.wingedsheep.mtg.sets.definitions.iko.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Sanctuary Lockdown
 * {2}{W}
 * Enchantment
 * Humans you control get +1/+1.
 * {2}, Tap two untapped Humans you control: Tap target creature an opponent controls.
 *
 * "Tap two untapped Humans you control" is a cost, not an effect: [Costs.TapPermanents] carries the
 * untapped requirement and the you-control restriction itself, so the filter only has to say
 * "Human permanent". Note it is a *permanent* filter rather than a creature one — the printed
 * tribal noun is the whole restriction, and a noncreature Human (an animated Vehicle, say) pays it
 * just as well.
 */
val SanctuaryLockdown = card("Sanctuary Lockdown") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment"
    oracleText = "Humans you control get +1/+1.\n{2}, Tap two untapped Humans you control: Tap target creature an opponent controls."

    staticAbility {
        ability = ModifyStats(
            powerBonus = 1,
            toughnessBonus = 1,
            filter = GroupFilter(GameObjectFilter.Permanent.withSubtype("Human").youControl())
        )
    }

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{2}"),
            Costs.TapPermanents(
                count = 2,
                filter = GameObjectFilter.Permanent.withSubtype("Human")
            )
        )
        val victim = target("target", Targets.CreatureOpponentControls)
        effect = Effects.Tap(victim)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "28"
        artist = "Lius Lasahido"
        flavorText = "\"Drannith gives no quarter to traitors.\"\n—General Kudro"
        imageUri = "https://cards.scryfall.io/normal/front/1/3/1387b7be-f69e-4919-9070-a89654c656f3.jpg"
    }
}
