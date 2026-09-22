package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

val MarwynTheClearcutter = card("Marwyn, the Clearcutter") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Legendary Creature — Elf Warrior"
    oracleText = "{2}, {T}, Sacrifice an artifact or land: Draw a card."
    power = 2
    toughness = 1

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}"), Costs.Tap, Costs.Sacrifice(GameObjectFilter.ArtifactOrLand))
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "249"
        artist = "Quintin Gleim"
        flavorText = "\"I had to make a choice: save my people from Benalia, or save the land from my people. In the end, it was no choice at all.\""
        imageUri = "https://cards.scryfall.io/normal/front/f/9/f93da73c-ca8b-438e-8387-6109dac3fc1a.jpg?1789644549"
        inBooster = false
    }
}
