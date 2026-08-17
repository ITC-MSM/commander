package com.wingedsheep.mtg.sets.definitions.mrd.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Second Sunrise — Mirrodin #20
 * {1}{W}{W} · Instant · Rare
 *
 * Each player returns to the battlefield all artifact, creature, enchantment, and land cards in
 * their graveyard that were put there from the battlefield this turn.
 *
 * One Gather → Move pipeline over `Player.Each` (the same all-graveyards sweep Rise of the Dark
 * Realms uses), with two details that carry the card's whole meaning:
 *
 *  - **`putIntoGraveyardFromBattlefieldThisTurn()`** is the "put there from the battlefield this
 *    turn" clause. The predicate reads the per-card `PutIntoGraveyardThisTurnComponent` that
 *    `ZoneTransitionService` stamps on every graveyard arrival and strips when the card leaves, so a
 *    creature milled this turn — or one that died two turns ago — is correctly skipped. The
 *    component is wiped at each untap step, which is what makes it per-turn.
 *  - **`underOwnersControl = true`** is "*each player* returns … in *their* graveyard". Unlike a
 *    reanimation spell, Second Sunrise hands nothing to its caster: every card comes back under its
 *    own owner's control (CR 610.3c), including the opponents' dead.
 *
 * The type list is spelled out as an explicit union rather than `GameObjectFilter.Permanent`
 * because the printed text names exactly four types — a planeswalker or battle card that hit the
 * graveyard from the battlefield this turn stays there.
 */
val SecondSunrise = card("Second Sunrise") {
    manaCost = "{1}{W}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Each player returns to the battlefield all artifact, creature, enchantment, and " +
        "land cards in their graveyard that were put there from the battlefield this turn."

    spell {
        effect = Effects.Composite(
            listOf(
                GatherCardsEffect(
                    source = CardSource.FromZone(
                        zone = Zone.GRAVEYARD,
                        player = Player.Each,
                        filter = (
                            GameObjectFilter.Artifact or
                                GameObjectFilter.Creature or
                                GameObjectFilter.Enchantment or
                                GameObjectFilter.Land
                            ).putIntoGraveyardFromBattlefieldThisTurn()
                    ),
                    storeAs = "returningCards"
                ),
                MoveCollectionEffect(
                    from = "returningCards",
                    destination = CardDestination.ToZone(Zone.BATTLEFIELD),
                    underOwnersControl = true
                )
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "20"
        artist = "Greg Staples"
        flavorText = "The bright tunnel sometimes leads back to life."
        imageUri = "https://cards.scryfall.io/normal/front/4/e/4e50ee7c-f2a2-4d49-a1cc-8233fd8dd0c5.jpg?1783944558"
    }
}
