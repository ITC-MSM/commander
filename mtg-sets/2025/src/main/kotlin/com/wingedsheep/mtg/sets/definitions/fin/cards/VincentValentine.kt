package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.effects.TransformEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.scripting.values.EntityNumericProperty
import com.wingedsheep.sdk.scripting.values.EntityReference

/**
 * Vincent Valentine // Galian Beast
 * {2}{B}{B} — Legendary Creature — Assassin 2/2 // Legendary Creature — Werewolf Beast 3/2
 *
 * Front — Vincent Valentine:
 *   Whenever a creature an opponent controls dies, put a number of +1/+1 counters on Vincent
 *   Valentine equal to that creature's power.
 *   Whenever Vincent Valentine attacks, you may transform it.
 *
 * Back — Galian Beast:
 *   Trample, lifelink
 *   When Galian Beast dies, return it to the battlefield tapped (front face up).
 *
 * The counter trigger reads the dying creature's power via
 * `DynamicAmount.EntityProperty(EntityReference.Triggering, EntityNumericProperty.Power)`, which
 * resolves with last-known information once the creature has left the battlefield (CR 112.7a) —
 * matching the official ruling "Use the creature's power as it last existed on the battlefield."
 * Galian Beast's death trigger is a plain `Effects.PutOntoBattlefield(Self, tapped = true)`: a
 * double-faced permanent's non-battlefield characteristics are always its front face (CR 712.8a),
 * so by the time this creature is in the graveyard it has already reverted to the Vincent
 * Valentine face, and a DFC re-entering the battlefield is front-face-up by default — the same
 * mechanism proven by Unstoppable Slasher's "return to the battlefield tapped" trigger.
 */
private val GalianBeast = card("Galian Beast") {
    manaCost = ""
    colorIdentity = "B"
    typeLine = "Legendary Creature — Werewolf Beast"
    oracleText = "Trample, lifelink\n" +
        "When Galian Beast dies, return it to the battlefield tapped (front face up)."
    power = 3
    toughness = 2

    keywords(Keyword.TRAMPLE, Keyword.LIFELINK)

    // When Galian Beast dies, return it to the battlefield tapped (front face up).
    triggeredAbility {
        trigger = Triggers.Dies
        effect = Effects.PutOntoBattlefield(EffectTarget.Self, tapped = true)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "125"
        artist = "Norikatsu Miyoshi"
        flavorText = "\"This body is... the punishment that's been given to me...\""
        imageUri = "https://cards.scryfall.io/normal/back/0/2/028ef608-acfe-4e9d-90db-eca4411ba78a.jpg?1782686505"
    }
}

private val VincentValentineFront = card("Vincent Valentine") {
    manaCost = "{2}{B}{B}"
    colorIdentity = "B"
    typeLine = "Legendary Creature — Assassin"
    oracleText = "Whenever a creature an opponent controls dies, put a number of +1/+1 counters " +
        "on Vincent Valentine equal to that creature's power.\n" +
        "Whenever Vincent Valentine attacks, you may transform it."
    power = 2
    toughness = 2

    // Whenever a creature an opponent controls dies, put a number of +1/+1 counters on
    // Vincent Valentine equal to that creature's power.
    triggeredAbility {
        trigger = Triggers.leavesBattlefield(
            filter = GameObjectFilter.Creature.opponentControls(),
            to = Zone.GRAVEYARD,
            binding = TriggerBinding.ANY
        )
        effect = Effects.AddDynamicCounters(
            Counters.PLUS_ONE_PLUS_ONE,
            DynamicAmount.EntityProperty(EntityReference.Triggering, EntityNumericProperty.Power),
            EffectTarget.Self
        )
    }

    // Whenever Vincent Valentine attacks, you may transform it.
    triggeredAbility {
        trigger = Triggers.Attacks
        effect = MayEffect(effect = TransformEffect(EffectTarget.Self))
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "125"
        artist = "Norikatsu Miyoshi"
        imageUri = "https://cards.scryfall.io/normal/front/0/2/028ef608-acfe-4e9d-90db-eca4411ba78a.jpg?1782686505"
    }
}

val VincentValentine: CardDefinition = CardDefinition.doubleFacedCreature(
    frontFace = VincentValentineFront,
    backFace = GalianBeast
)
