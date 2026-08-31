package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.Chooser
import com.wingedsheep.sdk.scripting.effects.MoveType
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Thieving Sprite
 * {2}{B}
 * Creature — Faerie Rogue
 * 1/1
 *
 * Flying
 * When this creature enters, target player reveals X cards from their hand, where X is the number
 * of Faeries you control. You choose one of those cards. That player discards that card.
 *
 * Two choosers, in order, which is the whole shape of the card: the *targeted player* picks which
 * X cards to reveal, then the Sprite's *controller* picks one of those revealed cards to be
 * discarded. `ChooseExactly` clamps to the hand when the player holds fewer than X cards, which is
 * the right reading of "reveals X cards" against a small hand — they reveal their whole hand.
 *
 * The bare noun "Faeries" counts every Faerie *permanent* you control, and the Sprite itself is a
 * Faerie that has already entered when the trigger resolves, so X is at least 1 in the normal
 * case. X is counted on resolution, not on the trigger going on the stack.
 */
val ThievingSprite = card("Thieving Sprite") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Faerie Rogue"
    power = 1
    toughness = 1
    oracleText = "Flying\n" +
        "When this creature enters, target player reveals X cards from their hand, where X is " +
        "the number of Faeries you control. You choose one of those cards. That player discards " +
        "that card."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        target("target player", Targets.Player)
        effect = Effects.Pipeline {
            val hand = gather(CardSource.FromZone(Zone.HAND, Player.TargetPlayer))
            val revealed = chooseExactly(
                DynamicAmounts.battlefield(
                    Player.You,
                    GameObjectFilter.Permanent.withSubtype(Subtype.FAERIE)
                ).count(),
                from = hand,
                chooser = Chooser.TargetPlayer,
                prompt = "Choose cards to reveal",
                selectedLabel = "Reveal"
            )
            reveal(revealed)
            val chosen = chooseExactly(
                1,
                from = revealed,
                chooser = Chooser.Controller,
                prompt = "Choose a revealed card for that player to discard",
                selectedLabel = "Discard"
            )
            move(
                chosen,
                CardDestination.ToZone(Zone.GRAVEYARD, Player.TargetPlayer),
                moveType = MoveType.Discard
            )
        }
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "143"
        artist = "Dan Murayama Scott"
        imageUri = "https://cards.scryfall.io/normal/front/9/b/9b5b51c3-60a8-45b2-9de0-605388091e8a.jpg?1783942881"
    }
}
