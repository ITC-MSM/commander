package com.wingedsheep.mtg.sets.definitions.roe.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Death Cultist
 * {B}
 * Creature — Human Wizard
 * 1 / 1
 *
 * Sacrifice this creature: Target player loses 1 life and you gain 1 life.
 *
 * Modeling notes:
 *  - The sacrifice is a **cost**, not an effect: the whole ability is `Sacrifice this creature:`
 *    before the colon, so it uses [Costs.SacrificeSelf] (the corpus's `CostSacrificeSelf` atom,
 *    which is exactly what Assay compiles this line to) rather than an `Effects.Sacrifice` in the
 *    resolution. The Cultist is therefore already in the graveyard when the drain resolves, and the
 *    ability still resolves if it is somehow removed in response.
 *  - "Target player" is the full player target, not "target opponent": [Targets.Player] binds a
 *    handle that [Effects.LoseLife] consumes. Passing the bound handle matters — `Effects.LoseLife`
 *    defaults to `Player.TargetOpponent`, which would silently narrow the printed wording.
 *  - "loses 1 life **and** you gain 1 life" is one resolution with two ordered halves, so it is an
 *    [Effects.Composite] of a targeted `LoseLife` and an untargeted `GainLife` (whose default
 *    controller recipient is already correct — no explicit `EffectTarget.Controller`). This is not
 *    lifelink-style linked drain: the gain happens even if the loss is replaced or prevented.
 */
val DeathCultist = card("Death Cultist") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Creature — Human Wizard"
    power = 1
    toughness = 1
    oracleText = "Sacrifice this creature: Target player loses 1 life and you gain 1 life."

    activatedAbility {
        cost = Costs.SacrificeSelf
        val player = target("target player", Targets.Player)
        effect = Effects.Composite(
            Effects.LoseLife(1, player),
            Effects.GainLife(1)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "105"
        artist = "Igor Kieryluk"
        flavorText = "Death inevitably seduces all who study it."
        imageUri = "https://cards.scryfall.io/normal/front/b/d/bd0a2fe3-45ab-4bac-aca7-a6418e28d0be.jpg?1783941987"
    }
}
