package com.wingedsheep.mtg.sets.definitions.mrd.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.GatherUntilMatchEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.RevealCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectFromCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectionMode
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Spoils of the Vault — Mirrodin #78 (canonical printing)
 * {B} · Instant
 *
 * Choose a card name. Reveal cards from the top of your library until you reveal a card with that
 * name, then put that card into your hand. Exile all other cards revealed this way, and you lose 1
 * life for each of the exiled cards.
 *
 * The Desperate Research pieces with the fixed seven-card window swapped for a walk: naming stores
 * the choice in `chosenValues`, and [GameObjectFilter.namedFromVariable] reads it back both as the
 * stopper for [GatherUntilMatchEffect] and as the partition for the pile it stopped on.
 *
 * Unlike Desperate Research this one names *any* card — the basic land names are legal choices, and
 * the 2018-12-07 ruling is explicit that the name need not be in the library or even the deck. That
 * case needs no branch: the walk runs the library out, the partition selects nothing, and every card
 * revealed lands in the exile pile whose size is the life payment. Naming a card that *is* on top
 * costs zero life the same way, because the matching card never enters that pile.
 */
val SpoilsOfTheVault = card("Spoils of the Vault") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "Choose a card name. Reveal cards from the top of your library until you reveal a card " +
        "with that name, then put that card into your hand. Exile all other cards revealed this way, " +
        "and you lose 1 life for each of the exiled cards."

    spell {
        val named = GameObjectFilter.Any.namedFromVariable("chosenName")

        effect = Effects.Composite(
            listOf(
                // 1. Choose a card name — any name, basic lands included.
                Effects.ChooseCardName(
                    storeAs = "chosenName",
                    prompt = "Choose a card name"
                ),
                // 2. Walk the top of the library until that name shows up (or it runs out).
                GatherUntilMatchEffect(
                    player = Player.You,
                    filter = named,
                    storeMatch = "match",
                    storeRevealed = "revealed"
                ),
                RevealCollectionEffect(from = "revealed"),
                // 3. Partition the reveal: the named card vs. everything seen on the way to it.
                SelectFromCollectionEffect(
                    from = "revealed",
                    selection = SelectionMode.All,
                    filter = named,
                    storeSelected = "toHand",
                    storeRemainder = "toExile"
                ),
                MoveCollectionEffect(
                    from = "toHand",
                    destination = CardDestination.ToZone(Zone.HAND, Player.You)
                ),
                MoveCollectionEffect(
                    from = "toExile",
                    destination = CardDestination.ToZone(Zone.EXILE, Player.You)
                ),
                // 4. One life per exiled card — counted after the move, by entity id.
                Effects.LoseLife(
                    amount = DynamicAmounts.distinctEntitiesIn("toExile"),
                    target = EffectTarget.Controller
                )
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "78"
        artist = "Thomas M. Baxa"
        imageUri = "https://cards.scryfall.io/normal/front/3/b/3b9d100d-d20b-4018-8518-609113ec36d7.jpg?1783944544"
        ruling("2018-12-07", "You don't have to choose the name of a card that's still in your library, or even a card that's in your deck at all. If no card with the chosen name is in your library, you exile your library and lose 1 life for each of those cards.")
    }
}
