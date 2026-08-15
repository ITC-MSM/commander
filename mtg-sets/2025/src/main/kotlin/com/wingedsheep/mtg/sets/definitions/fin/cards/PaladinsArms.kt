package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.jobSelect
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantSubtype
import com.wingedsheep.sdk.scripting.GrantWard
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.effects.WardCost

/**
 * Paladin's Arms
 * {2}{W}
 * Artifact — Equipment
 * Job select (When this Equipment enters, create a 1/1 colorless Hero creature token,
 *   then attach this to it.)
 * Equipped creature gets +2/+1, has ward {1}, and is a Knight in addition to its other types.
 * Lightbringer and Hero's Shield — Equip {4}
 *
 * The standard Job-select Equipment shell (token + auto-attach via [jobSelect]) plus three
 * static grants on the equipped creature: a flat stat bump ([ModifyStats]), ward {1}
 * ([GrantWard] with a mana ward cost), and the Knight type ([GrantSubtype]). "Lightbringer and
 * Hero's Shield" is flavor on the equip ability; it resolves as a standard equip {4}.
 */
val PaladinsArms = card("Paladin's Arms") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Artifact — Equipment"
    oracleText = "Job select (When this Equipment enters, create a 1/1 colorless Hero creature token, then attach this to it.)\n" +
        "Equipped creature gets +2/+1, has ward {1}, and is a Knight in addition to its other types.\n" +
        "Lightbringer and Hero's Shield — Equip {4} ({4}: Attach to target creature you control. Equip only as a sorcery.)"

    jobSelect()

    staticAbility {
        ability = ModifyStats(2, 1, Filters.EquippedCreature)
    }
    staticAbility {
        ability = GrantWard(WardCost.Mana("{1}"), Filters.EquippedCreature)
    }
    staticAbility {
        ability = GrantSubtype("Knight", Filters.EquippedCreature)
    }

    equipAbility("{4}")

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "28"
        artist = "Immanuela Crovius"
        imageUri = "https://cards.scryfall.io/normal/front/4/4/446506c5-5e1d-4b42-aef3-ea247d7881ef.jpg?1748705858"
    }
}
