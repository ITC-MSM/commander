package com.wingedsheep.mtg.sets.definitions.khm.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Demon Bolt
 * {2}{R}
 * Instant
 * Demon Bolt deals 4 damage to target creature or planeswalker.
 * Foretell {R} (During your turn, you may pay {2} and exile this card from your hand face down. Cast it on a later turn for its foretell cost.)
 *
 * A one-line burn spell: [Effects.DealDamage] with a fixed 4 aimed at a single
 * [Targets.CreatureOrPlaneswalker] binding. Foretell is the only structural part — the bare
 * `Keyword.FORETELL` is display-only, so it is lowered into [KeywordAbility.foretell], which the
 * engine's ForetellEnumerator reads to offer the pay-{2}-and-exile special action and the later
 * {R} cast from exile. The card builder derives the printed FORETELL keyword from that ability.
 */
val DemonBolt = card("Demon Bolt") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Demon Bolt deals 4 damage to target creature or planeswalker.\n" +
        "Foretell {R} (During your turn, you may pay {2} and exile this card from your hand " +
        "face down. Cast it on a later turn for its foretell cost.)"

    spell {
        val t = target("target", Targets.CreatureOrPlaneswalker)
        effect = Effects.DealDamage(4, t)
    }

    keywordAbility(KeywordAbility.foretell("{R}"))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "129"
        artist = "Campbell White"
        flavorText = "\"Burn them from the sky.\"\n—Varragoth"
        imageUri = "https://cards.scryfall.io/normal/front/5/f/5f856b0e-b413-49b0-9aa7-d935ad40ae53.jpg?1783928232"
    }
}
