package com.wingedsheep.mtg.sets.definitions.rna.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Footlight Fiend — Ravnica Allegiance #216
 * {B/R} · Creature — Devil · 1 / 1
 *
 * A dies trigger, so the target is chosen as the ability goes on the stack while the Fiend is
 * already in the graveyard (CR 603.6c/603.10). The damage source is the Fiend's last known
 * information, which the engine supplies from the zone-change event.
 */
val FootlightFiend = card("Footlight Fiend") {
    manaCost = "{B/R}"
    colorIdentity = "BR"
    typeLine = "Creature — Devil"
    power = 1
    toughness = 1
    oracleText = "When this creature dies, it deals 1 damage to any target."

    triggeredAbility {
        trigger = Triggers.Dies
        val victim = target("target", Targets.Any)
        effect = Effects.DealDamage(1, victim)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "216"
        artist = "Deruchenko Alexander"
        flavorText = "\"This footlight's broken. Get me a stagehand!\"\n" +
        "—Judith"
        imageUri = "https://cards.scryfall.io/normal/front/8/c/8c604697-5c81-4329-9b16-f19bd90ba08c.jpg"
    }
}
