package com.wingedsheep.mtg.sets.definitions.som.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Bladed Pinions — Scars of Mirrodin #140
 * {2} · Artifact — Equipment
 *
 * Equipped creature has flying and first strike.
 * Equip {2}
 *
 * One [GrantKeyword] per printed keyword, each over [GroupFilter.attachedCreature] so the grant
 * lands on the creature this Equipment is attached to rather than on the Equipment itself.
 */
val BladedPinions = card("Bladed Pinions") {
    manaCost = "{2}"
    colorIdentity = ""
    typeLine = "Artifact — Equipment"
    oracleText = "Equipped creature has flying and first strike.\n" +
        "Equip {2}"

    staticAbility {
        ability = GrantKeyword(Keyword.FLYING, GroupFilter.attachedCreature())
    }

    staticAbility {
        ability = GrantKeyword(Keyword.FIRST_STRIKE, GroupFilter.attachedCreature())
    }

    equipAbility("{2}")

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "140"
        artist = "Steve Argyle"
        flavorText = "Lacking trained pterons, the Auriok had to rely on other measures to gain the upper hand in the skies."
        imageUri = "https://cards.scryfall.io/normal/front/b/f/bf479c90-c791-4152-a8e6-fd3123f698df.jpg?1783941713"
    }
}
