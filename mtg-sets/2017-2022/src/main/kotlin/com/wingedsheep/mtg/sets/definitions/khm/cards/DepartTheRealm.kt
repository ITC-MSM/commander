package com.wingedsheep.mtg.sets.definitions.khm.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Depart the Realm
 * {1}{U}
 * Instant
 * Return target nonland permanent to its owner's hand.
 * Foretell {U} (During your turn, you may pay {2} and exile this card from your hand face down. Cast it on a later turn for its foretell cost.)
 *
 * A one-effect bounce spell whose foretell cost is a single {U} — the cheapest way to pay for it
 * is to foretell on an earlier turn, which is exactly what [KeywordAbility.foretell] models.
 */
val DepartTheRealm = card("Depart the Realm") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Return target nonland permanent to its owner's hand.\n" +
        "Foretell {U} (During your turn, you may pay {2} and exile this card from your hand face down. Cast it on a later turn for its foretell cost.)"

    spell {
        val victim = target("target nonland permanent", Targets.NonlandPermanent)
        effect = Effects.ReturnToHand(victim)
    }

    keywordAbility(KeywordAbility.foretell("{U}"))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "53"
        artist = "Denman Rooke"
        flavorText = "\"My home calls to me, I must go.\""
        imageUri = "https://cards.scryfall.io/normal/front/2/c/2ce0403d-76ed-4eb0-abdd-74ed28f96137.jpg"
    }
}
