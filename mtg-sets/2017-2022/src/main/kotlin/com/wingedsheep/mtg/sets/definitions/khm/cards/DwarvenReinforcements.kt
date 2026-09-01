package com.wingedsheep.mtg.sets.definitions.khm.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Dwarven Reinforcements
 * {3}{R}
 * Sorcery
 * Create two 2/1 red Dwarf Berserker creature tokens.
 * Foretell {1}{R} (During your turn, you may pay {2} and exile this card from your hand face down. Cast it on a later turn for its foretell cost.)
 *
 * A two-token sorcery whose foretell cost undercuts its printed one by two generic — the standard
 * Kaldheim tempo trade that [KeywordAbility.foretell] models.
 */
val DwarvenReinforcements = card("Dwarven Reinforcements") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Create two 2/1 red Dwarf Berserker creature tokens.\n" +
        "Foretell {1}{R} (During your turn, you may pay {2} and exile this card from your hand face down. Cast it on a later turn for its foretell cost.)"

    spell {
        effect = Effects.CreateToken(
            power = 2,
            toughness = 1,
            colors = setOf(Color.RED),
            creatureTypes = setOf("Dwarf", "Berserker"),
            count = 2
        )
    }

    keywordAbility(KeywordAbility.foretell("{1}{R}"))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "134"
        artist = "Andrey Kuzinskiy"
        flavorText = "\"Orders? We don't wait for orders!\""
        imageUri = "https://cards.scryfall.io/normal/front/4/0/40bcc7cb-65dd-4bc6-8606-a162fa6c65f7.jpg"
    }
}
