package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardLayout
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.MayCastFromGraveyard
import com.wingedsheep.sdk.scripting.MayPlayLandsFromGraveyard
import com.wingedsheep.sdk.scripting.RedirectZoneChange
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.predicates.ControllerPredicate
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Walk-In Closet // Forgotten Cellar (DSK 205) — split-layout Room (CR 709.5).
 *
 * Walk-In Closet {2}{G} — Enchantment — Room
 *   You may play lands from your graveyard.
 *
 * Forgotten Cellar {3}{G}{G} — Enchantment — Room
 *   When you unlock this door, you may cast spells from your graveyard this turn, and if a card
 *   would be put into your graveyard from anywhere this turn, exile it instead.
 *
 * Cast each half separately; the cast face enters unlocked, the other locked. Pay the locked
 * face's printed mana cost as a sorcery-speed special action to unlock it (CR 709.5e).
 *
 * Walk-In Closet is the durable static [MayPlayLandsFromGraveyard]. Forgotten Cellar's unlock
 * trigger grants two *durational* riders until end of turn — the runtime siblings of Festival of
 * Embers' printed abilities: [Effects.GrantStaticAbility] of [MayCastFromGraveyard] (read by the
 * graveyard-cast enumerator/resolver alongside printed grants) and [Effects.GrantReplacementEffect]
 * of [RedirectZoneChange] to exile (read by the zone-change redirect path). The cast permission is
 * filtered to [GameObjectFilter.Nonland] so it grants "cast spells", not land plays.
 */
val WalkInClosetForgottenCellar = card("Walk-In Closet // Forgotten Cellar") {
    layout = CardLayout.SPLIT
    colorIdentity = "G"

    face("Walk-In Closet") {
        manaCost = "{2}{G}"
        typeLine = "Enchantment — Room"
        oracleText = "You may play lands from your graveyard."

        staticAbility {
            ability = MayPlayLandsFromGraveyard
        }
    }

    face("Forgotten Cellar") {
        manaCost = "{3}{G}{G}"
        typeLine = "Enchantment — Room"
        oracleText = "When you unlock this door, you may cast spells from your graveyard this turn, " +
            "and if a card would be put into your graveyard from anywhere this turn, exile it instead."

        triggeredAbility {
            trigger = Triggers.OnDoorUnlocked
            effect = Effects.Composite(
                listOf(
                    // ... you may cast spells from your graveyard this turn ...
                    Effects.GrantStaticAbility(
                        ability = MayCastFromGraveyard(filter = GameObjectFilter.Nonland),
                        target = EffectTarget.Self,
                        duration = Duration.EndOfTurn,
                    ),
                    // ... and if a card would be put into your graveyard from anywhere this turn,
                    // exile it instead.
                    Effects.GrantReplacementEffect(
                        replacement = RedirectZoneChange(
                            newDestination = Zone.EXILE,
                            appliesTo = EventPattern.ZoneChangeEvent(
                                filter = GameObjectFilter(controllerPredicate = ControllerPredicate.OwnedByYou),
                                to = Zone.GRAVEYARD,
                            ),
                        ),
                        target = EffectTarget.Self,
                        duration = Duration.EndOfTurn,
                    ),
                ),
            )
            description = "When you unlock this door, you may cast spells from your graveyard this turn, " +
                "and if a card would be put into your graveyard from anywhere this turn, exile it instead."
        }
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "205"
        artist = "Miklós Ligeti"
        imageUri = "https://cards.scryfall.io/normal/front/0/a/0adcd4e5-d542-4293-8774-ace2305ef820.jpg?1726867658"
    }
}
