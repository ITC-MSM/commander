package com.wingedsheep.mtg.sets.definitions.khm.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Behold the Multiverse
 * {3}{U}
 * Instant
 * Scry 2, then draw two cards.
 * Foretell {1}{U} (During your turn, you may pay {2} and exile this card from your hand face down. Cast it on a later turn for its foretell cost.)
 *
 * Two library primitives in sequence — the scry resolves before the draw, so the cards it
 * arranges are the ones drawn. Foretell is lowered into [KeywordAbility.foretell]; the printed
 * `Keyword.FORETELL` is display-only and the card builder derives it back from the ability.
 */
val BeholdTheMultiverse = card("Behold the Multiverse") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Scry 2, then draw two cards.\n" +
        "Foretell {1}{U} (During your turn, you may pay {2} and exile this card from your hand face down. Cast it on a later turn for its foretell cost.)"

    spell {
        effect = Effects.Composite(
            Effects.Scry(2),
            Effects.DrawCards(2)
        )
    }

    keywordAbility(KeywordAbility.foretell("{1}{U}"))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "46"
        artist = "Magali Villeneuve"
        flavorText = "Countless worlds unfolded before Niko, every one in need of heroes."
        imageUri = "https://cards.scryfall.io/normal/front/2/7/27855a38-a682-4f97-ad22-ac625e86faec.jpg"
    }
}
