package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeywordToOwnSpells

/**
 * Samut, Tyrant of Naktamun — Reality Fracture #220
 * {1}{U} · Legendary Creature — Human Wizard · 2/1
 *
 * Instant and sorcery spells you control have split second. (As long as a spell with split second
 * is on the stack, players can't cast spells or activate abilities that aren't mana abilities.)
 *
 * [GrantKeywordToOwnSpells] over [GameObjectFilter.InstantOrSorcery] — "spells you control", so the
 * grant follows the spell's current controller while it is on the stack, and lasts only while
 * Samut is on the battlefield under that player's control. The engine's split-second lock
 * (CR 702.61) reads the grant live: if Samut leaves the battlefield (say, to a triggered ability),
 * the spell loses split second and players may respond again.
 */
val SamutTyrantOfNaktamun = card("Samut, Tyrant of Naktamun") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Legendary Creature — Human Wizard"
    power = 2
    toughness = 1
    oracleText = "Instant and sorcery spells you control have split second. (As long as a spell with " +
        "split second is on the stack, players can't cast spells or activate abilities that aren't " +
        "mana abilities.)"

    staticAbility {
        ability = GrantKeywordToOwnSpells(
            keyword = Keyword.SPLIT_SECOND,
            spellFilter = GameObjectFilter.InstantOrSorcery
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "220"
        artist = "Grzegorz Rutkowski"
        flavorText = "Everything in her wake stills, then stops."
        imageUri = "https://cards.scryfall.io/normal/front/6/d/6d7d8fa7-ce69-4a8c-9af0-55571393a244.jpg?1789729644"
        inBooster = false
    }
}
