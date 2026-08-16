package com.wingedsheep.mtg.sets.definitions.eld.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Shimmer Dragon
 * {4}{U}{U}
 * Creature — Dragon
 * 5/6
 * Flying
 * As long as you control four or more artifacts, this creature has hexproof.
 * Tap two untapped artifacts you control: Draw a card.
 *
 * The conditional hexproof is a [ConditionalStaticAbility] wrapping a source-scoped
 * [GrantKeyword] gated on [Conditions.YouControlAtLeast]`(4, Artifact)`. The draw ability's only
 * cost is [Costs.TapPermanents]`(2, Artifact)` — no mana, no tapping the Dragon itself.
 */
val ShimmerDragon = card("Shimmer Dragon") {
    manaCost = "{4}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Dragon"
    power = 5
    toughness = 6
    oracleText = "Flying\nAs long as you control four or more artifacts, this creature has hexproof. (It can't be the target of spells or abilities your opponents control.)\nTap two untapped artifacts you control: Draw a card."

    keywords(Keyword.FLYING)

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = GrantKeyword(Keyword.HEXPROOF, GroupFilter.source()),
            condition = Conditions.YouControlAtLeast(4, GameObjectFilter.Artifact),
        )
    }

    activatedAbility {
        cost = Costs.TapPermanents(2, GameObjectFilter.Artifact)
        effect = Effects.DrawCards(1)
        description = "Tap two untapped artifacts you control: Draw a card."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "317"
        artist = "Lie Setiawan"
        flavorText = "The indigo dragon gladly traded a bit of its hoard for everlasting moonlight."
        imageUri = "https://cards.scryfall.io/normal/front/5/b/5beffa4c-4188-48c1-8374-3ac3e8ea1900.jpg?1783932551"
    }
}
