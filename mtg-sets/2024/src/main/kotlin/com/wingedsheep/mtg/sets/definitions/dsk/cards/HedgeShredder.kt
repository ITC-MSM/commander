package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.IterationSpace
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.ZonePlacement

/**
 * Hedge Shredder
 * {2}{G}{G}
 * Artifact — Vehicle
 * 5/5
 * Whenever this Vehicle attacks, you may mill two cards.
 * Whenever one or more land cards are put into your graveyard from your library, put them onto
 * the battlefield tapped.
 * Crew 1
 *
 * The second ability is a library-to-graveyard batching trigger ("one or more ... are put into
 * your graveyard" fires once per event regardless of how many cards moved, per CR 603.2c on a
 * single trigger event): the milled land cards that caused it are captured into the resolving
 * ability's pipeline under
 * [IterationSpace.TRIGGER_CAPTURED_COLLECTION], and a [MoveCollectionEffect] moves
 * exactly those cards from the graveyard onto the battlefield tapped under the controller's
 * control. Because the trigger feeds off the same mill the first ability performs, milling lands
 * (e.g. from "may mill two cards") puts them straight into play.
 */
val HedgeShredder = card("Hedge Shredder") {
    manaCost = "{2}{G}{G}"
    colorIdentity = "G"
    typeLine = "Artifact — Vehicle"
    power = 5
    toughness = 5
    oracleText = "Whenever this Vehicle attacks, you may mill two cards.\n" +
        "Whenever one or more land cards are put into your graveyard from your library, put them " +
        "onto the battlefield tapped.\n" +
        "Crew 1 (Tap any number of creatures you control with total power 1 or more: This Vehicle " +
        "becomes an artifact creature until end of turn.)"

    triggeredAbility {
        trigger = Triggers.Attacks
        optional = true
        effect = Patterns.Library.mill(2)
    }

    triggeredAbility {
        trigger = Triggers.LandsPutIntoGraveyardFromLibrary
        effect = MoveCollectionEffect(
            from = IterationSpace.TRIGGER_CAPTURED_COLLECTION,
            destination = CardDestination.ToZone(
                zone = Zone.BATTLEFIELD,
                placement = ZonePlacement.Tapped
            )
        )
    }

    keywordAbility(KeywordAbility.Numeric(Keyword.CREW, 1))

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "183"
        artist = "Cristi Balanescu"
        imageUri = "https://cards.scryfall.io/normal/front/3/9/39e83502-2ffd-4169-94e3-116701323ed5.jpg?1726286545"
    }
}
