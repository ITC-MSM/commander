package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersAsCopy
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Echoing Deeps
 * Land — Cave
 * You may have this land enter tapped as a copy of any land card in a graveyard, except it's a Cave
 * in addition to its other types.
 * {T}: Add {C}.
 *
 * The copy is an as-enters replacement (CR 707.2): a graveyard-sourced [EntersAsCopy] with
 * `tappedIfCopied` (the "enter tapped as a copy" rider) and `additionalSubtypes = ["Cave"]` for the
 * "it's a Cave in addition to its other types" clause. If the copy is declined (or no land card is
 * in any graveyard) the land enters untapped as its printed self — a Cave that taps for {C}.
 */
val EchoingDeeps = card("Echoing Deeps") {
    manaCost = ""
    colorIdentity = ""
    typeLine = "Land — Cave"
    oracleText = "You may have this land enter tapped as a copy of any land card in a graveyard, " +
        "except it's a Cave in addition to its other types.\n{T}: Add {C}."

    replacementEffect(
        EntersAsCopy(
            optional = true,
            copyFilter = GameObjectFilter.Land,
            copyFromZone = Zone.GRAVEYARD,
            additionalSubtypes = listOf("Cave"),
            tappedIfCopied = true,
        )
    )

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddColorlessMana(1)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "271"
        artist = "Mauricio Calle"
        flavorText = "The still, silent waters reflect what was, not what is."
        imageUri = "https://cards.scryfall.io/normal/front/2/4/244c06b3-532d-426e-8bee-ee9461d092a6.jpg?1782694396"
    }
}
