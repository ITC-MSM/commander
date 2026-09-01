package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.PreventDamage
import com.wingedsheep.sdk.scripting.events.DamageType
import com.wingedsheep.sdk.scripting.events.RecipientFilter

/**
 * Dolmen Gate
 * {2}
 * Artifact
 *
 * Prevent all combat damage that would be dealt to attacking creatures you control.
 *
 * A static prevention shield in the Fog Bank / Daunting Defender family — a [PreventDamage]
 * replacement effect with no amount (prevent *all* of it) whose recipient is a live filter
 * rather than a fixed entity. The filter is re-read when each damage instance would be dealt,
 * so a creature that stops attacking, or that comes under your control mid-combat, is judged
 * as it is at that moment.
 *
 * [DamageType.Combat] is load-bearing: the shield covers only combat damage, so a burn spell
 * aimed at an attacker of yours still connects. "You control" scopes to the Gate's controller,
 * so it does nothing for an opponent's attackers — it only protects your own alpha strike.
 */
val DolmenGate = card("Dolmen Gate") {
    manaCost = "{2}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "Prevent all combat damage that would be dealt to attacking creatures you control."

    replacementEffect(
        PreventDamage(
            amount = null,
            appliesTo = EventPattern.DamageEvent(
                recipient = RecipientFilter.Matching(
                    GameObjectFilter.Creature.youControl().attacking()
                ),
                damageType = DamageType.Combat
            )
        )
    )

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "256"
        artist = "Richard Sardinha"
        flavorText = "Lorwyn's stones resonate with the place from which they were hewed. " +
            "Though taken far, still they call to their home when silence is upon the land."
        imageUri = "https://cards.scryfall.io/normal/front/f/d/fdcbf10e-32f2-41c5-a7a2-5f24662892d2.jpg?1783942851"
    }
}
