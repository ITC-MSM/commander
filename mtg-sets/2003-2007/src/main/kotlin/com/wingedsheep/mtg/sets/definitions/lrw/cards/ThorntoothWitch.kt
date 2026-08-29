package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Thorntooth Witch
 * {5}{B}
 * Creature — Treefolk Shaman
 * 3/4
 * Whenever you cast a Treefolk spell, you may have target creature get +3/-3 until end of turn.
 *
 * `Triggers.YouCastSubtype` matches the *spell's* subtype, so a Kindred card with the Treefolk type
 * triggers it as much as a Treefolk creature spell does — which is what the printed noun says.
 *
 * "You may have" is the ability's `optional` shorthand, which lowers to a `Gate.MayDecide` around
 * the pump: the target is chosen as the trigger goes on the stack and the choice to use it is made
 * on resolution, so a target that has left by then simply fizzles.
 */
val ThorntoothWitch = card("Thorntooth Witch") {
    manaCost = "{5}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Treefolk Shaman"
    power = 3
    toughness = 4
    oracleText = "Whenever you cast a Treefolk spell, you may have target creature get +3/-3 until end of turn."

    triggeredAbility {
        trigger = Triggers.YouCastSubtype(Subtype.TREEFOLK)
        val creature = target("target creature", Targets.Creature)
        optional = true
        effect = Effects.ModifyStats(3, -3, creature)
        description = "you may have target creature get +3/-3 until end of turn."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "144"
        artist = "William O'Connor"
        flavorText = "The crone's boughs creaked as she spoke. \"You look peaked, little one. Come, sip from my warm brew. It'll have you blooming in no time.\""
        imageUri = "https://cards.scryfall.io/normal/front/1/4/140265a2-61c3-4731-989c-9d55c27400ec.jpg?1783942882"
    }
}
