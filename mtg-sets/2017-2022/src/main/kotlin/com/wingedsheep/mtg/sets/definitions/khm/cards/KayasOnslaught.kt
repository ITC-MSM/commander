package com.wingedsheep.mtg.sets.definitions.khm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Kaya's Onslaught
 * {2}{W}
 * Instant
 * Target creature gets +1/+1 and gains double strike until end of turn.
 * Foretell {W} (During your turn, you may pay {2} and exile this card from your hand face down. Cast it on a later turn for its foretell cost.)
 *
 * A combat trick whose real cost is {W} if it was foretold a turn earlier — double strike on an
 * unblocked attacker is the payoff [KeywordAbility.foretell] is hiding.
 */
val KayasOnslaught = card("Kaya's Onslaught") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Target creature gets +1/+1 and gains double strike until end of turn.\n" +
        "Foretell {W} (During your turn, you may pay {2} and exile this card from your hand face down. Cast it on a later turn for its foretell cost.)"

    spell {
        val recipient = target("target creature", Targets.Creature)
        effect = Effects.Composite(
            Effects.ModifyStats(1, 1, recipient),
            Effects.GrantKeyword(Keyword.DOUBLE_STRIKE, recipient)
        )
    }

    keywordAbility(KeywordAbility.foretell("{W}"))

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "18"
        artist = "Daarken"
        flavorText = "\"Your trail ends here.\""
        imageUri = "https://cards.scryfall.io/normal/front/f/2/f28e151f-b61b-486f-b7f8-7abde207c442.jpg"
    }
}
