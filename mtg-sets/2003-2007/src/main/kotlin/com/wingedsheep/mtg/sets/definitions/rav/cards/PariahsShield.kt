package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.RedirectDamage
import com.wingedsheep.sdk.scripting.events.RecipientFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Pariah's Shield — Ravnica: City of Guilds #267
 * {5} · Artifact — Equipment
 *
 * All damage that would be dealt to you is dealt to equipped creature instead.
 * Equip {3}
 *
 * The Equipment half of the Pariah shape (With Great Power . . . is the Aura half): a static
 * [RedirectDamage] whose `redirectTo` is [EffectTarget.EquippedCreature], resolved by
 * `DamageUtils.resolveRedirectTarget` off the Equipment's own `AttachedToComponent`.
 *
 * `RecipientFilter.You` is the Shield's *controller*, so a control change moves the shield with
 * the Equipment. With nothing attached the redirect target resolves to null and damage is dealt to
 * you normally — the card's own ruling.
 */
val PariahsShield = card("Pariah's Shield") {
    manaCost = "{5}"
    colorIdentity = ""
    typeLine = "Artifact — Equipment"
    oracleText = "All damage that would be dealt to you is dealt to equipped creature instead.\nEquip {3}"

    replacementEffect(
        RedirectDamage(
            redirectTo = EffectTarget.EquippedCreature,
            appliesTo = EventPattern.DamageEvent(recipient = RecipientFilter.You)
        )
    )

    equipAbility("{3}")

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "267"
        artist = "Doug Chaffee"
        flavorText = "To bear the shield of the pariah is the highest honor a Boros can receive—and the last."
        imageUri = "https://cards.scryfall.io/normal/front/c/c/cc316f1e-84ce-4013-a150-d537f964a604.jpg?1783943596"

        ruling(
            "2005-10-01",
            "If Pariah's Shield isn't attached to a creature, all damage that would be dealt to you " +
                "is dealt to you normally."
        )
    }
}
