package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

val RewriteRegrets = card("Rewrite Regrets") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "Return target creature or planeswalker card with mana value 6 or less from your graveyard to the battlefield.\n" +
        "Empower Jace 2. (Put two loyalty counters on a Jace token you control. If you don't control one, first create a blue Jace planeswalker token with \"[−1]: Surveil 1\" and \"[−3]: Draw a card.\")"

    spell {
        val card = target(
            "target creature or planeswalker card with mana value 6 or less from your graveyard",
            TargetObject(
                filter = TargetFilter(
                    GameObjectFilter.CreatureOrPlaneswalker.ownedByYou().manaValueAtMost(6),
                    zone = Zone.GRAVEYARD,
                )
            ),
        )
        effect = Effects.Move(card, Zone.BATTLEFIELD, fromZone = Zone.GRAVEYARD)
            .then(Patterns.Mechanic.empowerJace(2))
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "62"
        artist = "Matt Stewart"
        imageUri = "https://cards.scryfall.io/normal/front/4/5/453cfde7-c460-4b55-9472-b714e16f24bb.jpg?1788952087"
        inBooster = false
    }
}
