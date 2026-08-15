package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Transdimensional Bovine
 * {2}{G}
 * Creature — Ox Avatar
 * 0/4
 *
 * Flying
 * {T}: Add two mana of any one color.
 */
val TransdimensionalBovine = card("Transdimensional Bovine") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Ox Avatar"
    oracleText = "Flying\n{T}: Add two mana of any one color."
    power = 0
    toughness = 4

    keywords(Keyword.FLYING)

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddAnyColorMana(2)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "134"
        artist = "Lius Lasahido"
        flavorText = "The nature of a giant, disembodied cyborg cow head that travels through time and space raises countless questions. But Cudley doesn't like talking with his mouth full."
        imageUri = "https://cards.scryfall.io/normal/front/d/e/de124563-205c-4f40-8c13-4ae203599912.jpg?1769006238"
    }
}
