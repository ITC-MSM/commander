package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.effects.CREATED_TOKENS
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect

/**
 * Retrieve the Esper
 * {3}{U}
 * Sorcery
 *
 * Create a 3/3 blue Robot Warrior artifact creature token. Then if this spell was cast from a
 * graveyard, put two +1/+1 counters on that token.
 * Flashback {5}{U}
 *
 * The token-create atomic publishes its entity id under [CREATED_TOKENS]; the graveyard-cast rider
 * is a [ConditionalEffect] gated on [Conditions.WasCastFromGraveyard] that addresses that same token
 * via [Effects.AddCountersToCollection] (the Incubate composition pattern).
 */
val RetrieveTheEsper = card("Retrieve the Esper") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Sorcery"
    oracleText = "Create a 3/3 blue Robot Warrior artifact creature token. " +
        "Then if this spell was cast from a graveyard, put two +1/+1 counters on that token.\n" +
        "Flashback {5}{U} (You may cast this card from your graveyard for its flashback cost. Then exile it.)"

    spell {
        effect = Effects.Composite(
            Effects.CreateToken(
                power = 3,
                toughness = 3,
                colors = setOf(Color.BLUE),
                creatureTypes = setOf("Robot", "Warrior"),
                artifactToken = true,
                imageUri = "https://cards.scryfall.io/normal/front/c/2/c2b4e93b-6b27-4dd3-a6dd-a75d6fab14dc.jpg?1748704066",
            ),
            ConditionalEffect(
                condition = Conditions.WasCastFromGraveyard,
                effect = Effects.AddCountersToCollection(CREATED_TOKENS, Counters.PLUS_ONE_PLUS_ONE, 2),
            ),
        )
    }

    keywordAbility(KeywordAbility.flashback("{5}{U}"))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "68"
        artist = "Jake Murray"
        imageUri = "https://cards.scryfall.io/normal/front/e/b/ebd733f0-8883-434a-b36c-ef76b091fe8e.jpg?1748706008"
    }
}
