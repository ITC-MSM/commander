package com.wingedsheep.mtg.sets.definitions.som.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Perilous Myr
 * {2}
 * Artifact Creature — Phyrexian Myr
 * 1/1
 *
 * When this creature dies, it deals 2 damage to any target.
 */
val PerilousMyr = card("Perilous Myr") {
    manaCost = "{2}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Phyrexian Myr"
    oracleText = "When this creature dies, it deals 2 damage to any target."
    power = 1
    toughness = 1

    triggeredAbility {
        trigger = Triggers.Dies
        val victim = target("any target", Targets.Any)
        effect = Effects.DealDamage(2, victim)
        description = "When this creature dies, it deals 2 damage to any target."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "192"
        artist = "Jason Felix"
        flavorText = "To Mirrodin, an explosive. To Phyrexia, a missionary."
        imageUri = "https://cards.scryfall.io/normal/front/4/b/4b942605-eb4a-452d-9b07-a4f912f96958.jpg?1783941699"
    }
}
