package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget

/**
 * Archmage of Runes
 * {3}{U}{U}
 * Creature — Giant Wizard
 * 3/6
 *
 * Instant and sorcery spells you cast cost {1} less to cast.
 * Whenever you cast an instant or sorcery spell, draw a card.
 *
 * The cost reduction is a [ModifySpellCost] static (`YouCast` filtered to instants and
 * sorceries, `ReduceGeneric(1)`). The card-draw payoff reuses the shared
 * [Triggers.YouCastInstantOrSorcery] spell-cast trigger.
 */
val ArchmageOfRunes = card("Archmage of Runes") {
    manaCost = "{3}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Giant Wizard"
    power = 3
    toughness = 6
    oracleText = "Instant and sorcery spells you cast cost {1} less to cast.\n" +
        "Whenever you cast an instant or sorcery spell, draw a card."

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.YouCast(GameObjectFilter.InstantOrSorcery),
            modification = CostModification.ReduceGeneric(1),
        )
    }

    triggeredAbility {
        trigger = Triggers.YouCastInstantOrSorcery
        effect = Effects.DrawCards(1)
        description = "Whenever you cast an instant or sorcery spell, draw a card."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "30"
        artist = "Kai Carpenter"
        flavorText = "He's so intent on his study that others think him frozen in place."
        imageUri = "https://cards.scryfall.io/normal/front/3/3/334b5018-2da9-49f1-9d09-83d312ecfb02.jpg?1782689239"
    }
}
