package com.wingedsheep.mtg.sets.definitions.atq.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Golgothian Sylex
 * {4}
 * Artifact
 * {1}, {T}: Each nontoken permanent with a name originally printed in the Antiquities expansion
 * is sacrificed by its controller.
 *
 * `SacrificeAll` over the set-membership filter: every nontoken permanent whose card was
 * *originally printed* in ATQ (`originallyPrintedInSet("ATQ")`, canonical set code — reprints
 * still match), including Golgothian Sylex itself (its name was originally printed in ATQ and
 * the oracle text has no self-exclusion, so it sacrifices itself). Each matching permanent is
 * sacrificed by its own controller (CR 701.17), routed to its owner's graveyard; tokens never
 * match the filter.
 */
val GolgothianSylex = card("Golgothian Sylex") {
    manaCost = "{4}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "{1}, {T}: Each nontoken permanent with a name originally printed in the Antiquities expansion is sacrificed by its controller."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}"), Costs.Tap)
        effect = Effects.SacrificeAll(
            filter = GameObjectFilter.Permanent.nontoken().originallyPrintedInSet("ATQ")
        )
        description = "{1}, {T}: Each nontoken permanent with a name originally printed in the Antiquities expansion is sacrificed by its controller."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "51"
        artist = "Kerstin Kaman"
        flavorText = "From their earliest educations, the brothers had known that no human contrivance could stand against the true masters of Dominia."
        imageUri = "https://cards.scryfall.io/normal/front/8/5/856be1dd-a20b-49c2-be9d-7db76c7efd8b.jpg?1666943891"
    }
}
