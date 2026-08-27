package com.wingedsheep.mtg.sets.definitions.m10.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Honor of the Pure
 * {1}{W}
 * Enchantment
 *
 * White creatures you control get +1/+1.
 *
 * A colour lord rather than a tribal one — the same [ModifyStats]-over-a-[GroupFilter] shape as
 * Thistledown Liege, with the colour predicate doing the work a subtype usually does. No
 * `excludeSelf`: the enchantment is not a creature, so there is nothing for it to exclude, and the
 * printed line has no "other". The filter is evaluated against projected state by the layer system,
 * so a creature that *becomes* white picks the bonus up.
 */
val HonorOfThePure = card("Honor of the Pure") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment"
    oracleText = "White creatures you control get +1/+1."

    staticAbility {
        ability = ModifyStats(
            powerBonus = 1,
            toughnessBonus = 1,
            filter = GroupFilter(GameObjectFilter.Creature.withColor(Color.WHITE).youControl())
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "16"
        artist = "Greg Staples"
        flavorText = "Together the soldiers were like a golden blade, cutting down their enemies and scarring the darkness."
        imageUri = "https://cards.scryfall.io/normal/front/3/5/35a40f09-d16a-43c7-b4fd-244f45883a47.jpg?1783942401"
    }
}
