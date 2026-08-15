package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.GrantKeywordToOwnSpells
import com.wingedsheep.sdk.scripting.effects.SearchDestination
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Lo and Li, Twin Tutors
 * {4}{B}
 * Legendary Creature — Human Advisor
 * 2/2
 *
 * When Lo and Li enter, search your library for a Lesson or Noble card, reveal it, put it into
 * your hand, then shuffle.
 * Noble creatures you control and Lesson spells you control have lifelink.
 *
 * The ETB tutor is [Patterns.Library.searchLibrary] with a compound subtype filter —
 * `GameObjectFilter.Any.withAnySubtype("Lesson", "Noble")` matches a card that is *either* a Lesson
 * (instant/sorcery subtype) or a Noble (creature subtype), i.e. the OR of the two subtypes.
 *
 * The lifelink clause covers two disjoint object groups:
 *  - **Noble creatures you control** — a lord-style [GrantKeyword] over your battlefield creatures
 *    with the Noble subtype (projected keyword, honored on combat/noncombat damage).
 *  - **Lesson spells you control** — [GrantKeywordToOwnSpells] projects the keyword onto Lesson
 *    spells on the stack. Lifelink only observably matters when such a spell deals damage (a burn
 *    Lesson like Ozai's Cruelty); the noncombat-damage path in `DamageUtils` consults the
 *    spell-source's granted keywords so the controller gains that much life.
 */
val LoAndLiTwinTutors = card("Lo and Li, Twin Tutors") {
    manaCost = "{4}{B}"
    colorIdentity = "B"
    typeLine = "Legendary Creature — Human Advisor"
    power = 2
    toughness = 2
    oracleText = "When Lo and Li enter, search your library for a Lesson or Noble card, reveal it, " +
        "put it into your hand, then shuffle.\n" +
        "Noble creatures you control and Lesson spells you control have lifelink."

    // When Lo and Li enter, search your library for a Lesson or Noble card, reveal it, put it into
    // your hand, then shuffle.
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Library.searchLibrary(
            filter = GameObjectFilter.Any.withAnySubtype("Lesson", "Noble"),
            count = 1,
            destination = SearchDestination.HAND,
            reveal = true,
            shuffleAfter = true,
        )
    }

    // Noble creatures you control ... have lifelink.
    staticAbility {
        ability = GrantKeyword(
            Keyword.LIFELINK,
            GroupFilter.AllCreaturesYouControl.withSubtype("Noble"),
        )
    }

    // ... and Lesson spells you control have lifelink.
    staticAbility {
        ability = GrantKeywordToOwnSpells(
            keyword = Keyword.LIFELINK,
            spellFilter = GameObjectFilter.Any.withSubtype("Lesson"),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "108"
        artist = "AKAGI"
        flavorText = "\"Almost perfect,\" Lo began. Li finished, \"One hair out of place.\""
        imageUri = "https://cards.scryfall.io/normal/front/c/b/cba9260d-ef07-4429-99f9-6004cc1fea0f.jpg?1764120747"
    }
}
