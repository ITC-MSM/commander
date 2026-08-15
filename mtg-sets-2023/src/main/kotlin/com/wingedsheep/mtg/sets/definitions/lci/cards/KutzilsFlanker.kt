package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.ModalEffect
import com.wingedsheep.sdk.scripting.effects.Mode
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.scripting.values.TurnTracker

/**
 * Kutzil's Flanker
 * {2}{W}
 * Creature — Cat Warrior
 * 3/1
 * Flash
 * When this creature enters, choose one —
 * • Put a +1/+1 counter on this creature for each creature that left the battlefield under your
 *   control this turn.
 * • You gain 2 life and scry 2.
 * • Exile target player's graveyard.
 */
val KutzilsFlanker = card("Kutzil's Flanker") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Cat Warrior"
    oracleText = "Flash\n" +
        "When this creature enters, choose one —\n" +
        "• Put a +1/+1 counter on this creature for each creature that left the battlefield under " +
        "your control this turn.\n" +
        "• You gain 2 life and scry 2.\n" +
        "• Exile target player's graveyard."
    power = 3
    toughness = 1
    keywords(Keyword.FLASH)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = ModalEffect.chooseOne(
            Mode.noTarget(
                Effects.AddDynamicCounters(
                    Counters.PLUS_ONE_PLUS_ONE,
                    DynamicAmount.TurnTracking(Player.You, TurnTracker.CREATURES_LEFT_BATTLEFIELD),
                    EffectTarget.Self
                ),
                "Put a +1/+1 counter on this creature for each creature that left the battlefield " +
                    "under your control this turn"
            ),
            Mode.noTarget(
                Effects.Composite(listOf(Effects.GainLife(2), Effects.Scry(2))),
                "You gain 2 life and scry 2"
            ),
            Mode.withTarget(
                Effects.Composite(
                    listOf(
                        GatherCardsEffect(
                            source = CardSource.FromZone(Zone.GRAVEYARD, Player.ContextPlayer(0)),
                            storeAs = "targetGraveyard"
                        ),
                        MoveCollectionEffect(
                            from = "targetGraveyard",
                            destination = CardDestination.ToZone(Zone.EXILE, Player.ContextPlayer(0))
                        )
                    )
                ),
                Targets.Player,
                "Exile target player's graveyard"
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "20"
        artist = "Michele Giorgi"
        imageUri = "https://cards.scryfall.io/normal/front/d/1/d1201811-54ab-4c4e-b6e1-19b0d07e5ede.jpg?1782694597"
    }
}
