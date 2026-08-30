package com.wingedsheep.mtg.sets.definitions.p02.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.targets.TargetOpponent

/**
 * Predatory Nightstalker
 * {3}{B}{B}
 * Creature — Nightstalker
 * 3/2
 * When this creature enters, you may have target opponent sacrifice a creature of their choice.
 *
 * Portal Second Age is the card's earliest real-expansion printing, so the canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives here.
 *
 * Cruel Edict's effect behind a consent gate: the opponent is targeted when the trigger goes on
 * the stack, and *they* pick which creature to sacrifice on resolution — sacrifice is not
 * targeting, so shroud and protection don't save the creature.
 */
val PredatoryNightstalker = card("Predatory Nightstalker") {
    manaCost = "{3}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Nightstalker"
    power = 3
    toughness = 2
    oracleText = "When this creature enters, you may have target opponent sacrifice a creature of their choice."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val opponent = target("target", TargetOpponent())
        effect = MayEffect(Effects.Sacrifice(GameObjectFilter.Creature, 1, opponent))
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "82"
        artist = "D. Alexander Gregory"
        imageUri = "https://cards.scryfall.io/normal/front/7/f/7fd4c782-bd7f-495e-8a4d-ea7c9ac1f58b.jpg?1783946472"
    }
}
