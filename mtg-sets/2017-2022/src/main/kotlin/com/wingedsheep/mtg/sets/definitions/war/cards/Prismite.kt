package com.wingedsheep.mtg.sets.definitions.war.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Prismite — War of the Spark #242
 * {2}
 * Artifact Creature — Golem
 * 2 / 1
 * {2}: Add one mana of any color.
 *
 * The Signpost Scarecrow shape: [Effects.AddAnyColorMana] is "add one mana of any color" at its
 * defaults (all five colours, one mana). `manaAbility = true` is the load-bearing flag — CR 605.1a
 * makes this a mana ability, the engine gates on `ActivatedAbility.isManaAbility`, and the builder
 * derives `timing = ManaAbility` from it, so the timing is never written by hand.
 */
val Prismite = card("Prismite") {
    manaCost = "{2}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Golem"
    oracleText = "{2}: Add one mana of any color."
    power = 2
    toughness = 1

    activatedAbility {
        cost = Costs.Mana("{2}")
        effect = Effects.AddAnyColorMana()
        manaAbility = true
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "242"
        artist = "Alayna Danner"
        flavorText = "It didn't budge when the portal opened, nor when the ancient gods emerged. It came to life only when all ten guilds came together to face a common threat."
        imageUri = "https://cards.scryfall.io/normal/front/4/0/40475e96-0283-445f-97fb-1da008707399.jpg"
    }
}
