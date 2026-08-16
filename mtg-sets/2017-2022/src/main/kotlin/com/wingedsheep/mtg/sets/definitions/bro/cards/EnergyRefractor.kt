package com.wingedsheep.mtg.sets.definitions.bro.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Energy Refractor
 * {2}
 * Artifact
 * When this artifact enters, draw a card.
 * {2}: Add one mana of any color.
 *
 * The mana ability costs mana only — there is no tap symbol in the printed cost, so the artifact
 * can be activated repeatedly. `manaAbility = true` derives the mana-ability timing rule.
 */
val EnergyRefractor = card("Energy Refractor") {
    manaCost = "{2}"
    typeLine = "Artifact"
    oracleText = "When this artifact enters, draw a card.\n" +
        "{2}: Add one mana of any color."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.DrawCards(1)
    }

    activatedAbility {
        cost = Costs.Mana("{2}")
        effect = Effects.AddManaOfChoice()
        manaAbility = true
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "234"
        artist = "Maria Poliakova"
        flavorText = "\"Raw energy contains infinite possibility.\"\n—Urza"
        imageUri = "https://cards.scryfall.io/normal/front/c/c/cc2a81fb-5045-4b7b-8cfb-b90c4f4a1f51.jpg?1783920019"
    }
}
