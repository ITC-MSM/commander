package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Flickering Hound
 * {3}{W}
 * Creature — Dog
 * 2/2
 *
 * Whenever you cast a creature spell, exile up to one other target creature you control, then
 * return that card to the battlefield under its owner's control.
 *
 * The creature spell being cast is still on the stack when this triggers, so "other" only has to
 * exclude the Hound itself. With no target chosen the trigger does nothing. The return is a plain
 * move to the battlefield, which puts the card under its owner's control; a token that is exiled
 * ceases to exist and doesn't come back.
 */
val FlickeringHound = card("Flickering Hound") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Dog"
    power = 2
    toughness = 2
    oracleText = "Whenever you cast a creature spell, exile up to one other target creature you " +
        "control, then return that card to the battlefield under its owner's control."

    triggeredAbility {
        trigger = Triggers.YouCastCreature
        val creature = target(
            "up to one other target creature you control",
            TargetCreature(optional = true, filter = TargetFilter.OtherCreatureYouControl),
        )
        effect = Effects.Move(creature, Zone.EXILE)
            .then(Effects.Move(creature, Zone.BATTLEFIELD))
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "7"
        artist = "Chris Rahn"
        flavorText = "It roams the halls for truant students, who suddenly find themselves back in class."
        imageUri = "https://cards.scryfall.io/normal/front/6/8/686f3a25-305d-4f02-8972-eba7b8e9635f.jpg?1789470769"
        inBooster = false
    }
}
