package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect

/**
 * "Wasn't cast from your hand" covers every other way the spell reaches resolution — flashback
 * from the graveyard and uncast copies alike — so the two-card branch is the negation of
 * [Conditions.WasCastFromHand] rather than a graveyard check.
 */
val TwinnedVision = card("Twinned Vision") {
    manaCost = "{1}{U/R}"
    colorIdentity = "UR"
    typeLine = "Instant"
    oracleText = "Draw a card. If this spell wasn't cast from your hand, draw two cards instead.\n" +
        "Flashback—{1}{U/R}{U/R}, Discard a card. (You may cast this card from your graveyard for its " +
        "flashback cost. Then exile it.)"

    spell {
        effect = ConditionalEffect(
            condition = Conditions.WasCastFromHand,
            effect = Effects.DrawCards(1),
            elseEffect = Effects.DrawCards(2)
        )
    }

    keywordAbility(KeywordAbility.flashback("{1}{U/R}{U/R}", Costs.additional.DiscardCards(1)))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "157"
        artist = "Andreas Zafiratos"
        flavorText = "\"She will rage and burn until her fuel is expended. It's almost sad to see me like this.\""
        imageUri = "https://cards.scryfall.io/normal/front/5/5/55f85984-0137-4899-8993-bbc8c4794d33.jpg?1789556940"
        inBooster = false
    }
}
