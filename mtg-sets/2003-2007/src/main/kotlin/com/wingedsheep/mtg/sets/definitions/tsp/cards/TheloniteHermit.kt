package com.wingedsheep.mtg.sets.definitions.tsp.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Thelonite Hermit
 * {3}{G}
 * Creature — Elf Shaman
 * 1 / 1
 * All Saprolings get +1/+1.
 * Morph {3}{G}{G} (You may cast this card face down as a 2/2 creature for {3}. Turn it face up any time for its morph cost.)
 * When this creature is turned face up, create four 1/1 green Saproling creature tokens.
 *
 * The lord is "all Saprolings", not "Saprolings you control" — the group filter carries no
 * controller predicate, and it is permanent-position rather than creature-position so a
 * non-creature Saproling would still be pumped.
 */
val TheloniteHermit = card("Thelonite Hermit") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf Shaman"
    power = 1
    toughness = 1
    oracleText = "All Saprolings get +1/+1.\n" +
        "Morph {3}{G}{G} (You may cast this card face down as a 2/2 creature for {3}. Turn it face up any time for its morph cost.)\n" +
        "When this creature is turned face up, create four 1/1 green Saproling creature tokens."

    morph = "{3}{G}{G}"

    triggeredAbility {
        trigger = Triggers.TurnedFaceUp
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.GREEN),
            creatureTypes = setOf("Saproling"),
            count = 4
        )
        description = "When this creature is turned face up, create four 1/1 green Saproling creature tokens."
    }

    staticAbility {
        ability = ModifyStats(
            powerBonus = 1,
            toughnessBonus = 1,
            filter = GroupFilter(GameObjectFilter.Permanent.withSubtype(Subtype.SAPROLING))
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "228"
        artist = "Chippy"
        imageUri = "https://cards.scryfall.io/normal/front/b/e/be95b6b0-ff20-405f-81ae-87f5cce45fb2.jpg"
    }
}
