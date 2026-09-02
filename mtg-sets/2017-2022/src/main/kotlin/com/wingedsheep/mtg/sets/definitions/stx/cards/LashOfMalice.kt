package com.wingedsheep.mtg.sets.definitions.stx.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Lash of Malice — Strixhaven: School of Mages #74 (canonical printing)
 * {B} · Instant
 *
 * Target creature gets +2/-2 until end of turn.
 *
 * The Flowstone Infusion shape: a single [Effects.ModifyStats] of +2/-2 on the targeted creature
 * with the default until-end-of-turn duration. Nothing restricts the target's controller, so the
 * bare [Targets.Creature] requirement is correct — it kills an X/2 as readily as it pumps.
 */
val LashOfMalice = card("Lash of Malice") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText =
        "Target creature gets +2/-2 until end of turn."

    spell {
        val creature = target("target", Targets.Creature)
        effect = Effects.ModifyStats(2, -2, creature)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "74"
        artist = "Slawomir Maniak"
        flavorText = "\"The strongest shadow spells come from disdain for someone else, not frustration with yourself.\"\n—Embrose, Silverquill dean"
        imageUri = "https://cards.scryfall.io/normal/front/a/f/af3da2c6-29ed-4563-8bae-d1cc05df8897.jpg?1783927367"
    }
}
