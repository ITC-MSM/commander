package com.wingedsheep.mtg.sets.definitions.mh1.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.ninjutsu
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantTriggeredAbility
import com.wingedsheep.sdk.scripting.TriggeredAbility
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Ingenious Infiltrator
 * {2}{U}{B}
 * Creature — Vedalken Ninja
 * 2/3
 * Ninjutsu {U}{B} ({U}{B}, Return an unblocked attacker you control to hand: Put this card onto the battlefield from your hand tapped and attacking.)
 * Whenever a Ninja you control deals combat damage to a player, draw a card.
 *
 * The second line is a *lord-shaped* grant, not a trigger of this creature's own: every Ninja you
 * control — this one included — has "whenever this deals combat damage to a player, draw a card",
 * so it fires once per Ninja that connects. [GrantTriggeredAbility] over a battlefield-scoped
 * [GroupFilter] is how `TriggerDetector` sees that.
 */
val IngeniousInfiltrator = card("Ingenious Infiltrator") {
    manaCost = "{2}{U}{B}"
    colorIdentity = "BU"
    typeLine = "Creature — Vedalken Ninja"
    power = 2
    toughness = 3
    oracleText = "Ninjutsu {U}{B} ({U}{B}, Return an unblocked attacker you control to hand: Put this card onto the battlefield from your hand tapped and attacking.)\n" +
        "Whenever a Ninja you control deals combat damage to a player, draw a card."

    ninjutsu("{U}{B}")

    staticAbility {
        ability = GrantTriggeredAbility(
            ability = TriggeredAbility.create(
                trigger = Triggers.DealsCombatDamageToPlayer.event,
                binding = Triggers.DealsCombatDamageToPlayer.binding,
                effect = Effects.DrawCards(1),
            ),
            filter = GroupFilter(GameObjectFilter.Permanent.withSubtype("Ninja").youControl()),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "204"
        artist = "Jason Rainville"
        imageUri = "https://cards.scryfall.io/normal/front/9/1/919cd266-b796-4a1c-937f-76b565c82495.jpg?1783933081"
    }
}
