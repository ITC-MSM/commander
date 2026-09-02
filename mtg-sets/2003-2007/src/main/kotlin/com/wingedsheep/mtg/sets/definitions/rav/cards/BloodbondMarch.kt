package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.EntityReference

/**
 * Bloodbond March — Ravnica: City of Guilds #192
 * {2}{B}{G} · Enchantment · Rare
 *
 * Whenever a player casts a creature spell, each player returns all cards with the same name as
 * that spell from their graveyard to the battlefield.
 *
 * Modelling notes:
 * - The trigger is ANY-bound on every player's creature spells, so the spell on the stack is the
 *   triggering entity and "the same name as that spell" is
 *   `sharingNameWith(EntityReference.Triggering)` — the filter Spellweaver Helix reads against
 *   its imprint pile, here read against every graveyard at once via `Player.Each`.
 * - The spell itself is on the stack, not in a graveyard, so it is never among the gathered
 *   cards; the trigger resolves before the spell does, which is why the freshly cast creature
 *   does not return itself.
 * - "Each player returns … from their graveyard" is one gather across every graveyard and one
 *   move with `underOwnersControl`, so each returned card comes back under its own owner's
 *   control — including the caster's opponents' copies. Nothing is chosen and nothing is tapped.
 */
val BloodbondMarch = card("Bloodbond March") {
    manaCost = "{2}{B}{G}"
    colorIdentity = "BG"
    typeLine = "Enchantment"
    oracleText = "Whenever a player casts a creature spell, each player returns all cards with " +
        "the same name as that spell from their graveyard to the battlefield."

    triggeredAbility {
        trigger = Triggers.anyPlayerCasts(spellFilter = GameObjectFilter.Creature)
        effect = Effects.Pipeline {
            val sameName = gather(
                CardSource.FromZone(
                    zone = Zone.GRAVEYARD,
                    player = Player.Each,
                    filter = GameObjectFilter.Any.sharingNameWith(EntityReference.Triggering)
                )
            )
            move(sameName, CardDestination.ToZone(Zone.BATTLEFIELD), underOwnersControl = true)
        }
        description = "Whenever a player casts a creature spell, each player returns all cards " +
            "with the same name as that spell from their graveyard to the battlefield."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "192"
        artist = "Jim Nelson"
        flavorText = "The Golgari support a vast army because death never ends its soldiers' service."
        imageUri = "https://cards.scryfall.io/normal/front/f/b/fb977f5b-2202-43c1-a8c0-7fba8c093fa2.jpg?1783943627"
    }
}
