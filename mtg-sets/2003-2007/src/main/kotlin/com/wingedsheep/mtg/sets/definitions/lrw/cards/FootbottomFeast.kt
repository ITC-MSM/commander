package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardOrder
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.ZonePlacement
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Footbottom Feast
 * {2}{B}
 * Instant
 * Put any number of target creature cards from your graveyard on top of your library.
 * Draw a card.
 *
 * Drafna's Restoration's shape, scoped to your own graveyard: one `unlimited = true` target slot
 * ("any number of target …", so zero is a legal choice and the spell still resolves for the
 * cantrip), gathered with [CardSource.ChosenTargets] and moved to the top of your library.
 *
 * The printed text has no "in any order", but CR 401.4 gives the order to the effect's controller
 * whenever a spell puts two or more cards on top of a library without specifying one — hence
 * [CardOrder.ControllerChooses]. The draw is sequenced *after* the move, so a single creature card
 * put back on top is the card you then draw.
 */
val FootbottomFeast = card("Footbottom Feast") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "Put any number of target creature cards from your graveyard on top of your library.\n" +
        "Draw a card."

    spell {
        target(
            "any number of target creature cards from your graveyard",
            TargetObject(unlimited = true, filter = TargetFilter.CreatureInYourGraveyard)
        )
        effect = Effects.Composite(
            GatherCardsEffect(source = CardSource.ChosenTargets, storeAs = "feast_cards"),
            MoveCollectionEffect(
                from = "feast_cards",
                destination = CardDestination.ToZone(
                    Zone.LIBRARY,
                    player = Player.You,
                    placement = ZonePlacement.Top
                ),
                order = CardOrder.ControllerChooses
            ),
            Effects.DrawCards(1)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "115"
        artist = "Jim Nelson"
        flavorText = "The scent of rot and vinegar fills the marsh, inviting boggarts from every " +
            "warren to reunite and feast."
        imageUri = "https://cards.scryfall.io/normal/front/f/c/fc04f820-9505-4ccd-98e7-1bb861161af5.jpg?1783942890"
    }
}
