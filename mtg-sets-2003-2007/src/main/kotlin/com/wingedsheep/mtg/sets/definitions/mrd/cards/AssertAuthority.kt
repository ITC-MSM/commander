package com.wingedsheep.mtg.sets.definitions.mrd.cards

import com.wingedsheep.sdk.core.CardType
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Assert Authority — Mirrodin #30
 * {5}{U}{U} · Instant
 *
 * Affinity for artifacts (This spell costs {1} less to cast for each artifact you control.)
 * Counter target spell. If that spell is countered this way, exile it instead of putting it into
 * its owner's graveyard.
 *
 * Two existing primitives, no engine work: [KeywordAbility.Affinity] over [CardType.ARTIFACT]
 * (Thoughtcast's cost reduction — generic mana only, so the floor is {U}{U}) plus
 * [Effects.CounterSpellToExile], the Dissipate counter whose `CounterDestination.Exile` sends the
 * countered spell to exile instead of its owner's graveyard.
 *
 * Affinity is a cost reduction, not an alternative cost, so mana value stays 7 in every zone no
 * matter how many artifacts paid for it.
 */
val AssertAuthority = card("Assert Authority") {
    manaCost = "{5}{U}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Affinity for artifacts (This spell costs {1} less to cast for each artifact you control.)\n" +
        "Counter target spell. If that spell is countered this way, exile it instead of putting it " +
        "into its owner's graveyard."

    keywordAbility(KeywordAbility.Affinity(CardType.ARTIFACT))

    spell {
        target = Targets.Spell
        effect = Effects.CounterSpellToExile()
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "30"
        artist = "Greg Hildebrandt"
        imageUri = "https://cards.scryfall.io/normal/front/f/c/fc339ed7-e1d4-4fe9-a4c4-b030d3e74c00.jpg?1783944557"
    }
}
