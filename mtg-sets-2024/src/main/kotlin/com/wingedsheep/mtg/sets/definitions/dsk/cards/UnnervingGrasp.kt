package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Unnerving Grasp
 * {2}{U}
 * Sorcery
 * Return up to one target nonland permanent to its owner's hand. Manifest dread. (Look at the top
 * two cards of your library. Put one onto the battlefield face down as a 2/2 creature and the other
 * into your graveyard. Turn it face up any time for its mana cost if it's a creature card.)
 *
 * "Up to one target nonland permanent" → an optional [TargetPermanent] over
 * [TargetFilter.NonlandPermanent]; the bounce is skipped when no target is chosen and the manifest
 * dread still happens. The two effects are an ordered [Effects.Composite] (bounce, then the shared
 * [Patterns.Library.manifestDread] recipe), matching the printed sequence.
 */
val UnnervingGrasp = card("Unnerving Grasp") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Sorcery"
    oracleText = "Return up to one target nonland permanent to its owner's hand. Manifest dread. " +
        "(Look at the top two cards of your library. Put one onto the battlefield face down as a " +
        "2/2 creature and the other into your graveyard. Turn it face up any time for its mana " +
        "cost if it's a creature card.)"

    spell {
        val permanent = target(
            "up to one target nonland permanent",
            TargetPermanent(optional = true, filter = TargetFilter.NonlandPermanent),
        )
        effect = Effects.Composite(
            Effects.ReturnToHand(permanent),
            Patterns.Library.manifestDread(),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "80"
        artist = "Jeremy Wilson"
        imageUri = "https://cards.scryfall.io/normal/front/5/6/5602756d-76d2-4502-965f-36fc44596123.jpg?1726286150"
    }
}
