package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Reroute — Ravnica: City of Guilds #139
 * {1}{R} · Instant · Uncommon
 *
 * Change the target of target activated ability with a single target. (Mana abilities can't be
 * targeted.)
 * Draw a card.
 *
 * The Willbender / Bolt Bend redirect narrowed to activated abilities: `TargetFilter.
 * ActivatedAbilityOnStack` admits only activated abilities (a mana ability never uses the stack,
 * so the reminder text is free), and [Effects.ChangeTarget] asks the caster for a new target
 * drawn from the ability's *own* requirement — the redirect can never make an illegal choice
 * legal. The draw follows unconditionally, as printed: an ability with no other legal target is
 * left alone and you still draw.
 *
 * "With a single target" is enforced where the shared redirect shape enforces it — at
 * resolution, where an ability carrying two targets is left untouched — rather than as a
 * targeting restriction; the same reading Willbender and Bolt Bend already ship with.
 */
val Reroute = card("Reroute") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Change the target of target activated ability with a single target. (Mana " +
        "abilities can't be targeted.)\nDraw a card."

    spell {
        target(
            "activated ability with a single target",
            TargetObject(filter = TargetFilter.ActivatedAbilityOnStack)
        )
        effect = Effects.ChangeTarget().then(Effects.DrawCards(1))
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "139"
        artist = "Christopher Rush"
        flavorText = "Three hundred years of practice thwarted by an instant of mischief."
        imageUri = "https://cards.scryfall.io/normal/front/4/2/42794e10-ddcd-4d2d-ab0c-a6b99b6d4662.jpg?1783943648"
        ruling(
            "2016-06-08",
            "Activated abilities contain a colon. They're generally written \"[Cost]: [Effect].\" " +
                "Some keywords are activated abilities and will have colons in their reminder text."
        )
    }
}
