package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.jobSelect
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.GrantSubtype
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * Samurai's Katana
 * {2}{R}
 * Artifact — Equipment
 * Job select (When this Equipment enters, create a 1/1 colorless Hero creature token,
 *   then attach this to it.)
 * Equipped creature gets +2/+2, has trample and haste, and is a Samurai in addition to
 *   its other types.
 * Murasame — Equip {5}
 */
val SamuraisKatana = card("Samurai's Katana") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Artifact — Equipment"
    oracleText = "Job select (When this Equipment enters, create a 1/1 colorless Hero creature token, then attach this to it.)\n" +
        "Equipped creature gets +2/+2, has trample and haste, and is a Samurai in addition to its other types.\n" +
        "Murasame — Equip {5} ({5}: Attach to target creature you control. Equip only as a sorcery.)"

    jobSelect()

    staticAbility {
        ability = ModifyStats(2, 2, Filters.EquippedCreature)
    }
    staticAbility {
        ability = GrantKeyword(Keyword.TRAMPLE, Filters.EquippedCreature)
    }
    staticAbility {
        ability = GrantKeyword(Keyword.HASTE, Filters.EquippedCreature)
    }
    staticAbility {
        ability = GrantSubtype("Samurai", Filters.EquippedCreature)
    }

    equipAbility("{5}")

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "154"
        artist = "Smirtouille"
        imageUri = "https://cards.scryfall.io/normal/front/1/1/11f1d378-c78c-402a-ac46-2d32598c23e7.jpg?1748706334"
    }
}
