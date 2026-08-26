package com.wingedsheep.mtg.sets.definitions.neo.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.SearchDestination
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Ecologist's Terrarium — Kamigawa: Neon Dynasty #246 (canonical printing)
 * {2} · Artifact
 *
 * When this artifact enters, you may search your library for a basic land card, reveal it, put it
 * into your hand, then shuffle.
 * {2}, {T}, Sacrifice this artifact: Put a +1/+1 counter on target creature. Activate only as a
 * sorcery.
 *
 * The Pilgrim's Eye trigger (an optional [Patterns.Library.searchLibrary] to hand) bolted onto a
 * sorcery-speed sacrifice outlet, so a dead mid-game artifact still converts into a counter.
 */
val EcologistsTerrarium = card("Ecologist's Terrarium") {
    manaCost = "{2}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "When this artifact enters, you may search your library for a basic land card, " +
        "reveal it, put it into your hand, then shuffle.\n" +
        "{2}, {T}, Sacrifice this artifact: Put a +1/+1 counter on target creature. Activate " +
        "only as a sorcery."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        optional = true
        effect = Patterns.Library.searchLibrary(
            filter = GameObjectFilter.BasicLand,
            destination = SearchDestination.HAND,
            reveal = true,
        )
        description = "When this artifact enters, you may search your library for a basic land " +
            "card, reveal it, put it into your hand, then shuffle."
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}"), Costs.Tap, Costs.SacrificeSelf)
        val t = target("creature to grow", TargetCreature())
        effect = Effects.AddCounters(counterType = "+1/+1", count = 1, target = t)
        timing = TimingRule.SorcerySpeed
        description = "{2}, {T}, Sacrifice this artifact: Put a +1/+1 counter on target creature. " +
            "Activate only as a sorcery."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "246"
        artist = "Daniel Ljunggren"
        imageUri = "https://cards.scryfall.io/normal/front/1/d/1d981026-b2df-4d8d-a9b4-296b011d9925.jpg?1783923825"
    }
}
