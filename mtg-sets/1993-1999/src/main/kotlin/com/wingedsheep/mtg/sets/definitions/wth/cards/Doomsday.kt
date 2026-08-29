package com.wingedsheep.mtg.sets.definitions.wth.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CardOrder
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.EmitLibrarySearchedEventEffect
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Doomsday
 * {B}{B}{B}
 * Sorcery
 * Search your library and graveyard for five cards and exile the rest. Put the chosen cards on
 * top of your library in any order. You lose half your life, rounded up.
 *
 * One Gather → Select → Move pipeline over both zones at once
 * ([CardSource.FromMultipleZones]) — the search is a single choice across library *and* graveyard,
 * so gathering them separately would let a player pick five from each. `chooseExactlySplit` is what
 * makes the rest reachable: the non-selected cards land in the remainder slot and are exiled, which
 * is the printed "and exile the rest".
 *
 * The two rulings below fall out of the primitive rather than needing a special case.
 * "You can't choose to find fewer than five" is `ChooseExactly`, not `ChooseUpTo`; and with fewer
 * than five cards between the two zones the executor auto-selects everything eligible and leaves an
 * empty remainder, so all of them wind up back on the library and nothing is exiled.
 *
 * No shuffle: Doomsday sets the top of the library deliberately, which is the whole card. The
 * search still emits [EmitLibrarySearchedEventEffect] so "whenever a player searches their library"
 * (Aven Mindcensor, Leonin Arbiter) sees it.
 */
val Doomsday = card("Doomsday") {
    manaCost = "{B}{B}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "Search your library and graveyard for five cards and exile the rest. Put the " +
        "chosen cards on top of your library in any order. You lose half your life, rounded up."

    spell {
        effect = Effects.Composite(
            Effects.Pipeline {
                val pool = gather(
                    CardSource.FromMultipleZones(
                        zones = listOf(Zone.LIBRARY, Zone.GRAVEYARD),
                        player = Player.You
                    )
                )
                val split = chooseExactlySplit(
                    count = 5,
                    from = pool,
                    prompt = "Choose five cards to put on top of your library",
                    selectedLabel = "Put on top of library",
                    remainderLabel = "Exile"
                )
                exile(split.remainder)
                toLibraryTop(split.selected, order = CardOrder.ControllerChooses)
                run(EmitLibrarySearchedEventEffect)
            },
            Effects.LoseHalfLife(roundUp = true)
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "66"
        artist = "Adrian Smith"
        imageUri = "https://cards.scryfall.io/normal/front/5/b/5b3c6d87-9383-450b-bba5-33435b6b0d08.jpg?1783946736"

        ruling(
            "2018-03-16",
            "If your graveyard and library combined contain five or more cards, you must choose " +
                "five cards from among them. You can't choose to find fewer than that."
        )
        ruling(
            "2018-03-16",
            "If your graveyard and library combined contain fewer than five cards, all of those " +
                "cards will wind up in your library."
        )
    }
}
