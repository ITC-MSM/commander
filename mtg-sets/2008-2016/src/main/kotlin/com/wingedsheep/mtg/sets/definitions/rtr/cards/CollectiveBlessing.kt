package com.wingedsheep.mtg.sets.definitions.rtr.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Collective Blessing
 * {3}{G}{G}{W}
 * Enchantment
 *
 * Creatures you control get +3/+3.
 *
 * Canonical printing: Return to Ravnica, the card's earliest real printing.
 *
 * A layer-7c lord: one static [ModifyStats] over [GroupFilter.AllCreaturesYouControl]. The group
 * is re-read every projection, so a creature that enters later gets the bonus without a trigger.
 */
val CollectiveBlessing = card("Collective Blessing") {
    manaCost = "{3}{G}{G}{W}"
    colorIdentity = "GW"
    typeLine = "Enchantment"
    oracleText = "Creatures you control get +3/+3."

    staticAbility {
        ability = ModifyStats(3, 3, GroupFilter.AllCreaturesYouControl)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "150"
        artist = "Svetlin Velinov"
        flavorText = "Senators of Azorius often hired agents to spy on the Selesnya. They were told to record every spore and root they saw, as each could become a deadly foe."
        imageUri = "https://cards.scryfall.io/normal/front/5/3/53c84c4d-e6d6-4eac-9d14-5b6cba914c3d.jpg?1783940342"
    }
}
