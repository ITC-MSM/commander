package com.wingedsheep.mtg.sets.definitions.chk.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.splice
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Wear Away
 * {G}{G}
 * Instant — Arcane
 * Destroy target artifact or enchantment.
 * Splice onto Arcane {3}{G}
 *
 * Destruction is `Effects.Move(..., Zone.GRAVEYARD, byDestruction = true)` — the flag is what makes
 * regeneration and indestructible able to see it. The one-predicate `Or` in
 * [TargetFilter.ArtifactOrEnchantment] is the "artifact or enchantment" noun phrase.
 */
val WearAway = card("Wear Away") {
    manaCost = "{G}{G}"
    colorIdentity = "G"
    typeLine = "Instant — Arcane"
    oracleText = "Destroy target artifact or enchantment.\n" +
        "Splice onto Arcane {3}{G} (As you cast an Arcane spell, you may reveal this card from " +
        "your hand and pay its splice cost. If you do, add this card's effects to that spell.)"

    splice("{3}{G}")

    spell {
        val t = target("target", TargetPermanent(filter = TargetFilter.ArtifactOrEnchantment))
        effect = Effects.Move(t, Zone.GRAVEYARD, byDestruction = true)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "250"
        artist = "Greg Hildebrandt"
        imageUri = "https://cards.scryfall.io/normal/front/3/e/3e809799-ce47-4eaf-b2c2-2c8807182532.jpg?1783944280"
    }
}
