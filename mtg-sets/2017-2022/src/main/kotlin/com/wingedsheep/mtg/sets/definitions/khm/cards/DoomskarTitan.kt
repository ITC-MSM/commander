package com.wingedsheep.mtg.sets.definitions.khm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Doomskar Titan
 * {4}{R}{R}
 * Creature — Giant Berserker
 * 4/4
 * When this creature enters, creatures you control get +1/+0 and gain haste until end of turn.
 * Foretell {4}{R} (During your turn, you may pay {2} and exile this card from your hand face down. Cast it on a later turn for its foretell cost.)
 *
 * The pump and the haste grant are one [Patterns.Group.pumpAndGrantToAll] pass, not a composite of
 * two group walks: the printed sentence names its group once, and gathering it twice would let a
 * P/T-sensitive filter match a different set on the second pass.
 */
val DoomskarTitan = card("Doomskar Titan") {
    manaCost = "{4}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Giant Berserker"
    oracleText = "When this creature enters, creatures you control get +1/+0 and gain haste until end of turn.\n" +
        "Foretell {4}{R} (During your turn, you may pay {2} and exile this card from your hand face down. Cast it on a later turn for its foretell cost.)"
    power = 4
    toughness = 4

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Group.pumpAndGrantToAll(
            power = 1,
            toughness = 0,
            keyword = Keyword.HASTE,
            filter = GroupFilter(GameObjectFilter.Creature.youControl())
        )
    }

    keywordAbility(KeywordAbility.foretell("{4}{R}"))

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "130"
        artist = "Johann Bodin"
        imageUri = "https://cards.scryfall.io/normal/front/a/4/a4e125e4-103f-4a68-b85f-1382646da71e.jpg"
    }
}
