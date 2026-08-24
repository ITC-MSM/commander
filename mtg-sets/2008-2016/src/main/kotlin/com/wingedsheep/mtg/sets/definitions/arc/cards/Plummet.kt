package com.wingedsheep.mtg.sets.definitions.arc.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Plummet
 * {1}{G}
 * Instant
 * Destroy target creature with flying.
 *
 * Canonical printing: Archenemy, the card's earliest real printing. Reprinted a dozen times over —
 * M12 through M21, ORI, BFZ, RIX, IKO, THB, MID, AFR and M15 — each as a `Printing` row.
 */
val Plummet = card("Plummet") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Destroy target creature with flying."

    spell {
        val t = target(
            "target creature with flying",
            TargetCreature(filter = TargetFilter(GameObjectFilter.Creature.withKeyword(Keyword.FLYING)))
        )
        effect = Effects.Destroy(t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "65"
        artist = "Pete Venters"
        flavorText = "\"You are the grandest of all,\" said the archdruid to the trees. They became so proud of bark and branch that they suffered no creature to fly overhead or perch upon a bough."
        imageUri = "https://cards.scryfall.io/normal/front/a/6/a67bb585-cc4f-4cbc-9a5a-d31df98c07ae.jpg?1783941902"
    }
}
