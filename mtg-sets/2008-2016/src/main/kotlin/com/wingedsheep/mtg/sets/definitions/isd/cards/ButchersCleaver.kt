package com.wingedsheep.mtg.sets.definitions.isd.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Butcher's Cleaver
 * {3}
 * Artifact — Equipment
 * Equipped creature gets +3/+0.
 * As long as equipped creature is a Human, it has lifelink.
 * Equip {3}
 *
 * The conditional lifelink is a [GrantKeyword] static gated by an [Conditions.EntityMatches] on
 * the equipped creature ([EffectTarget.EquippedCreature]) being a Human — evaluated in both
 * resolution and projection, so the grant tracks the equipped creature's type continuously.
 */
val ButchersCleaver = card("Butcher's Cleaver") {
    manaCost = "{3}"
    colorIdentity = ""
    typeLine = "Artifact — Equipment"
    oracleText = "Equipped creature gets +3/+0.\n" +
        "As long as equipped creature is a Human, it has lifelink.\n" +
        "Equip {3}"

    staticAbility {
        ability = ModifyStats(3, 0, Filters.EquippedCreature)
    }
    staticAbility {
        condition = Conditions.EntityMatches(
            EffectTarget.EquippedCreature,
            GameObjectFilter.Creature.withSubtype(Subtype.HUMAN)
        )
        ability = GrantKeyword(Keyword.LIFELINK)
    }
    equipAbility("{3}")

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "217"
        artist = "Jason Felix"
        flavorText = "Outside the safety of Thraben, there is little distinction between tool and weapon."
        imageUri = "https://cards.scryfall.io/normal/front/e/1/e141fe62-515e-4fe4-b032-81f169ec58d6.jpg?1782714694"
    }
}
