package com.wingedsheep.mtg.sets.definitions.tsp.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.madness
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Call to the Netherworld
 * {B}
 * Sorcery
 * Return target black creature card from your graveyard to your hand.
 * Madness {0} (If you discard this card, discard it into exile. When you do, cast it for its
 * madness cost or put it into your graveyard.)
 *
 * Madness {0} is a real zero cost rather than the absence of one — `madness("{0}")` parses to a
 * payable cost, so the madness trigger still offers the free cast. `CardBuilder.build()` derives
 * the printed `Keyword.MADNESS` from the keyword ability, so the bare keyword is never written
 * beside it.
 *
 * The targeted return needs no `fromZone` guard: the requirement's own `zone = GRAVEYARD` is
 * re-checked at resolution under CR 608.2b, so [Effects.ReturnToHand] is the right facade here.
 */
val CallToTheNetherworld = card("Call to the Netherworld") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "Return target black creature card from your graveyard to your hand.\n" +
        "Madness {0} (If you discard this card, discard it into exile. When you do, cast it for its madness cost or put it into your graveyard.)"

    spell {
        val t = target(
            "target",
            TargetObject(
                filter = TargetFilter(
                    baseFilter = GameObjectFilter.Creature.withColor(Color.BLACK).ownedByYou(),
                    zone = Zone.GRAVEYARD
                )
            )
        )
        effect = Effects.ReturnToHand(t)
    }

    madness("{0}")

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "97"
        artist = "Vance Kovacs"
        flavorText = "The ritual was normally performed only by horrors and pit spawn. Lesser mages had but one sanity to crack in the casting."
        imageUri = "https://cards.scryfall.io/normal/front/f/a/fa7322e2-dbea-46e1-ba29-a86a13e5d33e.jpg"
    }
}
