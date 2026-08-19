package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.RegenerateEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Black Poplar Shaman
 * {2}{B}
 * Creature — Treefolk Shaman
 * 1/3
 * {2}{B}: Regenerate target Treefolk.
 *
 * "Target Treefolk" is any Treefolk *permanent*, not just a creature — a Treefolk land (Murmuring
 * Bosk is a Forest, but Treefolk lands exist in the block) is a legal target.
 */
val BlackPoplarShaman = card("Black Poplar Shaman") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Treefolk Shaman"
    power = 1
    toughness = 3
    oracleText = "{2}{B}: Regenerate target Treefolk."

    activatedAbility {
        cost = Costs.Mana("{2}{B}")
        val treefolk = target(
            "target Treefolk",
            TargetPermanent(filter = TargetFilter.Permanent.withSubtype(Subtype.TREEFOLK))
        )
        effect = RegenerateEffect(treefolk)
        description = "{2}{B}: Regenerate target Treefolk."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "99"
        artist = "Mark Poole"
        flavorText = "It absorbs the pain of other treefolk, which leaves it bitter, yet addicted to the sensation of agony."
        imageUri = "https://cards.scryfall.io/normal/front/1/1/11a1fc03-5719-49ed-9d53-d8ea5693373e.jpg?1783942895"
    }
}
