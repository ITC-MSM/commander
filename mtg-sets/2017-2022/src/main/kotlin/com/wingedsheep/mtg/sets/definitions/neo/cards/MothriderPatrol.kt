package com.wingedsheep.mtg.sets.definitions.neo.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Mothrider Patrol — Kamigawa: Neon Dynasty #30 (canonical printing)
 * {W} · Creature — Fox Warrior · 1/1
 *
 * Flying
 * {3}{W}, {T}: Tap target creature.
 */
val MothriderPatrol = card("Mothrider Patrol") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Creature — Fox Warrior"
    power = 1
    toughness = 1
    oracleText = "Flying\n{3}{W}, {T}: Tap target creature."

    keywords(Keyword.FLYING)

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{3}{W}"), Costs.Tap)
        val t = target("creature to tap", TargetCreature())
        effect = Effects.Tap(t)
        description = "{3}{W}, {T}: Tap target creature."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "30"
        artist = "Ilse Gort"
        flavorText = "\"Be proud, young pup! You've earned your wings. Rise and become the " +
            "western wind.\"\n—Swift-Arm, Golden-Tail headmaster"
        imageUri = "https://cards.scryfall.io/normal/front/f/6/f657f2dc-adc8-4a66-b081-a71b3a127389.jpg?1783923915"
    }
}
