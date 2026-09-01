package com.wingedsheep.mtg.sets.definitions.chk.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.splice
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Kodama's Might
 * {G}
 * Instant — Arcane
 * Target creature gets +2/+2 until end of turn.
 * Splice onto Arcane {G}
 *
 * A one-mana pump whose splice cost is also {G}: the cheapest graft in the cluster, and the reason
 * a splice deck can stack several combat tricks onto one Arcane spell.
 */
val KodamasMight = card("Kodama's Might") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Instant — Arcane"
    oracleText = "Target creature gets +2/+2 until end of turn.\n" +
        "Splice onto Arcane {G} (As you cast an Arcane spell, you may reveal this card from your " +
        "hand and pay its splice cost. If you do, add this card's effects to that spell.)"

    splice("{G}")

    spell {
        val t = target("target", TargetCreature(filter = TargetFilter.Creature))
        effect = Effects.ModifyStats(2, 2, t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "224"
        artist = "Terese Nielsen"
        imageUri = "https://cards.scryfall.io/normal/front/d/0/d0ef91bb-ec25-463f-9fc9-f0f9c0652cf5.jpg?1783944286"
    }
}
