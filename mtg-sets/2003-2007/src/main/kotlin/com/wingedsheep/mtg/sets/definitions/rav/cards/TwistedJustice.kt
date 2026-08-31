package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Twisted Justice — Ravnica: City of Guilds #237 (canonical printing, only printing)
 * {4}{U}{B} · Sorcery
 *
 * Target player sacrifices a creature of their choice. You draw cards equal to that creature's
 * power.
 *
 * An edict with a payoff riding on the victim, so the whole card is the seam between the two steps:
 * by the time the draw counts power, the creature is already in the graveyard. Both halves are
 * existing primitives and the seam is already load-bearing elsewhere (Kylox, Visionary Inventor):
 *
 * - `Effects.Sacrifice(Creature, 1, <target player>)` is the Diabolic Edict half — the *player*
 *   chooses which creature, which is what "of their choice" means (the sacrifice is not a target).
 * - The sacrifice captures a last-known-information snapshot per sacrificed permanent (CR 608.2h)
 *   and `CompositeEffect` merges it into the resolving context, so
 *   `DynamicAmounts.totalPowerSacrificedThisWay()` reads the creature's power *as it last existed
 *   on the battlefield* — the only reading of "that creature's power" that can be correct here.
 *
 * A target player with no creatures sacrifices nothing and the amount is 0, so you draw nothing;
 * the spell still resolves. Negative power likewise draws nothing (the draw floors at 0).
 */
val TwistedJustice = card("Twisted Justice") {
    manaCost = "{4}{U}{B}"
    colorIdentity = "UB"
    typeLine = "Sorcery"
    oracleText = "Target player sacrifices a creature of their choice. You draw cards equal to " +
        "that creature's power."

    spell {
        val player = target("target player", Targets.Player)
        effect = Effects.Composite(
            Effects.Sacrifice(filter = GameObjectFilter.Creature, count = 1, target = player),
            Effects.DrawCards(DynamicAmounts.totalPowerSacrificedThisWay()),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "237"
        artist = "Ralph Horsley"
        flavorText = "In Otiev's mind, he ruled in favor of the accused. But in his courtroom he was only a spectator, watching his hand deliver the sign of death."
        imageUri = "https://cards.scryfall.io/normal/front/d/8/d8efa02d-c301-47e1-8cdf-26ff9e97a243.jpg?1783943608"
    }
}
