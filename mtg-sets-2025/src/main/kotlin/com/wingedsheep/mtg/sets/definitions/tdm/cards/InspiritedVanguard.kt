package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Inspirited Vanguard — Tarkir: Dragonstorm #146
 * {4}{G} · Creature — Human Soldier · 3/2
 *
 * Whenever this creature enters or attacks, it endures 2. (Put two +1/+1
 * counters on it or create a 2/2 white Spirit creature token.)
 *
 * Modeled as the standard "enters or attacks" pair of triggers (see
 * Sentinel of the Nameless City), each running the same Endure 2 effect.
 */
val InspiritedVanguard = card("Inspirited Vanguard") {
    manaCost = "{4}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Human Soldier"
    power = 3
    toughness = 2
    oracleText = "Whenever this creature enters or attacks, it endures 2. " +
        "(Put two +1/+1 counters on it or create a 2/2 white Spirit creature token.)"

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.Endure(2)
        description = "Whenever this creature enters, it endures 2."
    }

    triggeredAbility {
        trigger = Triggers.Attacks
        effect = Effects.Endure(2)
        description = "Whenever this creature attacks, it endures 2."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "146"
        artist = "Carlos Palma Cruchaga"
        imageUri = "https://cards.scryfall.io/normal/front/c/6/c642d6ac-f0f0-4b4c-a7ee-50631531f6d1.jpg?1743204549"
    }
}
