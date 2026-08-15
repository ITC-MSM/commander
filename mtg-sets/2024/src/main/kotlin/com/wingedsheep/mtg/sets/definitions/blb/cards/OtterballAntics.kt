package com.wingedsheep.mtg.sets.definitions.blb.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.effects.CreateTokenEffect

/**
 * Otterball Antics {1}{U}
 * Sorcery
 *
 * Create a 1/1 blue and red Otter creature token with prowess. If this spell was cast
 * from anywhere other than your hand, put a +1/+1 counter on that creature.
 * Flashback {3}{U}
 */
val OtterballAntics = card("Otterball Antics") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Sorcery"
    oracleText = "Create a 1/1 blue and red Otter creature token with prowess. If this spell was cast from anywhere other than your hand, put a +1/+1 counter on that creature.\nFlashback {3}{U} (You may cast this card from your graveyard for its flashback cost. Then exile it.)"

    spell {
        effect = ConditionalEffect(
            condition = Conditions.Not(Conditions.WasCastFromHand),
            // Not from hand: create token with +1/+1 counter
            effect = CreateTokenEffect(
                power = 1,
                toughness = 1,
                colors = setOf(Color.BLUE, Color.RED),
                creatureTypes = setOf("Otter"),
                keywords = setOf(Keyword.PROWESS),
                imageUri = "https://cards.scryfall.io/normal/front/e/6/e6b2c465-c446-4dee-9101-763105dcf813.jpg?1724438155",
                initialCounters = mapOf("+1/+1" to 1)
            ),
            // From hand: create token without counter
            elseEffect = CreateTokenEffect(
                power = 1,
                toughness = 1,
                colors = setOf(Color.BLUE, Color.RED),
                creatureTypes = setOf("Otter"),
                keywords = setOf(Keyword.PROWESS),
                imageUri = "https://cards.scryfall.io/normal/front/e/6/e6b2c465-c446-4dee-9101-763105dcf813.jpg?1724438155"
            )
        )
    }

    keywordAbility(KeywordAbility.flashback("{3}{U}"))

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "63"
        artist = "Rhonda Libbey"
        imageUri = "https://cards.scryfall.io/normal/front/3/f/3ff83ff7-e428-4ccc-8341-f223dab76bd1.jpg?1721426185"
    }
}
