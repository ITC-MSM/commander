package com.wingedsheep.mtg.sets.definitions.atq.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.RedirectDamage
import com.wingedsheep.sdk.scripting.events.RecipientFilter
import com.wingedsheep.sdk.scripting.events.SourceFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Martyrs of Korlis
 * {3}{W}{W}
 * Creature — Human
 * 1/6
 * As long as this creature is untapped, all damage that would be dealt to you by artifacts is
 * dealt to this creature instead.
 *
 * Antiquities engine gap: this is the first card to gate a static [RedirectDamage] replacement on
 * a *condition*. `RedirectDamage` gained an optional `condition: Condition?` (mirroring
 * `PreventDamage.restrictions`), evaluated against the replacement source at the moment damage
 * would be redirected. Here the gate is [Conditions.SourceIsUntapped] — when Martyrs is tapped the
 * condition fails and the damage is dealt to its controller normally; while untapped, all damage
 * from artifact sources to its controller is redirected to Martyrs ([EffectTarget.Self]).
 *
 * The source filter is `GameObjectFilter.Artifact` (the damage source must be an artifact);
 * `RecipientFilter.You` is the replacement's controller (Martyrs's controller).
 */
val MartyrsOfKorlis = card("Martyrs of Korlis") {
    manaCost = "{3}{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human"
    power = 1
    toughness = 6

    oracleText = "As long as this creature is untapped, all damage that would be dealt to you by " +
        "artifacts is dealt to this creature instead."

    replacementEffect(
        RedirectDamage(
            redirectTo = EffectTarget.Self,
            appliesTo = EventPattern.DamageEvent(
                recipient = RecipientFilter.You,
                source = SourceFilter.Matching(GameObjectFilter.Artifact),
            ),
            condition = Conditions.SourceIsUntapped,
        )
    )

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "6"
        artist = "Margaret Organ-Kean"
        imageUri = "https://cards.scryfall.io/normal/front/b/d/bde037b9-4947-4ff7-8ea4-e9f1a7e4ab88.jpg?1562935113"
    }
}
