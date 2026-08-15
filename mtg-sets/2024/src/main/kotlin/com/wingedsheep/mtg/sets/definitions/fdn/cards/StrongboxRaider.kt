package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.GrantMayPlayFromExileEffect
import com.wingedsheep.sdk.scripting.effects.MayPlayExpiry
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectFromCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectionMode
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Strongbox Raider
 * {2}{R}{R}
 * Creature — Orc Pirate
 * 5/2
 * Raid — When this creature enters, if you attacked this turn, exile the top two cards of your
 * library. Choose one of them. Until the end of your next turn, you may play that card.
 *
 * Raid is an intervening-if ETB trigger ([Conditions.YouAttackedThisTurn], CR 603.4). The impulse
 * half is the standard Gather → Move(EXILE) → Select(one) → grant may-play pipeline (see Riverwheel
 * Sweep); the non-chosen exiled card stays in exile with no play permission, matching "Choose one of
 * them," and the chosen card is playable until the end of the controller's next turn.
 */
val StrongboxRaider = card("Strongbox Raider") {
    manaCost = "{2}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Orc Pirate"
    power = 5
    toughness = 2
    oracleText = "Raid — When this creature enters, if you attacked this turn, exile the top two cards " +
        "of your library. Choose one of them. Until the end of your next turn, you may play that card."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        triggerCondition = Conditions.YouAttackedThisTurn
        effect = Effects.Composite(
            listOf(
                GatherCardsEffect(
                    source = CardSource.TopOfLibrary(DynamicAmount.Fixed(2)),
                    storeAs = "exiled"
                ),
                MoveCollectionEffect(
                    from = "exiled",
                    destination = CardDestination.ToZone(Zone.EXILE)
                ),
                SelectFromCollectionEffect(
                    from = "exiled",
                    selection = SelectionMode.ChooseExactly(DynamicAmount.Fixed(1)),
                    storeSelected = "chosen",
                    prompt = "Choose a card you may play until the end of your next turn"
                ),
                GrantMayPlayFromExileEffect(
                    from = "chosen",
                    expiry = MayPlayExpiry.UntilEndOfNextTurn
                )
            )
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "96"
        artist = "Craig J Spearing"
        flavorText = "\"Tonight, we feast!\""
        imageUri = "https://cards.scryfall.io/normal/front/b/2/b2223eb8-59f9-489b-a3f3-b6496218cb79.jpg?1782689184"
    }
}
