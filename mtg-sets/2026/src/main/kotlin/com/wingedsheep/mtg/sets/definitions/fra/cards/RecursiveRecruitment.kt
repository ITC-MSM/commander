package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.effects.CREATED_TOKENS
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.effects.CreateTokenEffect
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * The graveyard is counted as the spell resolves; a flashed-back copy is on the stack by then, so
 * it never counts itself. "For every three cards" rounds down.
 */
val RecursiveRecruitment = card("Recursive Recruitment") {
    manaCost = "{2}{U}{B}"
    colorIdentity = "UB"
    typeLine = "Sorcery"
    oracleText = "Create two 2/2 colorless Wizard Soldier creature tokens named Cadet. If this spell was " +
        "cast from a graveyard, put a +1/+1 counter on each of them for every three cards in your graveyard.\n" +
        "Flashback {6}{U}{B} (You may cast this card from your graveyard for its flashback cost. Then exile it.)"

    spell {
        effect = Effects.Composite(
            CreateTokenEffect(
                count = 2,
                power = 2,
                toughness = 2,
                colors = emptySet(),
                creatureTypes = setOf("Wizard", "Soldier"),
                name = "Cadet",
                imageUri = "https://cards.scryfall.io/normal/front/8/f/8f4534d8-2783-484f-8ebf-a47b1cc4c6df.jpg?1789734318",
            ),
            ConditionalEffect(
                condition = Conditions.WasCastFromGraveyard,
                effect = Effects.AddCountersToCollection(
                    CREATED_TOKENS,
                    Counters.PLUS_ONE_PLUS_ONE,
                    DynamicAmount.Divide(
                        DynamicAmounts.cardsInYourGraveyard(),
                        DynamicAmount.Fixed(3),
                        roundUp = false
                    )
                )
            )
        )
    }

    keywordAbility(KeywordAbility.flashback("{6}{U}{B}"))

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "147"
        artist = "Dominik Mayer"
        imageUri = "https://cards.scryfall.io/normal/front/6/8/68fddb6a-86d4-4ebb-907d-fdcaadebc4b3.jpg?1789556898"
        inBooster = false
    }
}
