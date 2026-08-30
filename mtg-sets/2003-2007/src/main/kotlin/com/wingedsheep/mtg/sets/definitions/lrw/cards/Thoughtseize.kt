package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.Chooser
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.MoveType
import com.wingedsheep.sdk.scripting.effects.RevealHandEffect
import com.wingedsheep.sdk.scripting.effects.SelectFromCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectionMode
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Thoughtseize
 * {B}
 * Sorcery
 *
 * Target player reveals their hand. You choose a nonland card from it. That player discards that
 * card. You lose 2 life.
 *
 * The hand-strip half is Duress's shape exactly — reveal, gather the target's hand, the *caster*
 * picks one, move it with [MoveType.Discard] so discard triggers see a real discard rather than a
 * bare zone change. Two things differ from Duress and both matter:
 *
 *  - it targets any **player**, not just an opponent, so [Targets.Player] rather than TargetOpponent;
 *  - the filter is `Nonland` only, so creatures are fair game.
 *
 * The life loss is a separate clause and is unconditional: per the 2020-08-07 ruling you lose 2
 * even when the target's hand holds nothing to take, which is why it sits outside the
 * gather/select/move chain rather than riding on the discard.
 */
val Thoughtseize = card("Thoughtseize") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "Target player reveals their hand. You choose a nonland card from it. " +
        "That player discards that card. You lose 2 life."

    spell {
        val player = target("target player", Targets.Player)
        effect = Effects.Composite(
            listOf(
                RevealHandEffect(player),
                GatherCardsEffect(
                    source = CardSource.FromZone(Zone.HAND, Player.ContextPlayer(0)),
                    storeAs = "revealedHand",
                ),
                SelectFromCollectionEffect(
                    from = "revealedHand",
                    selection = SelectionMode.ChooseExactly(DynamicAmount.Fixed(1)),
                    chooser = Chooser.Controller,
                    filter = GameObjectFilter.Nonland,
                    storeSelected = "toDiscard",
                    prompt = "Choose a nonland card to discard",
                    alwaysPrompt = true,
                    showAllCards = true,
                ),
                MoveCollectionEffect(
                    from = "toDiscard",
                    destination = CardDestination.ToZone(Zone.GRAVEYARD, Player.ContextPlayer(0)),
                    moveType = MoveType.Discard,
                ),
                Effects.LoseLife(2, EffectTarget.PlayerRef(Player.You)),
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "145"
        artist = "Aleksi Briclot"
        flavorText = "\"Any dream is a robust harvest. Still, I prefer the timeworn dreams, heavy " +
            "with import, that haunt the obsessive mind.\""
        imageUri = "https://cards.scryfall.io/normal/front/3/d/3df8c148-e87d-4043-9d8b-ec72bf8b6d5d.jpg?1783942883"
        ruling(
            "2020-08-07",
            "You lose 2 life even if the target player has no nonland cards in their hand to discard.",
        )
    }
}
