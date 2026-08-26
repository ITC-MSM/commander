package com.wingedsheep.mtg.sets.definitions.neo.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Network Terminal — Kamigawa: Neon Dynasty #251 (canonical printing)
 * {3} · Artifact
 *
 * {T}: Add one mana of any color.
 * {1}, {T}, Tap another untapped artifact you control: Draw a card, then discard a card.
 *
 * The loot ability's third cost is [Costs.TapAnotherPermanent] narrowed to artifacts — it excludes
 * the Terminal itself, which the ability has already tapped as its own {T} cost, so the two tap
 * costs never contend for the same permanent.
 */
val NetworkTerminal = card("Network Terminal") {
    manaCost = "{3}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "{T}: Add one mana of any color.\n" +
        "{1}, {T}, Tap another untapped artifact you control: Draw a card, then discard a card."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddManaOfChoice()
        manaAbility = true
        timing = TimingRule.ManaAbility
        description = "{T}: Add one mana of any color."
    }

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{1}"),
            Costs.Tap,
            Costs.TapAnotherPermanent(GameObjectFilter.Artifact),
        )
        effect = Patterns.Hand.loot()
        description = "{1}, {T}, Tap another untapped artifact you control: Draw a card, then discard a card."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "251"
        artist = "Andreas Zafiratos"
        flavorText = "Public access terminals around Towashi provide residents with up-to-date " +
            "information on everything from traffic delays to hostile kami sightings."
        imageUri = "https://cards.scryfall.io/normal/front/d/6/d61e596f-97ef-4eb3-af42-ddfe50d07667.jpg?1783923824"
    }
}
