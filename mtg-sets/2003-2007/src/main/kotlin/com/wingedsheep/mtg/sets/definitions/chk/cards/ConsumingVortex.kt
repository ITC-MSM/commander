package com.wingedsheep.mtg.sets.definitions.chk.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.splice
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Consuming Vortex
 * {1}{U}
 * Instant — Arcane
 * Return target creature to its owner's hand.
 * Splice onto Arcane {3}{U}
 *
 * The splice cost is not the mana cost: {3}{U} to graft the bounce onto another Arcane spell.
 * The declared `spell { }` effect *is* the spliced text (CR 702.47a), so `splice(...)` needs no
 * further wiring.
 */
val ConsumingVortex = card("Consuming Vortex") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Instant — Arcane"
    oracleText = "Return target creature to its owner's hand.\n" +
        "Splice onto Arcane {3}{U} (As you cast an Arcane spell, you may reveal this card from " +
        "your hand and pay its splice cost. If you do, add this card's effects to that spell.)"

    splice("{3}{U}")

    spell {
        val t = target("target", TargetCreature(filter = TargetFilter.Creature))
        effect = Effects.Move(t, Zone.HAND)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "54"
        artist = "Pete Venters"
        imageUri = "https://cards.scryfall.io/normal/front/4/9/493ee99c-74ca-4d78-ade2-c6a93b0bd4fd.jpg?1783944329"
    }
}
