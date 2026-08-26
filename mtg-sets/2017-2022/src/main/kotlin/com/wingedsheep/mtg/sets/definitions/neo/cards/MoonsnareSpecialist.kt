package com.wingedsheep.mtg.sets.definitions.neo.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.ninjutsu
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Moonsnare Specialist — Kamigawa: Neon Dynasty #70 (canonical printing)
 * {3}{U} · Creature — Human Ninja · 2/2
 *
 * Ninjutsu {2}{U}
 * When this creature enters, return up to one target creature to its owner's hand.
 *
 * Ninjutsu returns *your* unblocked attacker as part of the cost; this ETB then bounces a second
 * creature, which may be a blocker or another of your own attackers.
 */
val MoonsnareSpecialist = card("Moonsnare Specialist") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Ninja"
    power = 2
    toughness = 2
    oracleText = "Ninjutsu {2}{U} ({2}{U}, Return an unblocked attacker you control to hand: Put " +
        "this card onto the battlefield from your hand tapped and attacking.)\n" +
        "When this creature enters, return up to one target creature to its owner's hand."

    ninjutsu("{2}{U}")

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val t = target("creature to bounce", TargetCreature(optional = true))
        effect = Effects.ReturnToHand(t)
        description = "When this creature enters, return up to one target creature to its owner's hand."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "70"
        artist = "Lie Setiawan"
        imageUri = "https://cards.scryfall.io/normal/front/1/e/1e8b209f-f577-45cc-9d18-03c1d67d391e.jpg?1783923897"
    }
}
