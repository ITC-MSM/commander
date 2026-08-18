package com.wingedsheep.mtg.sets.definitions.mkm.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.FaceDownMode
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MayPlayExpiry
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Outrageous Robbery — Murders at Karlov Manor #97
 * {X}{B}{B} · Instant · Rare
 *
 * This is the same hidden-exile permission pipeline used by Expensive Taste: gathering from the
 * targeted opponent's library privately reveals the cards to the caster, HIDDEN keeps them opaque
 * to everyone else in exile, and a permanent may-play permission lets the caster play lands or
 * cast spells while spending mana as though it were mana of any type.
 */
val OutrageousRobbery = card("Outrageous Robbery") {
    manaCost = "{X}{B}{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "Target opponent exiles the top X cards of their library face down. You may " +
        "look at and play those cards for as long as they remain exiled. If you cast a spell " +
        "this way, you may spend mana as though it were mana of any type to cast it."

    spell {
        target("target opponent", Targets.Opponent)
        effect = Effects.Composite(
            GatherCardsEffect(
                source = CardSource.TopOfLibrary(DynamicAmount.XValue, Player.TargetOpponent),
                storeAs = "robbedCards",
            ),
            MoveCollectionEffect(
                from = "robbedCards",
                destination = CardDestination.ToZone(Zone.EXILE, Player.TargetOpponent),
                faceDown = FaceDownMode.HIDDEN,
            ),
            Effects.GrantMayPlayFromExile(
                from = "robbedCards",
                expiry = MayPlayExpiry.Permanent,
                withAnyManaType = true,
            ),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "97"
        artist = "Kai Carpenter"
        flavorText = "\"When you can't erase your own trail, leave as many false ones as " +
            "possible.\"\n—Etrata"
        imageUri = "https://cards.scryfall.io/normal/front/b/8/b87813fa-ad12-4062-bb9e-436d8418fba5.jpg?1783912894"

        ruling(
            "2024-02-02",
            "You pay all costs and follow all normal timing rules for cards played this way. For " +
                "example, if one of the exiled cards is a land card, you may play it only during " +
                "your main phase while the stack is empty.",
        )
        ruling(
            "2024-02-02",
            "If you leave the game, the exiled cards remain exiled face down indefinitely. No " +
                "player may look at them.",
        )
    }
}
