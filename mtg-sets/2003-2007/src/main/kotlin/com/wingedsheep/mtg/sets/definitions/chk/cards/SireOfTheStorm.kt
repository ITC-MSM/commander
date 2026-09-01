package com.wingedsheep.mtg.sets.definitions.chk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Sire of the Storm
 * {4}{U}{U}
 * Creature — Spirit
 * 3/3
 * Flying
 * Whenever you cast a Spirit or Arcane spell, you may draw a card.
 *
 * The blue half of the CHK "Whenever you cast a Spirit or Arcane spell" cycle: the shared
 * [Triggers.youCastSpell] over a homogeneous OR of the two subtype filters, binding `ANY`.
 *
 * The printed "you may" is the builder's `optional = true`, which lowers to a `Gate.MayDecide`
 * around the draw. The "may" matters here rather than being flavour: the trigger goes on the
 * stack even with an empty library, and declining is what keeps a Sire from decking its own
 * controller.
 */
val SireOfTheStorm = card("Sire of the Storm") {
    manaCost = "{4}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Spirit"
    oracleText = "Flying\n" +
        "Whenever you cast a Spirit or Arcane spell, you may draw a card."
    power = 3
    toughness = 3

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.youCastSpell(
            spellFilter = GameObjectFilter.Any.withAnySubtype("Spirit", "Arcane")
        )
        effect = Effects.DrawCards(1)
        optional = true
        description = "Whenever you cast a Spirit or Arcane spell, you may draw a card."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "85"
        artist = "Arnie Swekel"
        flavorText = "This storm blows gales through the dreams of men."
        imageUri = "https://cards.scryfall.io/normal/front/2/e/2e76d003-2d15-43d7-9cbb-e00564d0cabf.jpg?1783944322"
    }
}
