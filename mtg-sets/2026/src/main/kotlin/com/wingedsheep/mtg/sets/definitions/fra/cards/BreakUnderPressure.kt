package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.Chooser
import com.wingedsheep.sdk.scripting.effects.CollectionFilter
import com.wingedsheep.sdk.scripting.effects.FilterCollectionEffect
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.MoveType
import com.wingedsheep.sdk.scripting.effects.SelectFromCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectionMode
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * The target opponent's creatures and planeswalkers are gathered, narrowed to those tied for the
 * greatest mana value, and that player picks which of the tied permanents to sacrifice. The life
 * gain is unconditional — it happens even when the opponent controls nothing to sacrifice.
 */
val BreakUnderPressure = card("Break Under Pressure") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "Target opponent sacrifices a creature or planeswalker with the greatest mana value " +
        "among creatures and planeswalkers they control. You gain 2 life."

    spell {
        target("target opponent", Targets.Opponent)
        effect = Effects.Composite(
            GatherCardsEffect(
                source = CardSource.ControlledPermanents(
                    player = Player.ContextPlayer(0),
                    filter = GameObjectFilter.CreatureOrPlaneswalker
                ),
                storeAs = "candidates"
            ),
            FilterCollectionEffect(
                from = "candidates",
                filter = CollectionFilter.GreatestManaValue,
                storeMatching = "greatest"
            ),
            SelectFromCollectionEffect(
                from = "greatest",
                selection = SelectionMode.ChooseExactly(DynamicAmount.Fixed(1)),
                chooser = Chooser.TargetPlayer,
                storeSelected = "sacrificed",
                prompt = "Choose a creature or planeswalker with the greatest mana value to sacrifice",
                useTargetingUI = true
            ),
            MoveCollectionEffect(
                from = "sacrificed",
                destination = CardDestination.ToZone(Zone.GRAVEYARD),
                moveType = MoveType.Sacrifice
            ),
            Effects.GainLife(2)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "50"
        artist = "Jeff Miracola"
        flavorText = "\"Please, do take it personally.\"\n—Ingris Stingerquill"
        imageUri = "https://cards.scryfall.io/normal/front/4/6/46974d94-e900-43e4-92b5-4fb9b9f7cf46.jpg?1789385622"
        inBooster = false
    }
}
