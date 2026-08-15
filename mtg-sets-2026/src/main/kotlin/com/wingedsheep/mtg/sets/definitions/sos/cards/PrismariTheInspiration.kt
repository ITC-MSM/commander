package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeywordToOwnSpells
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Prismari, the Inspiration
 * {5}{U}{R}
 * Legendary Creature — Elder Dragon
 * 7/7
 *
 * Flying
 * Ward—Pay 5 life.
 * Instant and sorcery spells you cast have storm.
 *
 * The storm grant is a continuous static ability ([GrantKeywordToOwnSpells]) scoped to the
 * controller — while Prismari is on the battlefield, instant and sorcery spells its controller
 * casts effectively have storm (CR 702.40). The cast handler reads battlefield
 * `GrantKeywordToOwnSpells` grants alongside printed storm when building each spell's storm
 * trigger, so removing Prismari before the spell is cast revokes the grant. Like the printed
 * keyword, the granted storm copies zero times when no other spells have been cast this turn.
 */
val PrismariTheInspiration = card("Prismari, the Inspiration") {
    manaCost = "{5}{U}{R}"
    colorIdentity = "UR"
    typeLine = "Legendary Creature — Elder Dragon"
    power = 7
    toughness = 7
    oracleText = "Flying\n" +
        "Ward—Pay 5 life.\n" +
        "Instant and sorcery spells you cast have storm. (Whenever you cast an instant or sorcery " +
        "spell, copy it for each spell cast before it this turn. You may choose new targets for " +
        "the copies.)"

    keywords(Keyword.FLYING)
    keywordAbility(KeywordAbility.wardLife(5))

    staticAbility {
        ability = GrantKeywordToOwnSpells(
            keyword = Keyword.STORM,
            spellFilter = GameObjectFilter.InstantOrSorcery,
        )
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "212"
        artist = "Justin Gerard"
        imageUri = "https://cards.scryfall.io/normal/front/7/6/767ff9fa-4e7f-421a-b911-45186b520ae1.jpg?1775938472"
    }
}
