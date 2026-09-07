package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.transmute
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Dimir Machinations
 * {2}{B}
 * Sorcery
 * Look at the top three cards of target player's library. Exile any number of those cards, then
 * put the rest back in any order.
 * Transmute {1}{B}{B}
 *
 * The Cruel Fate shape with an unbounded split: gather the top three of the *target's* library,
 * `chooseAnyNumberSplit` so zero through three may be exiled, exile the chosen ones, and put the
 * remainder back on top of that same library. "In any order" is the default
 * `CardOrder.ControllerChooses` on `toLibraryTop` — the spell's controller orders them, not the
 * library's owner, and the ordering happens even when only one card is left (a no-op prompt the
 * engine skips).
 */
val DimirMachinations = card("Dimir Machinations") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "Look at the top three cards of target player's library. Exile any number of those cards, then put the rest back in any order.\n" +
        "Transmute {1}{B}{B} ({1}{B}{B}, Discard this card: Search your library for a card with the same mana value as this card, reveal it, put it into your hand, then shuffle. Transmute only as a sorcery.)"

    spell {
        target("target player", Targets.Player)
        effect = Effects.Pipeline {
            val looked = gather(
                CardSource.TopOfLibrary(DynamicAmount.Fixed(3), Player.TargetPlayer)
            )
            val (exiled, rest) = chooseAnyNumberSplit(
                from = looked,
                prompt = "Exile any number of those cards",
                selectedLabel = "Exile",
                remainderLabel = "Put back on top",
            )
            exile(exiled)
            toLibraryTop(rest, Player.TargetPlayer)
        }
    }

    transmute("{1}{B}{B}")

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "84"
        artist = "Greg Staples"
        imageUri = "https://cards.scryfall.io/normal/front/1/4/14bfd72a-78c1-4167-89bf-ea1fccccd5b1.jpg?1783943671"
    }
}
