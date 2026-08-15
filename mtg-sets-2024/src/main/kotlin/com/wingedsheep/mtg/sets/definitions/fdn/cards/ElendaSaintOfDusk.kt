package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.ProtectionScope

/**
 * Elenda, Saint of Dusk
 * {2}{W}{B}
 * Legendary Creature — Vampire Knight
 * 4/4
 *
 * Lifelink, hexproof from instants
 * As long as your life total is greater than your starting life total, Elenda gets +1/+1 and has
 * menace. Elenda gets an additional +5/+5 as long as your life total is at least 10 greater than
 * your starting life total.
 *
 * - "Hexproof from instants" (CR 702.11b) is [KeywordAbility.Hexproof] with a
 *   [ProtectionScope.CardType] scope, projected as the `HEXPROOF_FROM_CARDTYPE_INSTANT` keyword and
 *   enforced wherever hexproof-from-a-color is: legal-target enumeration, cast-time validation and
 *   the resolution-time fizzle check. Like all hexproof it only stops *opponents* — Elenda's
 *   controller can still target her with their own instants.
 * - Both stat clauses are separate [Conditions.LifeAboveStartingBy] statics and stack, so at
 *   starting+10 or more she is 4/4 +1/+1 +5/+5 = 10/10 with menace. Comparing against the player's
 *   real starting life total (not a hardcoded 20) keeps her honest in Commander and Two-Headed
 *   Giant.
 * - Menace rides the same condition as the first +1/+1, so it comes and goes with the life swing.
 */
val ElendaSaintOfDusk = card("Elenda, Saint of Dusk") {
    manaCost = "{2}{W}{B}"
    colorIdentity = "WB"
    typeLine = "Legendary Creature — Vampire Knight"
    power = 4
    toughness = 4
    oracleText = "Lifelink, hexproof from instants\n" +
        "As long as your life total is greater than your starting life total, Elenda gets +1/+1 " +
        "and has menace. Elenda gets an additional +5/+5 as long as your life total is at least " +
        "10 greater than your starting life total."

    keywords(Keyword.LIFELINK)
    keywordAbility(KeywordAbility.Hexproof(ProtectionScope.CardType("Instant")))

    // As long as your life total is greater than your starting life total, Elenda gets +1/+1...
    staticAbility {
        condition = Conditions.LifeAboveStartingBy(1)
        ability = ModifyStats(1, 1, Filters.Self)
    }

    // ...and has menace.
    staticAbility {
        condition = Conditions.LifeAboveStartingBy(1)
        ability = GrantKeyword(Keyword.MENACE, Filters.Self)
    }

    // Elenda gets an additional +5/+5 as long as your life total is at least 10 greater than
    // your starting life total.
    staticAbility {
        condition = Conditions.LifeAboveStartingBy(10)
        ability = ModifyStats(5, 5, Filters.Self)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "119"
        artist = "Chris Rahn"
        flavorText = "\"You have learned nothing in my absence.\""
        imageUri = "https://cards.scryfall.io/normal/front/2/4/24955f5f-093c-4d33-b0c1-911cd36032ce.jpg?1783909092"
    }
}
