package com.wingedsheep.mtg.sets.definitions.usg.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.PreventDamage
import com.wingedsheep.sdk.scripting.events.DamageType
import com.wingedsheep.sdk.scripting.events.RecipientFilter
import com.wingedsheep.sdk.scripting.events.SourceFilter

/**
 * Fog Bank
 * {1}{U}
 * Creature — Wall
 * 0/2
 * Defender (This creature can't attack.)
 * Flying
 * Prevent all combat damage that would be dealt to and dealt by this creature.
 *
 * Modeled as two static prevention replacement effects — the combat-damage twin of
 * Sandskin, but source-relative ([RecipientFilter.Self] / [SourceFilter.Self]) rather
 * than aura-relative.
 */
val FogBank = card("Fog Bank") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Wall"
    power = 0
    toughness = 2
    oracleText = "Defender (This creature can't attack.)\n" +
        "Flying\n" +
        "Prevent all combat damage that would be dealt to and dealt by this creature."

    keywords(Keyword.DEFENDER, Keyword.FLYING)

    // Prevent combat damage dealt TO this creature.
    replacementEffect(
        PreventDamage(
            amount = null,
            appliesTo = EventPattern.DamageEvent(
                recipient = RecipientFilter.Self,
                damageType = DamageType.Combat
            )
        )
    )

    // Prevent combat damage dealt BY this creature.
    replacementEffect(
        PreventDamage(
            amount = null,
            appliesTo = EventPattern.DamageEvent(
                source = SourceFilter.Self,
                damageType = DamageType.Combat
            )
        )
    )

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "75"
        artist = "Scott Kirschner"
        imageUri = "https://cards.scryfall.io/normal/front/6/a/6ade0d30-5a57-439e-95e8-5f865880031f.jpg?1782720730"

        // Champions of Kamigawa rules update — Walls printed before it received errata granting defender.
        ruling(
            "2004-10-04",
            "As of the Champions of Kamigawa rules update, the Wall creature type no longer inherently " +
                "prevents attacking. All Walls printed before this update received errata granting the " +
                "defender keyword."
        )
    }
}
