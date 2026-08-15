package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Bespoke Bō
 * {2}{U}
 * Artifact — Equipment
 *
 * When this Equipment enters, return up to one other target nonland
 * permanent to its owner's hand.
 * Equipped creature gets +2/+1 and has vigilance.
 * Equip {3}
 */
val BespokeBo = card("Bespoke Bō") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Artifact — Equipment"
    oracleText = "When this Equipment enters, return up to one other target nonland permanent to its owner's hand.\nEquipped creature gets +2/+1 and has vigilance.\nEquip {3} ({3}: Attach to target creature you control. Equip only as a sorcery.)"

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val permanent = target(
            "other nonland permanent",
            TargetPermanent(optional = true, filter = TargetFilter.NonlandPermanent.other())
        )
        effect = Effects.ReturnToHand(permanent)
    }

    staticAbility {
        ability = ModifyStats(+2, +1, Filters.EquippedCreature)
    }

    staticAbility {
        ability = GrantKeyword(Keyword.VIGILANCE, Filters.EquippedCreature)
    }

    equipAbility("{3}")

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "31"
        artist = "Nathaniel Himawan"
        imageUri = "https://cards.scryfall.io/normal/front/6/c/6c517308-831b-4e41-a82c-02ddf6383a0c.jpg?1771586788"
    }
}
