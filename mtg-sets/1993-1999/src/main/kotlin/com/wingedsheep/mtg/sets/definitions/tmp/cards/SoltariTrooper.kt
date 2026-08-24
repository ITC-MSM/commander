package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Soltari Trooper
 * {1}{W}
 * Creature — Soltari Soldier
 * 1/1
 * Shadow (This creature can block or be blocked by only creatures with shadow.)
 * Whenever this creature attacks, it gets +1/+1 until end of turn.
 */
val SoltariTrooper = card("Soltari Trooper") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Soltari Soldier"
    power = 1
    toughness = 1
    oracleText = "Shadow (This creature can block or be blocked by only creatures with shadow.)\n" +
        "Whenever this creature attacks, it gets +1/+1 until end of turn."

    keywords(Keyword.SHADOW)

    triggeredAbility {
        trigger = Triggers.Attacks
        effect = Effects.ModifyStats(1, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "47"
        artist = "Kev Walker"
        flavorText = "\"Dauthi blood is Soltari wine.\"\n" +
            "—Soltari *Tales of Life*"
        imageUri = "https://cards.scryfall.io/normal/front/3/2/32f74aa3-4003-4f53-b774-22b111935391.jpg"
    }
}
