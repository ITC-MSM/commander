package com.wingedsheep.mtg.sets.definitions.roe.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Leaf Arrow
 * {G}
 * Instant
 *
 * Leaf Arrow deals 3 damage to target creature with flying.
 *
 * Modeling notes:
 *  - "with flying" is a restriction on *which creature can be chosen*, not a condition checked on
 *    resolution, so it lives on the target requirement: Assay compiles one `TargetObject` carrying
 *    both `IsCreature` and `HasKeyword FLYING`, which is exactly
 *    [Targets.CreatureWithKeyword]`(Keyword.FLYING)`. Written as a condition instead, the spell
 *    could be aimed at a ground creature and simply do nothing — the wrong behaviour.
 *  - Target legality is re-checked on resolution (CR 608.2b) off projected state, so a creature
 *    that loses flying in response makes the spell fizzle. That falls out of putting the keyword
 *    in the filter.
 */
val LeafArrow = card("Leaf Arrow") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Leaf Arrow deals 3 damage to target creature with flying."

    spell {
        val flier = target("target creature with flying", Targets.CreatureWithKeyword(Keyword.FLYING))
        effect = Effects.DealDamage(3, flier)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "194"
        artist = "Eric Deschamps"
        flavorText = "\"Those who think the trees shall remain bystanders throughout this conflict shall be sorely mistaken.\"\n—Sutina, Speaker of the Tajuru"
        imageUri = "https://cards.scryfall.io/normal/front/2/d/2d531ba4-df99-41e4-9dd3-a27a420ad63c.jpg?1783941962"
    }
}
