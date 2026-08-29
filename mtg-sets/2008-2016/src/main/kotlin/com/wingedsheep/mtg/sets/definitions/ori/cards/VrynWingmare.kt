package com.wingedsheep.mtg.sets.definitions.ori.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget

/**
 * Vryn Wingmare
 * {2}{W}
 * Creature — Pegasus
 * 2/1
 *
 * Flying
 * Noncreature spells cost {1} more to cast.
 *
 * A symmetrical tax — [SpellCostTarget.AnyCaster], so it taxes its own controller too.
 */
val VrynWingmare = card("Vryn Wingmare") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Pegasus"
    oracleText = "Flying\n" +
        "Noncreature spells cost {1} more to cast."
    power = 2
    toughness = 1

    keywords(Keyword.FLYING)

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.AnyCaster(GameObjectFilter.Noncreature),
            modification = CostModification.IncreaseGeneric(1),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "40"
        artist = "Seb McKinnon"
        flavorText = "It's the favored mount of military commanders as well as anyone with a flair for the dramatic."
        imageUri = "https://cards.scryfall.io/normal/front/a/3/a34291dc-103f-493d-b217-bd1b0e946d8d.jpg"
    }
}
