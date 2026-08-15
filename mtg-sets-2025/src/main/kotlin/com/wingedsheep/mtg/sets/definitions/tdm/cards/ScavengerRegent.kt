package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Scavenger Regent // Exude Toxin
 * {3}{B} // {X}{B}{B}
 * Creature — Dragon // Sorcery — Omen
 * 4/4
 *
 * Scavenger Regent:
 *   Flying
 *   Ward—Discard a card.
 *
 * Exude Toxin — {X}{B}{B}, Sorcery — Omen:
 *   Each non-Dragon creature gets -X/-X until end of turn.
 *   (Then shuffle this card into its owner's library.)
 *
 * Ward—Discard a card is modeled with [KeywordAbility.wardDiscard]. Exude Toxin is an Omen face
 * (declared via the `omen { }` DSL, so on resolution the card shuffles into its owner's library
 * per the Omen reminder text). The board-wide -X/-X is
 * [Patterns.Group.modifyStatsForAll] over every creature without the Dragon subtype, with the
 * amount read from the X paid for the spell ([DynamicAmount.XValue] negated).
 */
val ScavengerRegent = card("Scavenger Regent") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Dragon"
    power = 4
    toughness = 4
    oracleText = "Flying\nWard—Discard a card."

    keywords(Keyword.FLYING)
    keywordAbility(KeywordAbility.wardDiscard())

    // Exude Toxin — Omen. Each non-Dragon creature gets -X/-X until end of turn.
    omen("Exude Toxin") {
        manaCost = "{X}{B}{B}"
        typeLine = "Sorcery — Omen"
        oracleText = "Each non-Dragon creature gets -X/-X until end of turn. " +
            "(Then shuffle this card into its owner's library.)"
        spell {
            effect = Patterns.Group.modifyStatsForAll(
                power = DynamicAmount.Multiply(DynamicAmount.XValue, -1),
                toughness = DynamicAmount.Multiply(DynamicAmount.XValue, -1),
                filter = GroupFilter(GameObjectFilter.Creature.notSubtype(Subtype.DRAGON))
            )
        }
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "90"
        artist = "John Tedrick"
        imageUri = "https://cards.scryfall.io/normal/front/0/d/0d4b46a3-847a-44a7-9f68-2cb4657cad61.jpg?1752538974"
    }
}
