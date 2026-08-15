package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.SearchDestination
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Cosmium Confluence
 * {4}{G} — Sorcery (Rare) — The Lost Caverns of Ixalan #181
 * Artist: Kasia 'Kafis' Zielińska
 *
 * Choose three. You may choose the same mode more than once.
 * • Search your library for a Cave card, put it onto the battlefield tapped, then shuffle.
 * • Put three +1/+1 counters on a Cave you control. It becomes a 0/0 Elemental creature
 *   with haste. It's still a land.
 * • Destroy target enchantment.
 *
 * Modeled via [modal] with chooseCount = 3 and allowRepeat = true.
 * Mode 0 — library tutoring via the Gather → Select → Move pipeline.
 * Mode 1 — counter addition (AddCountersEffect) followed by permanent Cave animation
 *   (BecomeCreatureEffect, Duration.Permanent). No "until end of turn" appears on the card;
 *   the Cave stays a creature unless it leaves the battlefield. The +1/+1 counters keep the
 *   effectively 0/0 body alive (0+3 = 3/3 while counters remain).
 * Mode 2 — targeted enchantment destruction.
 */
val CosmiumConfluence = card("Cosmium Confluence") {
    manaCost = "{4}{G}"
    colorIdentity = "G"
    typeLine = "Sorcery"
    oracleText = "Choose three. You may choose the same mode more than once.\n" +
        "• Search your library for a Cave card, put it onto the battlefield tapped, then shuffle.\n" +
        "• Put three +1/+1 counters on a Cave you control. It becomes a 0/0 Elemental creature with haste. It's still a land.\n" +
        "• Destroy target enchantment."

    spell {
        modal(chooseCount = 3, allowRepeat = true) {
            mode("Search your library for a Cave card, put it onto the battlefield tapped, then shuffle") {
                effect = Patterns.Library.searchLibrary(
                    filter = GameObjectFilter.Land.withSubtype("Cave"),
                    destination = SearchDestination.BATTLEFIELD,
                    entersTapped = true,
                    shuffleAfter = true
                )
            }
            mode("Put three +1/+1 counters on a Cave you control. It becomes a 0/0 Elemental creature with haste. It's still a land") {
                val cave = target(
                    "a Cave you control",
                    TargetPermanent(filter = TargetFilter.Land.withSubtype("Cave").youControl())
                )
                effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 3, cave) then
                    Effects.BecomeCreature(
                        target = cave,
                        power = 0,
                        toughness = 0,
                        keywords = setOf(Keyword.HASTE),
                        creatureTypes = setOf("Elemental"),
                        duration = Duration.Permanent
                    )
            }
            mode("Destroy target enchantment") {
                val enchantment = target("target enchantment", Targets.Enchantment)
                effect = Effects.Destroy(enchantment)
            }
        }
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "181"
        artist = "Kasia 'Kafis' Zielińska"
        imageUri = "https://cards.scryfall.io/normal/front/4/9/490a5054-0607-4e4a-a0a9-0e9eea7adb00.jpg?1782694465"
    }
}
