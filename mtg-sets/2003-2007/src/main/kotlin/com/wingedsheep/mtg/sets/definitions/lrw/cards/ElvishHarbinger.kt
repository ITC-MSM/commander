package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.SearchDestination

/**
 * Elvish Harbinger
 * {2}{G}
 * Creature — Elf Druid
 * 1/2
 * When this creature enters, you may search your library for an Elf card, reveal it, then shuffle
 * and put that card on top.
 * {T}: Add one mana of any color.
 */
val ElvishHarbinger = card("Elvish Harbinger") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf Druid"
    power = 1
    toughness = 2
    oracleText = "When this creature enters, you may search your library for an Elf card, reveal it, " +
        "then shuffle and put that card on top.\n{T}: Add one mana of any color."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        optional = true
        effect = Patterns.Library.searchLibrary(
            filter = GameObjectFilter.Any.withSubtype(Subtype.ELF),
            count = 1,
            destination = SearchDestination.TOP_OF_LIBRARY,
            shuffleAfter = true,
            reveal = true
        )
        description = "you may search your library for an Elf card, reveal it, then shuffle and " +
            "put that card on top."
    }

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddAnyColorMana(1)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "207"
        artist = "Larry MacDougall"
        imageUri = "https://cards.scryfall.io/normal/front/d/e/de789231-8358-4cbd-b8eb-1da4ce5b34c0.jpg?1783942866"
    }
}
