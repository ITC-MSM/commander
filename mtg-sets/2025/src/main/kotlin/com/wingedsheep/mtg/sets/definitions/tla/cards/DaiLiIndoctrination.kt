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
import com.wingedsheep.sdk.scripting.effects.MoveType
import com.wingedsheep.sdk.scripting.effects.RevealHandEffect
import com.wingedsheep.sdk.scripting.effects.SelectFromCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectionMode
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.TargetObject
import com.wingedsheep.sdk.scripting.targets.TargetOpponent
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Dai Li Indoctrination — {1}{B} Sorcery — Lesson
 *
 * Choose one —
 * • Target opponent reveals their hand. You choose a nonland permanent card from it.
 *   That player discards that card.
 * • Earthbend 2. (Target land you control becomes a 0/0 creature with haste that's
 *   still a land. Put two +1/+1 counters on it. When it dies or is exiled, return it
 *   to the battlefield tapped.)
 *
 * Mode 1 is a Duress-style targeted discard composed from the reveal/gather/select/move
 * pipeline (controller picks a nonland permanent card from the revealed hand). Mode 2 is
 * the [Effects.Earthbend] keyword action, identical in shape to Earthbending Lesson.
 */
val DaiLiIndoctrination = card("Dai Li Indoctrination") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery — Lesson"
    oracleText = "Choose one —\n" +
        "• Target opponent reveals their hand. You choose a nonland permanent card from it. That player discards that card.\n" +
        "• Earthbend 2. (Target land you control becomes a 0/0 creature with haste that's still a land. Put two +1/+1 counters on it. When it dies or is exiled, return it to the battlefield tapped.)"

    spell {
        modal(chooseCount = 1) {
            mode("Target opponent reveals their hand. You choose a nonland permanent card from it. That player discards that card") {
                val opponent = target("target opponent", TargetOpponent())
                effect = Effects.Composite(
                    listOf(
                        RevealHandEffect(opponent),
                        GatherCardsEffect(
                            source = CardSource.FromZone(Zone.HAND, Player.ContextPlayer(0)),
                            storeAs = "opponentHand"
                        ),
                        SelectFromCollectionEffect(
                            from = "opponentHand",
                            selection = SelectionMode.ChooseExactly(DynamicAmount.Fixed(1)),
                            chooser = Chooser.Controller,
                            filter = GameObjectFilter.NonlandPermanent,
                            storeSelected = "toDiscard",
                            prompt = "Choose a nonland permanent card to discard",
                            alwaysPrompt = true,
                            showAllCards = true
                        ),
                        MoveCollectionEffect(
                            from = "toDiscard",
                            destination = CardDestination.ToZone(Zone.GRAVEYARD, Player.ContextPlayer(0)),
                            moveType = MoveType.Discard
                        )
                    )
                )
            }
            mode("Earthbend 2") {
                val land = target("target land you control", TargetObject(filter = TargetFilter.Land.youControl()))
                effect = Effects.Earthbend(2, land)
            }
        }
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "93"
        artist = "Lius Lasahido"
        flavorText = "\"There is no war in Ba Sing Se.\""
        imageUri = "https://cards.scryfall.io/normal/front/e/c/eca652b8-44f1-4bd9-b4bf-036eeead13aa.jpg?1764120643"
    }
}
