package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.LoseLifeEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Dark Confidant
 * {1}{B}
 * Creature — Human Wizard
 * 2/1
 *
 * At the beginning of your upkeep, reveal the top card of your library and put
 * that card into your hand. You lose life equal to its mana value.
 *
 * Canonical printing lives here (Ravnica: City of Guilds, 2005 — earliest real
 * expansion). Later printings (e.g. Final Fantasy) contribute only a `Printing` row.
 */
val DarkConfidant = card("Dark Confidant") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Human Wizard"
    power = 2
    toughness = 1
    oracleText = "At the beginning of your upkeep, reveal the top card of your library and put that card into your hand. You lose life equal to its mana value."

    triggeredAbility {
        trigger = Triggers.YourUpkeep
        effect = Effects.Composite(
            listOf(
                GatherCardsEffect(
                    source = CardSource.TopOfLibrary(DynamicAmount.Fixed(1), Player.You),
                    storeAs = "revealed"
                ),
                MoveCollectionEffect(
                    from = "revealed",
                    destination = CardDestination.ToZone(Zone.HAND, Player.You),
                    revealed = true
                ),
                LoseLifeEffect(
                    DynamicAmount.StoredCardManaValue("revealed"),
                    EffectTarget.Controller
                )
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "81"
        artist = "Ron Spears"
        flavorText = "Greatness, at any cost."
        imageUri = "https://cards.scryfall.io/normal/front/9/4/94f7a441-bf2d-46fb-a7b6-9bd6137f86d9.jpg?1598914714"
    }
}
