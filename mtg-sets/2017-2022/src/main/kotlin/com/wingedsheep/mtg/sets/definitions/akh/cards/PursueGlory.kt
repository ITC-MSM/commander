package com.wingedsheep.mtg.sets.definitions.akh.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Pursue Glory
 * {3}{R}
 * Instant
 * Attacking creatures get +2/+0 until end of turn.
 * Cycling {2} ({2}, Discard this card: Draw a card.)
 *
 * "Attacking creatures" is every attacker on the battlefield, not just yours, so the [GroupFilter]
 * carries no controller predicate and the body points at [EffectTarget.Self] — the iterated
 * creature, not the spell's source.
 */
val PursueGlory = card("Pursue Glory") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Attacking creatures get +2/+0 until end of turn.\n" +
            "Cycling {2} ({2}, Discard this card: Draw a card.)"

    spell {
        effect = Effects.ForEachInGroup(
            GroupFilter(GameObjectFilter.Creature.attacking()),
            Effects.ModifyStats(2, 0, EffectTarget.Self)
        )
    }

    keywordAbility(KeywordAbility.cycling("{2}"))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "147"
        artist = "Johann Bodin"
        flavorText = "\"Combat is a form of worship, the clash of steel a solemn prayer.\"\n—Pytamun, initiate of Nef crop"
        imageUri = "https://cards.scryfall.io/normal/front/f/5/f5668ae2-fa74-47c9-877e-5326cac67659.jpg?1783936483"
    }
}
