package com.wingedsheep.mtg.sets.definitions.neo.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.ninjutsu
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Nezumi Prowler — Kamigawa: Neon Dynasty #116 (canonical printing)
 * {1}{B} · Artifact Creature — Rat Ninja · 3/1
 *
 * Ninjutsu {1}{B}
 * When this creature enters, target creature you control gains deathtouch and lifelink until end
 * of turn.
 *
 * The grant may target this Rat itself: ninjutsu puts it onto the battlefield attacking, so the
 * ETB resolves with it already a legal target and a 3/1 deathtouch lifelink attacker is the usual
 * line.
 */
val NezumiProwler = card("Nezumi Prowler") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Artifact Creature — Rat Ninja"
    power = 3
    toughness = 1
    oracleText = "Ninjutsu {1}{B} ({1}{B}, Return an unblocked attacker you control to hand: Put " +
        "this card onto the battlefield from your hand tapped and attacking.)\n" +
        "When this creature enters, target creature you control gains deathtouch and lifelink " +
        "until end of turn."

    ninjutsu("{1}{B}")

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val t = target(
            "creature you control",
            TargetCreature(filter = TargetFilter.CreatureYouControl),
        )
        effect = Effects.GrantKeyword(Keyword.DEATHTOUCH, t) then
            Effects.GrantKeyword(Keyword.LIFELINK, t)
        description = "When this creature enters, target creature you control gains deathtouch " +
            "and lifelink until end of turn."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "116"
        artist = "Joseph Weston"
        imageUri = "https://cards.scryfall.io/normal/front/c/a/ca911e9a-bd04-4531-a81e-3485e9cbe89e.jpg?1783923879"
    }
}
