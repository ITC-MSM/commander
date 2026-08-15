package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Captain Storm, Cosmium Raider
 * {U}{R}
 * Legendary Creature — Human Pirate
 * 2/2
 * Whenever an artifact you control enters, put a +1/+1 counter on target Pirate you control.
 *
 * The trigger fires whenever any artifact you control enters the battlefield (including tokens
 * such as Treasure or Clue tokens). The target is chosen on announcement (CR 603.3d); if there
 * is no valid Pirate you control when the ability resolves the trigger fizzles (CR 608.2b).
 * Captain Storm itself is a Pirate and is a legal target.
 */
val CaptainStormCosmiumRaider = card("Captain Storm, Cosmium Raider") {
    manaCost = "{U}{R}"
    colorIdentity = "UR"
    typeLine = "Legendary Creature — Human Pirate"
    power = 2
    toughness = 2
    oracleText = "Whenever an artifact you control enters, put a +1/+1 counter on target Pirate you control."

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Artifact.youControl(),
            binding = TriggerBinding.ANY,
        )
        val t = target(
            "target Pirate you control",
            TargetCreature(filter = TargetFilter.CreatureYouControl.withSubtype(Subtype.PIRATE)),
        )
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, t)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "227"
        artist = "Diego Gisbert"
        imageUri = "https://cards.scryfall.io/normal/front/1/4/14c65f5a-10bd-4f9b-b816-46c2240b11ff.jpg?1782694427"
    }
}
