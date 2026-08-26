package com.wingedsheep.mtg.sets.definitions.m10.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Magma Phoenix
 * {3}{R}{R}
 * Creature — Phoenix
 * 3/3
 *
 * Flying
 * When this creature dies, it deals 3 damage to each creature and each player.
 * {3}{R}{R}: Return this card from your graveyard to your hand.
 *
 * - "each creature and each player" is two iterations, not one: a group iteration over every
 *   creature on the battlefield, then a player iteration over every player (Volcanic Fallout's
 *   shape). Inside a group iteration `EffectTarget.Self` is the iterated permanent; inside a
 *   player iteration `EffectTarget.Controller` is the iterated player.
 * - The recursion ability is activated from the *graveyard* — `activateFromZone` is what makes it
 *   legal there, and [Effects.ReturnToHandFromGraveyard] carries the `fromZone` guard that stops
 *   a card exiled in response from returning anyway.
 */
val MagmaPhoenix = card("Magma Phoenix") {
    manaCost = "{3}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Phoenix"
    power = 3
    toughness = 3
    oracleText = "Flying\n" +
        "When this creature dies, it deals 3 damage to each creature and each player.\n" +
        "{3}{R}{R}: Return this card from your graveyard to your hand."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.Dies
        effect = Effects.Composite(
            Effects.ForEachInGroup(
                GroupFilter.AllCreatures,
                Effects.DealDamage(3, EffectTarget.Self),
            ),
            Effects.ForEachPlayer(
                Player.Each,
                listOf(Effects.DealDamage(3, EffectTarget.Controller)),
            ),
        )
        description = "When this creature dies, it deals 3 damage to each creature and each player."
    }

    activatedAbility {
        cost = Costs.Mana("{3}{R}{R}")
        effect = Effects.ReturnToHandFromGraveyard(EffectTarget.Self)
        activateFromZone = Zone.GRAVEYARD
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "148"
        artist = "Raymond Swanland"
        imageUri = "https://cards.scryfall.io/normal/front/6/3/63466db8-270e-4c68-8823-963f993de783.jpg?1783942370"
    }
}
