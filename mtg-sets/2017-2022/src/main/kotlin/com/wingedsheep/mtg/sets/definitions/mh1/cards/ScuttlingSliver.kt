package com.wingedsheep.mtg.sets.definitions.mh1.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivatedAbility
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantActivatedAbility
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Scuttling Sliver
 * {2}{U}
 * Creature — Sliver Trilobite
 * 2/2
 * Sliver creatures you control have "{2}: Untap this creature."
 *
 * Same [GrantActivatedAbility] shape as [HollowheadSliver]. "This creature" inside the granted
 * quote is the creature that has the ability, so the effect targets [EffectTarget.Self] — the
 * grantee, not this Sliver.
 */
val ScuttlingSliver = card("Scuttling Sliver") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Sliver Trilobite"
    power = 2
    toughness = 2
    oracleText = "Sliver creatures you control have \"{2}: Untap this creature.\""

    staticAbility {
        ability = GrantActivatedAbility(
            ability = ActivatedAbility(
                cost = Costs.Mana("{2}"),
                effect = Effects.Untap(EffectTarget.Self)
            ),
            filter = GroupFilter(GameObjectFilter.Creature.withSubtype(Subtype.SLIVER).youControl())
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "68"
        artist = "Mike Bierek"
        flavorText = "A living fossil active after eons of dormancy."
        imageUri = "https://cards.scryfall.io/normal/front/e/0/e0e63efe-b5ec-426e-8860-a881c495c39e.jpg?1783933137"
    }
}
