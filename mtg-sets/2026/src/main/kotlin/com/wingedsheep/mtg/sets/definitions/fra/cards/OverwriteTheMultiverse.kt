package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * "Exiled this way" is literal: X counts what [MoveCollectionEffect] actually moved (its
 * `storeMovedAs`), not what was gathered, so a creature that a replacement effect keeps out of
 * exile doesn't add loyalty.
 */
val OverwriteTheMultiverse = card("Overwrite the Multiverse") {
    manaCost = "{4}{B}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "Exile all creatures. Empower Jace X, where X is the number of creatures exiled this way. (Put that many loyalty counters on a Jace token you control. If you don't control one, first create a blue Jace planeswalker token with \"[−1]: Surveil 1\" and \"[−3]: Draw a card.\")"

    spell {
        effect = Effects.Composite(
            GatherCardsEffect(
                source = CardSource.BattlefieldMatching(GameObjectFilter.Creature),
                storeAs = "creatures",
            ),
            MoveCollectionEffect(
                from = "creatures",
                destination = CardDestination.ToZone(Zone.EXILE),
                storeMovedAs = "exiledCreatures",
            ),
            Patterns.Mechanic.empowerJace(DynamicAmount.DistinctEntitiesInCollections(listOf("exiledCreatures"))),
        )
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "59"
        artist = "Ryan Pancoast"
        flavorText = "\"Suffering ends at my will.\""
        imageUri = "https://cards.scryfall.io/normal/front/c/4/c4554f5b-791b-48f6-bf54-ad28699e1beb.jpg?1788952080"
        inBooster = false
    }
}
