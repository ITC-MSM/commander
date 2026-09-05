package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.MoveType
import com.wingedsheep.sdk.scripting.references.Player

val NightmareVoid = card("Nightmare Void") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "Target player reveals their hand. You choose a card from it. That player discards that card.\nDredge 2 (If you would draw a card, you may mill two cards instead. If you do, return this card from your graveyard to your hand.)"

    keywordAbility(KeywordAbility.dredge(2))

    spell {
        target("player", Targets.Player)
        effect = Effects.Pipeline {
            val hand = gather(CardSource.FromZone(Zone.HAND, Player.ContextPlayer(0)), revealed = true)
            val discarded = chooseExactly(1, hand, prompt = "Choose a card for that player to discard")
            move(discarded, CardDestination.ToZone(Zone.GRAVEYARD, Player.ContextPlayer(0)),
                moveType = MoveType.Discard)
        }
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "100"
        artist = "Chippy"
        imageUri = "https://cards.scryfall.io/normal/front/8/9/892299e9-8ee9-4a81-b4aa-b03ad2565eb4.jpg?1783943665"
        ruling("2014-02-01", "If you target yourself with this spell, you must reveal your entire hand to the other players just as any other player would.")
    }
}
