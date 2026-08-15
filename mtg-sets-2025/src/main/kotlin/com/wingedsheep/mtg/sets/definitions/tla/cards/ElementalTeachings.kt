package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.Chooser
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.RevealCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectFromCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectionMode
import com.wingedsheep.sdk.scripting.effects.SelectionRestriction
import com.wingedsheep.sdk.scripting.effects.ShuffleLibraryEffect
import com.wingedsheep.sdk.scripting.effects.ZonePlacement
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Elemental Teachings — {4}{G} Instant — Lesson (Avatar: The Last Airbender, rare).
 *
 * "Search your library for up to four land cards with different names and reveal them.
 *  An opponent chooses two of those cards. Put the chosen cards into your graveyard and
 *  the rest onto the battlefield tapped, then shuffle."
 *
 * Atomic pipeline (Tempt-with-Discovery shape with an opponent split):
 *  1. Gather library lands → controller `ChooseUpTo(4)` with `OnePerCardName` (different
 *     names) → "found". "Up to four" means the controller may find fewer (or none).
 *  2. Reveal "found".
 *  3. An opponent `ChooseExactly(2)` over "found" → "toGraveyard" / remainder "toBattlefield".
 *     The executor clamps the count to the collection size, so if fewer than two cards were
 *     found the opponent chooses that many (CR 700.x), and the rest still hit the battlefield.
 *  4. Move "toGraveyard" → graveyard, "toBattlefield" → battlefield tapped, then shuffle.
 */
val ElementalTeachings = card("Elemental Teachings") {
    manaCost = "{4}{G}"
    colorIdentity = "G"
    typeLine = "Instant — Lesson"
    oracleText = "Search your library for up to four land cards with different names and reveal them. " +
        "An opponent chooses two of those cards. Put the chosen cards into your graveyard and the rest " +
        "onto the battlefield tapped, then shuffle."

    spell {
        effect = Effects.Composite(
            listOf(
                GatherCardsEffect(
                    source = CardSource.FromZone(Zone.LIBRARY, Player.You, GameObjectFilter.Land),
                    storeAs = "searchable"
                ),
                SelectFromCollectionEffect(
                    from = "searchable",
                    selection = SelectionMode.ChooseUpTo(DynamicAmount.Fixed(4)),
                    storeSelected = "found",
                    restrictions = listOf(SelectionRestriction.OnePerCardName),
                    prompt = "Search for up to four land cards with different names"
                ),
                RevealCollectionEffect(from = "found"),
                SelectFromCollectionEffect(
                    from = "found",
                    selection = SelectionMode.ChooseExactly(DynamicAmount.Fixed(2)),
                    chooser = Chooser.Opponent,
                    storeSelected = "toGraveyard",
                    storeRemainder = "toBattlefield",
                    selectedLabel = "Graveyard",
                    remainderLabel = "Battlefield",
                    prompt = "Choose two of the revealed cards. Those go into the controller's graveyard; the rest enter the battlefield tapped.",
                    alwaysPrompt = true
                ),
                MoveCollectionEffect(
                    from = "toGraveyard",
                    destination = CardDestination.ToZone(Zone.GRAVEYARD)
                ),
                MoveCollectionEffect(
                    from = "toBattlefield",
                    destination = CardDestination.ToZone(Zone.BATTLEFIELD, placement = ZonePlacement.Tapped)
                ),
                ShuffleLibraryEffect()
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "178"
        artist = "Yoshioka"
        flavorText = "\"Understanding others, the other elements, and the other nations will help you become whole.\"\n—Iroh"
        imageUri = "https://cards.scryfall.io/normal/front/c/a/cac8ac35-6860-4839-ab85-93d409206c08.jpg?1764121209"
    }
}
