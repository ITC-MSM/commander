package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.AddColorlessManaEffect
import com.wingedsheep.sdk.scripting.effects.AddManaOfChoiceEffect
import com.wingedsheep.sdk.scripting.effects.ManaRestriction
import com.wingedsheep.sdk.scripting.values.ManaColorSet

/**
 * Maelstrom of the Spirit Dragon — Tarkir: Dragonstorm #260
 * Land · Rare
 *
 * {T}: Add {C}.
 * {T}: Add one mana of any color. Spend this mana only to cast a Dragon spell or an Omen spell.
 * {4}, {T}, Sacrifice this land: Search your library for a Dragon card, reveal it, put it into
 *   your hand, then shuffle.
 *
 * Two mana abilities and one non-mana sacrifice tutor. The any-color ability tags its mana with
 * [ManaRestriction.SubtypeSpellsOnly] for the subtypes "Dragon" and "Omen" — the new multi-subtype
 * spend restriction (a spell qualifies if its type line carries either subtype; the Omen face of a
 * modal DFC has the "Omen" subtype on the stack). The tutor is the atomic
 * [Patterns.Library.searchLibrary] pipeline (find a Dragon card, reveal it, to hand, then shuffle).
 */
val MaelstromOfTheSpiritDragon = card("Maelstrom of the Spirit Dragon") {
    typeLine = "Land"
    oracleText = "{T}: Add {C}.\n" +
        "{T}: Add one mana of any color. Spend this mana only to cast a Dragon spell or an Omen spell.\n" +
        "{4}, {T}, Sacrifice this land: Search your library for a Dragon card, reveal it, put it into " +
        "your hand, then shuffle."

    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddColorlessManaEffect(1)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddManaOfChoiceEffect(
            colorSet = ManaColorSet.AnyColor,
            restriction = ManaRestriction.SubtypeSpellsOnly(setOf("Dragon", "Omen"))
        )
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = AbilityCost.Composite(
            listOf(
                Costs.Mana(ManaCost.parse("{4}")),
                AbilityCost.Tap,
                AbilityCost.SacrificeSelf
            )
        )
        effect = Patterns.Library.searchLibrary(
            filter = GameObjectFilter.Any.withSubtype(Subtype.DRAGON),
            count = 1,
            reveal = true,
            shuffleAfter = true
        )
        description = "Search your library for a Dragon card, reveal it, put it into your hand, then shuffle."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "260"
        artist = "Carlos Palma Cruchaga"
        imageUri = "https://cards.scryfall.io/normal/front/c/4/c4e90bfb-d9a5-48a9-9ff9-b0f50a813eee.jpg?1743205026"
    }
}
