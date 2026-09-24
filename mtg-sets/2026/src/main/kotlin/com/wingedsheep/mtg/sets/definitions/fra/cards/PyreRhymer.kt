package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AdditionalManaOnSourceTap
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Pyre Rhymer // Molten Tide — Reality Fracture #91
 * {1}{R}{R} · Creature — Elemental Sorcerer · 3/3 · Rare
 *
 * Prowess. Enters prepared. Prowess is `prowess()`, not `keywords(Keyword.PROWESS)`: the keyword
 * alone is display-only and carries no trigger.
 *
 * Molten Tide ({R} Instant): "Until end of turn, whenever you tap a Mountain for mana, add an
 * additional {R}."
 *
 * Molten Tide is High Tide made one-sided: the same [AdditionalManaOnSourceTap] static granted to
 * the caster until end of turn, with the filter narrowed by `youControl()`. The grant holder is the
 * caster, so "you" in the filter reads as the caster — only Mountains they control (and so only
 * Mountains they tap) produce the extra {R}. The filter is re-read on every tap, so a Mountain that
 * arrives later in the turn is covered too.
 */
val PyreRhymer = card("Pyre Rhymer") {
    manaCost = "{1}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Elemental Sorcerer"
    power = 3
    toughness = 3
    oracleText = "Prowess\nThis creature enters prepared. (While it's prepared, you may cast a copy of its spell. Doing so unprepares it.)"

    prowess()
    keywords(Keyword.PREPARED)

    prepare("Molten Tide") {
        manaCost = "{R}"
        typeLine = "Instant"
        oracleText = "Until end of turn, whenever you tap a Mountain for mana, add an additional {R}."
        spell {
            effect = Effects.GrantStaticAbility(
                ability = AdditionalManaOnSourceTap(
                    sourceFilter = GameObjectFilter.Land.withSubtype(Subtype.MOUNTAIN).youControl(),
                    color = Color.RED,
                ),
                target = EffectTarget.Controller,
                duration = Duration.EndOfTurn,
            )
        }
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "91"
        artist = "Andrea Piparo"
        imageUri = "https://cards.scryfall.io/normal/front/2/b/2b0ebea0-86de-4da4-9fe8-dacc1e75c161.jpg?1789470850"
        inBooster = false
    }
}
