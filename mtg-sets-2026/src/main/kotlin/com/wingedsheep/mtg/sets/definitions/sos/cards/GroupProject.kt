package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Group Project
 * {1}{W}
 * Sorcery
 *
 * Create a 2/2 red and white Spirit creature token.
 * Flashback—Tap three untapped creatures you control. (You may cast this card from your
 * graveyard for its flashback cost. Then exile it.)
 *
 * The flashback cost has no mana component ({0}) and a non-mana additional cost: tap three
 * untapped creatures you control.
 */
val GroupProject = card("Group Project") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Sorcery"
    oracleText = "Create a 2/2 red and white Spirit creature token.\n" +
        "Flashback—Tap three untapped creatures you control. (You may cast this card from your " +
        "graveyard for its flashback cost. Then exile it.)"

    spell {
        effect = Effects.CreateToken(
            power = 2,
            toughness = 2,
            colors = setOf(Color.RED, Color.WHITE),
            creatureTypes = setOf("Spirit"),
            imageUri = "https://cards.scryfall.io/normal/front/8/7/877f7ddb-ed70-41a0-b845-d9bf8ac65f9b.jpg?1775828448"
        )
    }

    keywordAbility(
        KeywordAbility.flashback(
            "{0}",
            Costs.additional.TapPermanents(count = 3, filter = Filters.Creature)
        )
    )

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "17"
        artist = "Justine Cruz"
        flavorText = "\"Never be afraid to ask for help. Success is much sweeter when it's shared.\"\n—Quintorius Kand"
        imageUri = "https://cards.scryfall.io/normal/front/e/8/e8abc1eb-6225-4b18-8502-b5324b818aed.jpg?1775937026"
    }
}
