package com.wingedsheep.mtg.sets.definitions.khm.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Ravenous Lindwurm
 * {4}{G}{G}
 * Creature — Wurm
 * 6/6
 * When this creature enters, you gain 4 life.
 *
 * A six-mana 6/6 with a four-point lifegain rider — the green common that stabilises a limited board.
 */
val RavenousLindwurm = card("Ravenous Lindwurm") {
    manaCost = "{4}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Wurm"
    oracleText = "When this creature enters, you gain 4 life."
    power = 6
    toughness = 6

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.GainLife(4)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "187"
        artist = "Filip Burburan"
        flavorText = "\"I'm no coward, but I'd sooner tangle with a rampaging Torga troll than a hungry lindwurm!\"\n" +
            "—Yegar, Beskir warrior"
        imageUri = "https://cards.scryfall.io/normal/front/9/9/9961e3fc-167e-4043-8510-cc5cf08d473e.jpg"
    }
}
