package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantTriggeredAbility
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.TriggeredAbility
import com.wingedsheep.sdk.scripting.events.DamageType

/**
 * Ceremonial Knife
 * {1}
 * Artifact — Equipment
 *
 * Equipped creature gets +1/+0 and has "Whenever this creature deals combat damage, create a
 * Blood token."
 * Equip {2}
 *
 * Two statics over [Filters.EquippedCreature], because the printed line grants both a stat
 * modifier (Layer 7c) and a quoted triggered ability — and a [GrantTriggeredAbility] is read by
 * `TriggerDetector` rather than projected through the layer system, so it stays in its own block
 * instead of joining a `CompositeStaticAbility`.
 *
 * The quoted trigger has no recipient: "deals combat damage" fires on combat damage to *anything*
 * — a player, a planeswalker, or a blocking/blocked creature — so it is
 * `Triggers.dealsDamage(DamageType.Combat)` with the default `RecipientFilter.Any`, not the
 * narrower [Triggers.DealsCombatDamageToPlayer]. Binding stays SELF: inside the granted ability
 * "this creature" is the equipped creature that received it, and that creature's controller is the
 * one who gets the Blood token.
 */
val CeremonialKnife = card("Ceremonial Knife") {
    manaCost = "{1}"
    colorIdentity = ""
    typeLine = "Artifact — Equipment"
    oracleText = "Equipped creature gets +1/+0 and has \"Whenever this creature deals combat " +
        "damage, create a Blood token.\" (It's an artifact with \"{1}, {T}, Discard a card, " +
        "Sacrifice this token: Draw a card.\")\n" +
        "Equip {2}"

    staticAbility {
        ability = ModifyStats(1, 0, Filters.EquippedCreature)
    }

    staticAbility {
        ability = GrantTriggeredAbility(
            ability = TriggeredAbility.create(
                trigger = Triggers.dealsDamage(DamageType.Combat).event,
                binding = Triggers.dealsDamage(DamageType.Combat).binding,
                effect = Effects.CreateBlood(),
                descriptionOverride = "Whenever this creature deals combat damage, create a Blood token."
            )
        )
    }

    equipAbility("{2}")

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "254"
        artist = "Antonio José Manzanedo"
        imageUri = "https://cards.scryfall.io/normal/front/9/c/9ccb4b1e-ef8f-4c5f-8b5b-6148455442f7.jpg?1783924786"

        ruling(
            "2025-01-24",
            "If an effect refers to a Blood token, it means any artifact token with the subtype " +
                "Blood, even if it has gained other subtypes."
        )
        ruling("2025-01-24", "You can't sacrifice a Blood token to pay multiple costs.")
    }
}
