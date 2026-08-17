package com.wingedsheep.mtg.sets.definitions.snc.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Glittermonger
 * {3}{G}
 * Creature — Elf Rogue
 * 1 / 4
 * {T}: Create a Treasure token. (It's an artifact with "{T}, Sacrifice this token: Add one mana of any color.")
 *
 * A bare tap-cost activated ability over the shared [Effects.CreateTreasure] predefined token — no
 * mana, no sacrifice, so the cost is [Costs.Tap] on its own rather than a composite.
 */
val Glittermonger = card("Glittermonger") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf Rogue"
    oracleText = "{T}: Create a Treasure token. (It's an artifact with \"{T}, Sacrifice this token: Add one mana of any color.\")"
    power = 1
    toughness = 4

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.CreateTreasure(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "149"
        artist = "Evyn Fong"
        flavorText = "Street corners in the Mezzio are a tantalizing bazaar of the fantastic, the illicit, and the fake."
        imageUri = "https://cards.scryfall.io/normal/front/1/a/1a3d70d2-0604-41b3-8dd1-bfeb05832271.jpg?1783923102"
    }
}
