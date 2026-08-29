package com.wingedsheep.mtg.sets.definitions.thb.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Grim Physician
 * {B}
 * Creature — Zombie
 * 1/1
 *
 * When this creature dies, target creature an opponent controls gets -1/-1 until end of turn.
 *
 * A plain [Triggers.Dies] — no `triggerZone`, which would replace the default `{BATTLEFIELD}` and
 * leave the trigger unindexed. [Effects.ModifyStats] already ends at end of turn, so the printed
 * duration needs no argument.
 */
val GrimPhysician = card("Grim Physician") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Creature — Zombie"
    power = 1
    toughness = 1
    oracleText = "When this creature dies, target creature an opponent controls gets -1/-1 until end of turn."

    triggeredAbility {
        trigger = Triggers.Dies
        val victim = target("target", Targets.CreatureOpponentControls)
        effect = Effects.ModifyStats(-1, -1, victim)
        description = "When this creature dies, target creature an opponent controls gets -1/-1 " +
            "until end of turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "100"
        artist = "Mark Zug"
        flavorText = "The Returned retain no memory of their identities, but sometimes they mindlessly attempt familiar tasks."
        imageUri = "https://cards.scryfall.io/normal/front/b/7/b7e92c82-840f-4c75-b617-7b58a07be5b4.jpg"
    }
}
