package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Rootgrapple
 * {4}{G}
 * Kindred Instant — Treefolk
 * Destroy target noncreature permanent. If you control a Treefolk, draw a card.
 */
val Rootgrapple = card("Rootgrapple") {
    manaCost = "{4}{G}"
    colorIdentity = "G"
    typeLine = "Kindred Instant — Treefolk"
    oracleText = "Destroy target noncreature permanent. If you control a Treefolk, draw a card."

    spell {
        val permanent = target(
            "target noncreature permanent",
            TargetPermanent(filter = TargetFilter.NoncreaturePermanent)
        )
        effect = Effects.Composite(
            Effects.Destroy(permanent),
            ConditionalEffect(
                condition = Conditions.ControlPermanentOfType(Subtype.TREEFOLK),
                effect = Effects.DrawCards(1)
            )
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "234"
        artist = "Alan Pollack"
        flavorText = "\"All the sylvan secrets of this world are etched between my rings. The skinfolk's metal aberrations can rot between my roots.\"\n—Colfenor, the Last Yew"
        imageUri = "https://cards.scryfall.io/normal/front/0/f/0fe6051f-6252-4ad1-90ab-d21705a708d1.jpg?1783942858"
    }
}
