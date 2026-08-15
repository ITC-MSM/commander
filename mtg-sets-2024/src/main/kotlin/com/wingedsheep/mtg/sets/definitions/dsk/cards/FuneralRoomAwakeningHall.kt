package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardLayout
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Funeral Room // Awakening Hall (DSK 100) — split-layout Room (CR 709.5).
 *
 * Funeral Room {2}{B} — Enchantment — Room
 *   Whenever a creature you control dies, each opponent loses 1 life and you gain 1 life.
 *
 * Awakening Hall {6}{B}{B} — Enchantment — Room
 *   When you unlock this door, return all creature cards from your graveyard to the battlefield.
 *
 * Cast each half separately; the cast face enters unlocked, the other locked. Pay the locked face's
 * printed mana cost as a sorcery-speed special action to unlock it (CR 709.5e).
 *
 * Funeral Room is a [Triggers.YourCreatureDies] drain — `LoseLife(1, EachOpponent)` +
 * `GainLife(1)`, the canonical "each opponent loses 1 life and you gain 1 life" composite (cf.
 * Acolyte of Aclazotz). Awakening Hall is a "when you unlock this door" trigger
 * ([Triggers.OnDoorUnlocked], CR 709.5h) whose mass reanimation gathers every creature card in
 * *your* graveyard and returns them under your control — the Twilight's Call
 * `GatherCards → MoveCollection` pipeline scoped to [Player.You].
 */
val FuneralRoomAwakeningHall = card("Funeral Room // Awakening Hall") {
    layout = CardLayout.SPLIT
    colorIdentity = "B"

    face("Funeral Room") {
        manaCost = "{2}{B}"
        typeLine = "Enchantment — Room"
        oracleText = "Whenever a creature you control dies, each opponent loses 1 life and you gain 1 life."

        triggeredAbility {
            trigger = Triggers.YourCreatureDies
            effect = Effects.Composite(
                Effects.LoseLife(1, EffectTarget.PlayerRef(Player.EachOpponent)),
                Effects.GainLife(1)
            )
        }
    }

    face("Awakening Hall") {
        manaCost = "{6}{B}{B}"
        typeLine = "Enchantment — Room"
        oracleText = "When you unlock this door, return all creature cards from your graveyard to the battlefield."

        triggeredAbility {
            trigger = Triggers.OnDoorUnlocked
            effect = Effects.Composite(
                GatherCardsEffect(
                    source = CardSource.FromZone(
                        zone = Zone.GRAVEYARD,
                        player = Player.You,
                        filter = GameObjectFilter.Creature
                    ),
                    storeAs = "graveyardCreatures"
                ),
                MoveCollectionEffect(
                    from = "graveyardCreatures",
                    destination = CardDestination.ToZone(Zone.BATTLEFIELD),
                    underOwnersControl = true
                )
            )
        }
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "100"
        artist = "Miklós Ligeti"
        imageUri = "https://cards.scryfall.io/normal/front/4/8/48237c98-5067-47a8-af74-7b9bce57c6a4.jpg?1726867790"
    }
}
