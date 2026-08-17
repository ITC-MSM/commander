package com.wingedsheep.mtg.sets.definitions.msh.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.HealOtherDamage
import com.wingedsheep.sdk.scripting.events.RecipientFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Wolverine, Fierce Fighter — Marvel Super Heroes #240 (rare)
 * {2}{R}{G} · Legendary Creature — Mutant Berserker Hero · 3/5
 *
 * Haste
 * When Wolverine enters, he fights up to one other target creature.
 * If damage would be dealt to Wolverine, instead that damage is dealt, but all other damage
 * already dealt to him is healed.
 *
 * The healing factor is the set's only **heal** (CR 701.69a) card and the only damage replacement
 * in the SDK that leaves the amount alone: `HealOtherDamage` lets the damage through in full and
 * wipes whatever was marked before it. Net effect — Wolverine only ever carries the most recent
 * damage *event*, so he survives any number of separate 4-damage hits but still dies to a single
 * 5, and still dies to deathtouch or to −X/−X shrinking him under his marked damage.
 *
 * "All combat damage is dealt simultaneously" (CR 510.2) is what makes double-blocking him work:
 * the heal fires once for the step, so two 3-power blockers still stack 6 damage on him and kill
 * him. The first-strike step is a *separate* event, so a first-striker's damage is healed away
 * before the regular step — which is the card's real trick.
 *
 * The ETB is an ordinary `Effects.Fight` with an optional `OtherCreature` target ("up to one
 * other"), so it resolves harmlessly when the board is empty or the target is removed in response.
 */
val WolverineFierceFighter = card("Wolverine, Fierce Fighter") {
    manaCost = "{2}{R}{G}"
    colorIdentity = "RG"
    typeLine = "Legendary Creature — Mutant Berserker Hero"
    power = 3
    toughness = 5
    oracleText = "Haste\n" +
        "When Wolverine enters, he fights up to one other target creature.\n" +
        "If damage would be dealt to Wolverine, instead that damage is dealt, but all other " +
        "damage already dealt to him is healed."

    keywords(Keyword.HASTE)

    // When Wolverine enters, he fights up to one other target creature.
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val foe = target(
            "up to one other target creature",
            TargetObject(optional = true, filter = TargetFilter.OtherCreature)
        )
        effect = Effects.Fight(EffectTarget.Self, foe)
    }

    // Healing factor: the damage is dealt in full, but everything marked before it is healed.
    replacementEffect(
        HealOtherDamage(appliesTo = EventPattern.DamageEvent(recipient = RecipientFilter.Self))
    )

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "240"
        artist = "Dan Brereton"
        flavorText = "\"You picked the wrong dance partner, bub.\""
        imageUri = "https://cards.scryfall.io/normal/front/c/1/c1c7aa22-51b0-45ee-9a8e-5493a1820d8c.jpg?1783902893"
    }
}
