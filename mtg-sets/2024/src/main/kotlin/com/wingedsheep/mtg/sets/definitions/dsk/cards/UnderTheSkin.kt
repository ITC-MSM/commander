package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectFromCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectionMode
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Under the Skin
 * {2}{G}
 * Sorcery
 * Manifest dread. (Look at the top two cards of your library. Put one onto the battlefield face
 * down as a 2/2 creature and the other into your graveyard. Turn it face up any time for its mana
 * cost if it's a creature card.)
 * You may return a permanent card from your graveyard to your hand.
 *
 * Resolves in order: first the shared [Patterns.Library.manifestDread] recipe (CR 701.62 — note the
 * card milled by manifest dread is itself an eligible return target if it's a permanent card), then
 * an optional return of a permanent card from your graveyard to your hand. The "you may return" is a
 * resolution-time selection (not a target): Gather every permanent card in your graveyard →
 * [SelectionMode.ChooseUpTo] 1 (optional) → move it to hand. Same Gather/Select/Move shape as
 * Overlord of the Balemurk's optional graveyard return.
 */
val UnderTheSkin = card("Under the Skin") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Sorcery"
    oracleText = "Manifest dread. (Look at the top two cards of your library. Put one onto the " +
        "battlefield face down as a 2/2 creature and the other into your graveyard. Turn it face " +
        "up any time for its mana cost if it's a creature card.)\n" +
        "You may return a permanent card from your graveyard to your hand."

    spell {
        effect = Effects.Composite(
            listOf(
                Patterns.Library.manifestDread(),
                GatherCardsEffect(
                    source = CardSource.FromZone(Zone.GRAVEYARD, Player.You, GameObjectFilter.Permanent),
                    storeAs = "underTheSkinReturnable"
                ),
                SelectFromCollectionEffect(
                    from = "underTheSkinReturnable",
                    selection = SelectionMode.ChooseUpTo(DynamicAmount.Fixed(1)),
                    storeSelected = "underTheSkinToReturn",
                    showAllCards = true,
                    prompt = "You may return a permanent card from your graveyard to your hand",
                    selectedLabel = "Return to hand",
                    remainderLabel = "Leave in graveyard"
                ),
                MoveCollectionEffect(
                    from = "underTheSkinToReturn",
                    destination = CardDestination.ToZone(Zone.HAND)
                )
            )
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "203"
        artist = "Fernando Falcone"
        imageUri = "https://cards.scryfall.io/normal/front/0/a/0aaf1ad3-00ad-48ec-a71f-812649d55e14.jpg?1726286622"
    }
}
