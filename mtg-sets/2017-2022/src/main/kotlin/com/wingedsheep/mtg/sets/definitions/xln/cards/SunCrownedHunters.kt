package com.wingedsheep.mtg.sets.definitions.xln.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Sun-Crowned Hunters
 * {4}{R}{R}
 * Creature — Dinosaur
 * 5/4
 *
 * Enrage — Whenever this creature is dealt damage, it deals 3 damage to target opponent or
 * planeswalker.
 */
val SunCrownedHunters = card("Sun-Crowned Hunters") {
    manaCost = "{4}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Dinosaur"
    oracleText = "Enrage — Whenever this creature is dealt damage, it deals 3 damage to target " +
        "opponent or planeswalker."
    power = 5
    toughness = 4

    triggeredAbility {
        trigger = Triggers.TakesDamage
        val victim = target("target", Targets.OpponentOrPlaneswalker)
        effect = Effects.DealDamage(3, victim)
        description = "Enrage — Whenever this creature is dealt damage, it deals 3 damage to " +
            "target opponent or planeswalker."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "164"
        artist = "Aaron Miller"
        flavorText = "One alone is dangerous, and they are never alone."
        imageUri = "https://cards.scryfall.io/normal/front/3/c/3cab793e-0e17-4940-9cab-a30d62df5c20.jpg"
    }
}
