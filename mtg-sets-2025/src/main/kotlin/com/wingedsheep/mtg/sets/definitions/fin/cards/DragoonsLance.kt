package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.jobSelect
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.GrantSubtype
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * Dragoon's Lance
 * {1}{W}
 * Artifact — Equipment
 * Job select (When this Equipment enters, create a 1/1 colorless Hero creature token,
 *   then attach this to it.)
 * Equipped creature gets +1/+0 and is a Knight in addition to its other types.
 * During your turn, equipped creature has flying.
 * Gae Bolg — Equip {4}
 *
 * "During your turn, equipped creature has flying" is a conditional static grant gated on
 * Conditions.IsYourTurn (the Equipment's controller's turn), matching Blacksmith's Talent's
 * "during your turn" keyword grants. "Gae Bolg" is the flavor name on the equip ability; it
 * resolves as a standard equip {4}.
 */
val DragoonsLance = card("Dragoon's Lance") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Artifact — Equipment"
    oracleText = "Job select (When this Equipment enters, create a 1/1 colorless Hero creature token, then attach this to it.)\n" +
        "Equipped creature gets +1/+0 and is a Knight in addition to its other types.\n" +
        "During your turn, equipped creature has flying.\n" +
        "Gae Bolg — Equip {4} ({4}: Attach to target creature you control. Equip only as a sorcery.)"

    jobSelect()

    staticAbility {
        ability = ModifyStats(1, 0, Filters.EquippedCreature)
    }
    staticAbility {
        ability = GrantSubtype("Knight", Filters.EquippedCreature)
    }
    staticAbility {
        condition = Conditions.IsYourTurn
        ability = GrantKeyword(Keyword.FLYING, Filters.EquippedCreature)
    }

    equipAbility("{4}")

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "17"
        artist = "Josephine Chang"
        imageUri = "https://cards.scryfall.io/normal/front/9/6/96630531-8eb7-4e3e-8d63-60c562a5571b.jpg?1748705819"
    }
}
