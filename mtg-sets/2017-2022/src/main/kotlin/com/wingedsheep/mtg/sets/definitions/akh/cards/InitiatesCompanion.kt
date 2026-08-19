package com.wingedsheep.mtg.sets.definitions.akh.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Initiate's Companion
 * {1}{G}
 * Creature — Cat
 * 3/1
 *
 * Whenever this creature deals combat damage to a player, untap target creature or land.
 */
val InitiatesCompanion = card("Initiate's Companion") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Cat"
    oracleText = "Whenever this creature deals combat damage to a player, untap target creature or land."
    power = 3
    toughness = 1

    triggeredAbility {
        trigger = Triggers.DealsCombatDamageToPlayer
        val permanent = target(
            "target creature or land",
            TargetPermanent(filter = TargetFilter(GameObjectFilter.CreatureOrLand)),
        )
        effect = Effects.Untap(permanent)
        description = "Whenever this creature deals combat damage to a player, untap target creature or land."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "174"
        artist = "Dan Murayama Scott"
        flavorText = "\"I'd like to say that it's our pet, but the reverse may be closer to the truth.\"\n—Ixor, initiate of Rhet crop"
        imageUri = "https://cards.scryfall.io/normal/front/0/0/0046b802-bc71-44af-8925-666684d5fc87.jpg?1783936472"
    }
}
