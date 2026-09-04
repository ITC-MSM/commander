package com.wingedsheep.mtg.sets.definitions.roe.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding

/**
 * Soul's Attendant
 * {W}
 * Creature — Human Cleric
 * 1 / 1
 *
 * Whenever another creature enters, you may gain 1 life.
 *
 * Modeling notes:
 *  - The printed word is "**another** creature", so the binding is [TriggerBinding.OTHER] — Soul's
 *    Attendant entering does not trigger itself, but every other creature entering does. Assay
 *    compiles `"binding": "OTHER"` over a `ZoneChangeEvent` to the battlefield, and this matches it.
 *  - The card prints no "you control", so the filter is a bare [GameObjectFilter.Creature]: an
 *    opponent's creature entering triggers this just as one of yours does. This is why the trigger
 *    is spelled with `Triggers.entersBattlefield(...)` rather than `Triggers.OtherCreatureEnters`,
 *    whose filter is scoped to your own side — the same choice Essence Warden documents.
 *  - "you **may** gain 1 life" is a consent gate on resolution, which is what the `optional = true`
 *    shorthand lowers to (`Gate.MayDecide` wrapping the effect — Assay's `Gated`/`Gate.MayDecide`).
 *    Soul Warden, the non-optional twin, is the card without this line.
 */
val SoulsAttendant = card("Soul's Attendant") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Cleric"
    power = 1
    toughness = 1
    oracleText = "Whenever another creature enters, you may gain 1 life."

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Creature,
            binding = TriggerBinding.OTHER
        )
        optional = true
        effect = Effects.GainLife(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "44"
        artist = "Steve Prescott"
        flavorText = "In truth, her own faith was gone, trodden in Ulamog's wake. She pantomimed the blessing in the hope that it would inspire others to continue to struggle."
        imageUri = "https://cards.scryfall.io/normal/front/3/2/3223c0ac-cc22-4886-8919-11273b477cc7.jpg?1783942003"
    }
}
