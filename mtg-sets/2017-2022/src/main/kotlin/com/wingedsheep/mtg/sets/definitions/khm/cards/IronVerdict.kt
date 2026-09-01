package com.wingedsheep.mtg.sets.definitions.khm.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Iron Verdict
 * {2}{W}
 * Instant
 * Iron Verdict deals 5 damage to target tapped creature.
 * Foretell {W} (During your turn, you may pay {2} and exile this card from your hand face down. Cast it on a later turn for its foretell cost.)
 *
 * A punisher for attackers: the tapped restriction lives in the target requirement, so an untapped
 * creature is not a legal target at all rather than being targeted and then spared.
 */
val IronVerdict = card("Iron Verdict") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Iron Verdict deals 5 damage to target tapped creature.\n" +
        "Foretell {W} (During your turn, you may pay {2} and exile this card from your hand face down. Cast it on a later turn for its foretell cost.)"

    spell {
        val victim = target("target tapped creature", Targets.TappedCreature)
        effect = Effects.DealDamage(5, victim)
    }

    keywordAbility(KeywordAbility.foretell("{W}"))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "17"
        artist = "Bryan Sola"
        flavorText = "\"You'll raid nothing but mist in Istfell.\""
        imageUri = "https://cards.scryfall.io/normal/front/2/f/2fb38f5b-a2c2-4b06-8c9c-9615475a43e7.jpg"
    }
}
