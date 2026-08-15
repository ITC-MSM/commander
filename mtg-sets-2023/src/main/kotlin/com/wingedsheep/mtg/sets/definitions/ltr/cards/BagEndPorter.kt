package com.wingedsheep.mtg.sets.definitions.ltr.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Bag End Porter
 * {3}{G}
 * Creature — Dwarf
 * 4/4
 *
 * Whenever this creature attacks, it gets +X/+X until end of turn, where X is the number of
 * legendary creatures you control.
 */
val BagEndPorter = card("Bag End Porter") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Dwarf"
    power = 4
    toughness = 4
    oracleText = "Whenever this creature attacks, it gets +X/+X until end of turn, where X is the number of legendary creatures you control."

    triggeredAbility {
        trigger = Triggers.Attacks
        val x = DynamicAmount.AggregateBattlefield(
            player = Player.You,
            filter = GameObjectFilter.Creature.legendary()
        )
        effect = Effects.ModifyStats(x, x, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "153"
        artist = "Daniel Correia"
        flavorText = "\"Dwarves' tongues run on when speaking of their handiwork, they say.\"\n—Glóin"
        imageUri = "https://cards.scryfall.io/normal/front/b/7/b7557170-39a2-49df-823e-958c3eb34801.jpg?1686969228"
    }
}
