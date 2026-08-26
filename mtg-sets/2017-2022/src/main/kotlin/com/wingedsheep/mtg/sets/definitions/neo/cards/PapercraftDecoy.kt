package com.wingedsheep.mtg.sets.definitions.neo.cards

import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.MayPayManaEffect

/**
 * Papercraft Decoy — Kamigawa: Neon Dynasty #253 (canonical printing)
 * {2} · Artifact Creature — Frog · 2/1
 *
 * When this creature leaves the battlefield, you may pay {2}. If you do, draw a card.
 *
 * *Leaves*, not dies: bouncing, exiling or sacrificing it all pay off, which is what makes it a
 * sacrifice-outlet body rather than a chump blocker. [MayPayManaEffect] is the "you may pay … If
 * you do" fold — the payment and the draw are one effect, so declining costs nothing.
 */
val PapercraftDecoy = card("Papercraft Decoy") {
    manaCost = "{2}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Frog"
    power = 2
    toughness = 1
    oracleText = "When this creature leaves the battlefield, you may pay {2}. If you do, draw a card."

    triggeredAbility {
        trigger = Triggers.LeavesBattlefield
        effect = MayPayManaEffect(
            cost = ManaCost.parse("{2}"),
            effect = Effects.DrawCards(1),
        )
        description = "When this creature leaves the battlefield, you may pay {2}. If you do, draw a card."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "253"
        artist = "Brian Valeza"
        flavorText = "\"And what, precisely, was so important that you left the entrance " +
            "unguarded?\"\n—Fumika, Imperial enforcer"
        imageUri = "https://cards.scryfall.io/normal/front/3/4/3453084c-42cc-4241-b244-c79e704f96c8.jpg?1783923824"
    }
}
