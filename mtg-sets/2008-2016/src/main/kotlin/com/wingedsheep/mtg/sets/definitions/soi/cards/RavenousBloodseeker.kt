package com.wingedsheep.mtg.sets.definitions.soi.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Ravenous Bloodseeker (Shadows over Innistrad #175)
 * {1}{R}
 * Creature — Vampire Berserker
 * 1 / 3
 *
 * Discard a card: This creature gets +2/-2 until end of turn.
 *
 * The whole ability is a cost and a pump: [Costs.DiscardCard] is the discard atom, and the pump
 * targets the source itself rather than a chosen creature, so there is no target requirement. The
 * toughness modifier is negative, which is what lets the ability kill its own creature — the state-
 * based check runs after each activation.
 */
val RavenousBloodseeker = card("Ravenous Bloodseeker") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Vampire Berserker"
    power = 1
    toughness = 3
    oracleText = "Discard a card: This creature gets +2/-2 until end of turn."

    activatedAbility {
        cost = Costs.DiscardCard
        effect = Effects.ModifyStats(2, -2, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "175"
        artist = "James Ryman"
        flavorText = "\"Nothing will cool the fire in their blood. They are too far gone. We must keep them away from our towns at any cost.\"\n—Cosper Lowe of the Silbern Guard"
        imageUri = "https://cards.scryfall.io/normal/front/c/c/cc4d04bc-6263-43b0-9b01-894a532cd3ed.jpg?1783937746"
    }
}
