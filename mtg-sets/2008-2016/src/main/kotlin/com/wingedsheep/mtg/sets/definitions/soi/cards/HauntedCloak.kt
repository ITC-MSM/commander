package com.wingedsheep.mtg.sets.definitions.soi.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword

/**
 * Haunted Cloak (Shadows over Innistrad #257)
 * {3}
 * Artifact — Equipment
 *
 * Equipped creature has vigilance, trample, and haste.
 * Equip {1} ({1}: Attach to target creature you control. Equip only as a sorcery.)
 *
 * Modeling notes:
 *  - Three separate [GrantKeyword] statics, one per printed keyword; each keeps the default
 *    `GroupFilter.attachedCreature()` scope, which is what "equipped creature has" means.
 *  - `equipAbility("{1}")` sets `equipCost` *and* builds the `ActivatedAbility.equip` lowering
 *    with `isEquipAbility = true`; its defaults already carry sorcery timing and
 *    `TargetFilter.CreatureYouControl`, so the reminder text needs no extra spelling.
 */
val HauntedCloak = card("Haunted Cloak") {
    manaCost = "{3}"
    typeLine = "Artifact — Equipment"
    oracleText = "Equipped creature has vigilance, trample, and haste.\n" +
        "Equip {1} ({1}: Attach to target creature you control. Equip only as a sorcery.)"

    staticAbility {
        ability = GrantKeyword(Keyword.VIGILANCE)
    }
    staticAbility {
        ability = GrantKeyword(Keyword.TRAMPLE)
    }
    staticAbility {
        ability = GrantKeyword(Keyword.HASTE)
    }

    equipAbility("{1}")

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "257"
        artist = "Volkan Baǵa"
        flavorText = "Bear the weight of so many spirits and you'll surrender civility and restraint to savagery and instinct."
        imageUri = "https://cards.scryfall.io/normal/front/e/f/ef142a2a-08e8-4344-8626-239690b3bb87.jpg?1783937706"
    }
}
