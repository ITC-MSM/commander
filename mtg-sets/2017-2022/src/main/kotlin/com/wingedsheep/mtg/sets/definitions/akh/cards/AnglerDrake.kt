package com.wingedsheep.mtg.sets.definitions.akh.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Angler Drake
 * {4}{U}{U}
 * Creature — Drake
 * 4/4
 * Flying
 * When this creature enters, you may return target creature to its owner's hand.
 *
 * The target is mandatory at announcement — the trigger carries a `targetRequirement`, so a
 * creature is chosen when the ability goes on the stack — and the printed "you may" is only the
 * resolution-time yes/no (`optional = true` lowers to a `Gate.MayDecide`).
 */
val AnglerDrake = card("Angler Drake") {
    manaCost = "{4}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Drake"
    oracleText = "Flying\n" +
            "When this creature enters, you may return target creature to its owner's hand."
    power = 4
    toughness = 4

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        optional = true
        val victim = target("target", TargetObject(filter = TargetFilter.Creature))
        effect = Effects.ReturnToHand(victim)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "41"
        artist = "Svetlin Velinov"
        flavorText = "From the time they are hatchlings, river drakes are taught to pull the largest prey from the Luxa."
        imageUri = "https://cards.scryfall.io/normal/front/d/1/d14c753e-c5bf-4c34-b408-ac367b6ca6ab.jpg?1783936527"
    }
}
