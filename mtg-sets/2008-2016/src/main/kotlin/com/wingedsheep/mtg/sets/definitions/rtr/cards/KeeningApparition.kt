package com.wingedsheep.mtg.sets.definitions.rtr.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Keening Apparition
 * {1}{W}
 * Creature — Spirit
 * 2/2
 *
 * Sacrifice this creature: Destroy target enchantment.
 *
 * Canonical printing: Return to Ravnica, the card's earliest real printing.
 *
 * A free sacrifice ability — [Costs.SacrificeSelf] with no mana beside it.
 */
val KeeningApparition = card("Keening Apparition") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Spirit"
    oracleText = "Sacrifice this creature: Destroy target enchantment."
    power = 2
    toughness = 2

    activatedAbility {
        cost = Costs.SacrificeSelf
        val t = target("target enchantment", Targets.Enchantment)
        effect = Effects.Destroy(t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "12"
        artist = "Terese Nielsen"
        flavorText = "Some souls are too damaged to be of use to the Orzhov."
        imageUri = "https://cards.scryfall.io/normal/front/6/5/657b242c-46cb-44d1-86fd-fb2485144a5b.jpg?1783940375"
    }
}
