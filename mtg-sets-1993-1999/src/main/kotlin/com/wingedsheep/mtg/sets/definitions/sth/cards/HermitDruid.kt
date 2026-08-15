package com.wingedsheep.mtg.sets.definitions.sth.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardOrder

/**
 * Hermit Druid
 * {1}{G}
 * Creature — Human Druid
 * 1/1
 *
 * {G}, {T}: Reveal cards from the top of your library until you reveal a basic land card. Put
 * that card into your hand and all other cards revealed this way into your graveyard.
 */
val HermitDruid = card("Hermit Druid") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Human Druid"
    power = 1
    toughness = 1
    oracleText = "{G}, {T}: Reveal cards from the top of your library until you reveal a basic " +
        "land card. Put that card into your hand and all other cards revealed this way into your " +
        "graveyard."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{G}"), Costs.Tap)
        effect = Patterns.Library.revealUntilMatchToHand(
            filter = GameObjectFilter.BasicLand,
            restDestination = CardDestination.ToZone(Zone.GRAVEYARD),
            restOrder = CardOrder.Preserve
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "108"
        artist = "Heather Hudson"
        flavorText = "Seeking the company of plants ensures that your wits will go to seed."
        imageUri = "https://cards.scryfall.io/normal/front/3/e/3efc0622-ac2c-4722-ba05-961cc98c5940.jpg?1782720833"
    }
}
