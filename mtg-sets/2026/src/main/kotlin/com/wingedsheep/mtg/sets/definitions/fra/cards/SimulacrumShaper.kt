package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.SearchDestination

val SimulacrumShaper = card("Simulacrum Shaper") {
    manaCost = "{1}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf Druid"
    power = 2
    toughness = 2
    oracleText = "When this creature enters, you may search your library for a basic land card, put that card onto the battlefield tapped, then shuffle.\nWhen this creature dies, draw a card."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        optional = true
        effect = Patterns.Library.searchLibrary(
            filter = GameObjectFilter.BasicLand,
            destination = SearchDestination.BATTLEFIELD,
            entersTapped = true
        )
    }
    triggeredAbility {
        trigger = Triggers.Dies
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "113"
        artist = "Alix Branwyn"
        flavorText = "It was solemn work, repurposing flesh as fertile soil."
        imageUri = "https://cards.scryfall.io/normal/front/7/c/7c725702-8696-4e5a-8318-62f5e2616d52.jpg?1789470857"
        inBooster = false
    }
}
