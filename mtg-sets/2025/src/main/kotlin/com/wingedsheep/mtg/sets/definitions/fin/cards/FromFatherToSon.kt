package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.conditions.WasCastFromZone
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectFromCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectionMode
import com.wingedsheep.sdk.scripting.effects.ShuffleLibraryEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * From Father to Son
 * {1}{W}
 * Sorcery
 * Search your library for a Vehicle card, reveal it, and put it into your hand. If this spell
 *   was cast from a graveyard, put that card onto the battlefield instead. Then shuffle.
 * Flashback {4}{W}{W}{W}
 *
 * A Gather → Select → Move tutor whose *destination* is chosen at resolution by where the spell
 * was cast from. The only flashback-legal way to cast this from the graveyard is its own
 * flashback cost, so [WasCastFromZone] (GRAVEYARD) cleanly distinguishes the two printed
 * destinations: the found card goes to the battlefield on a graveyard cast, otherwise to hand.
 * Both branches reveal the card (the move's `revealed` flag) and the shuffle happens after,
 * matching "Then shuffle." "Search for ... a card" is a may-find tutor, so the selection is
 * [SelectionMode.ChooseUpTo] 1 (you may fail to find).
 */
val FromFatherToSon = card("From Father to Son") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Sorcery"
    oracleText = "Search your library for a Vehicle card, reveal it, and put it into your hand. " +
        "If this spell was cast from a graveyard, put that card onto the battlefield instead. Then shuffle.\n" +
        "Flashback {4}{W}{W}{W} (You may cast this card from your graveyard for its flashback cost. Then exile it.)"

    spell {
        effect = Effects.Composite(
            GatherCardsEffect(
                source = CardSource.FromZone(
                    zone = Zone.LIBRARY,
                    player = Player.You,
                    filter = GameObjectFilter.Artifact.withSubtype(Subtype.VEHICLE)
                ),
                storeAs = "searchable"
            ),
            SelectFromCollectionEffect(
                from = "searchable",
                selection = SelectionMode.ChooseUpTo(DynamicAmount.Fixed(1)),
                storeSelected = "found",
                prompt = "Search for a Vehicle card",
                selectedLabel = "Reveal it",
            ),
            ConditionalEffect(
                condition = WasCastFromZone(Zone.GRAVEYARD),
                effect = MoveCollectionEffect(
                    from = "found",
                    destination = CardDestination.ToZone(Zone.BATTLEFIELD),
                    revealed = true
                ),
                elseEffect = MoveCollectionEffect(
                    from = "found",
                    destination = CardDestination.ToZone(Zone.HAND),
                    revealed = true
                )
            ),
            ShuffleLibraryEffect()
        )
    }

    keywordAbility(KeywordAbility.flashback("{4}{W}{W}{W}"))

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "20"
        artist = "Jeremy Chong"
        imageUri = "https://cards.scryfall.io/normal/front/0/c/0c730a3b-334e-466b-bb9b-4b41fce2af6d.jpg?1748705832"
    }
}
