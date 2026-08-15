package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * Quick-Draw Katana
 * {2}
 * Artifact — Equipment
 * During your turn, equipped creature gets +2/+0 and has first strike.
 * Equip {2}
 *
 * Both the +2/+0 and first strike are gated on "during your turn"
 * (Conditions.IsYourTurn), unlike Jousting Lance whose +2/+0 is unconditional.
 */
val QuickDrawKatana = card("Quick-Draw Katana") {
    manaCost = "{2}"
    colorIdentity = ""
    typeLine = "Artifact — Equipment"
    oracleText = "During your turn, equipped creature gets +2/+0 and has first strike. " +
        "(It deals combat damage before creatures without first strike.)\nEquip {2}"

    staticAbility {
        condition = Conditions.IsYourTurn
        ability = ModifyStats(+2, +0, Filters.EquippedCreature)
    }

    staticAbility {
        condition = Conditions.IsYourTurn
        ability = GrantKeyword(Keyword.FIRST_STRIKE, Filters.EquippedCreature)
    }

    equipAbility("{2}")

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "130"
        artist = "Paolo Parente"
        imageUri = "https://cards.scryfall.io/normal/front/6/9/69beec98-c89c-4673-953c-8b3ef3d81560.jpg?1782689155"
    }
}
