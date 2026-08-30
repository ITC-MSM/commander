package com.wingedsheep.mtg.sets.definitions.avr.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantTriggeredAbility
import com.wingedsheep.sdk.scripting.TriggeredAbility

/**
 * Commander's Authority
 * {4}{W}
 * Enchantment — Aura
 *
 * Enchant creature
 * Enchanted creature has "At the beginning of your upkeep, create a 1/1 white Human creature token."
 *
 * The Cathar's Call shape. The quoted ability is granted *to* the enchanted creature through
 * [GrantTriggeredAbility] (whose filter defaults to the attached creature), and the trigger is
 * installed with [Triggers.YourUpkeep]'s own event and binding passed through verbatim — ANY, not
 * ATTACHED. An ATTACHED-bound trigger is never indexed by the engine's trigger index, so keeping
 * the spec's binding is what makes the card do anything at all.
 *
 * Granting the ability to the creature also lands "your upkeep" and the token's controller on the
 * *creature's* controller rather than the Aura's, which is the printed reading: enchant an
 * opponent's creature and they get the Humans.
 */
val CommandersAuthority = card("Commander's Authority") {
    manaCost = "{4}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\n" +
        "Enchanted creature has \"At the beginning of your upkeep, create a 1/1 white Human creature " +
        "token.\""

    auraTarget = Targets.Creature

    staticAbility {
        ability = GrantTriggeredAbility(
            TriggeredAbility.create(
                trigger = Triggers.YourUpkeep.event,
                binding = Triggers.YourUpkeep.binding,
                effect = Effects.CreateToken(
                    power = 1,
                    toughness = 1,
                    colors = setOf(Color.WHITE),
                    creatureTypes = setOf("Human")
                )
            )
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "13"
        artist = "Johannes Voss"
        flavorText = "\"Wear her symbol with honor, a sign of faith upheld and honest deeds bravely done.\""
        imageUri = "https://cards.scryfall.io/normal/front/0/8/08ef4383-11e7-4426-a04a-058570f46e47.jpg?1783940740"
    }
}
