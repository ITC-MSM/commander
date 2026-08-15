package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantAdditionalLandDrop
import com.wingedsheep.sdk.scripting.GrantDynamicStatsEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Zell Dincht — Final Fantasy #170
 * {2}{R} · Legendary Creature — Human Monk · 0/3
 *
 * You may play an additional land on each of your turns.
 * Zell Dincht gets +1/+0 for each land you control.
 * At the beginning of your end step, return a land you control to its owner's hand.
 *
 * - Extra land drop: [GrantAdditionalLandDrop] (cumulative with similar effects).
 * - The +1/+0 buff is a continuous self-buff whose power bonus is the number of lands you control,
 *   read through projected control via [GrantDynamicStatsEffect]. Not "other" lands — Zell isn't a
 *   land, so every land you control counts.
 * - The end-step bounce is a forced (non-"may") triggered ability targeting a land you control; you
 *   choose which when it's put on the stack and return it to its owner's hand on resolution.
 */
val ZellDincht = card("Zell Dincht") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Legendary Creature — Human Monk"
    power = 0
    toughness = 3
    oracleText = "You may play an additional land on each of your turns.\n" +
        "Zell Dincht gets +1/+0 for each land you control.\n" +
        "At the beginning of your end step, return a land you control to its owner's hand."

    // You may play an additional land on each of your turns.
    staticAbility {
        ability = GrantAdditionalLandDrop(count = 1)
    }

    // Zell Dincht gets +1/+0 for each land you control.
    staticAbility {
        ability = GrantDynamicStatsEffect(
            filter = GroupFilter.source(),
            powerBonus = DynamicAmount.AggregateBattlefield(Player.You, GameObjectFilter.Land),
            toughnessBonus = DynamicAmount.Fixed(0),
        )
    }

    // At the beginning of your end step, return a land you control to its owner's hand.
    triggeredAbility {
        trigger = Triggers.YourEndStep
        target = TargetObject(
            filter = TargetFilter(GameObjectFilter.Land.youControl()),
        )
        effect = Effects.ReturnToHand(EffectTarget.ContextTarget(0))
        description = "At the beginning of your end step, return a land you control to its owner's hand."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "170"
        artist = "Kevin Sidharta"
        flavorText = "\"My weapons are these fists of mine!\""
        imageUri = "https://cards.scryfall.io/normal/front/1/3/135d6b27-9168-4513-9d7d-56edae048857.jpg?1748706397"
    }
}
