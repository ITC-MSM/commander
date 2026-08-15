package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.conditions.Exists
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MayPlayExpiry
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Lightning, Security Sergeant — Final Fantasy #560
 * {2}{R} · Legendary Creature — Human Soldier · 2/3
 *
 * Menace
 * Whenever Lightning deals combat damage to a player, exile the top card of your library.
 * You may play that card for as long as you control Lightning.
 *
 * The impulse-exile + conditional play permission is the Possibility Technician shape
 * (Gather top → Move to exile → [Effects.GrantMayPlayFromExile] with [MayPlayExpiry.Permanent]
 * and a re-evaluated gate). "For as long as you control Lightning" is modeled as an [Exists]
 * gate for a battlefield creature named "Lightning, Security Sergeant" — Lightning is legendary,
 * so at most one is ever controlled, making this equivalent to controlling the source. As with
 * Possibility Technician, the gate is re-evaluated on every legal-action query, so the permission
 * ends when Lightning leaves; the only divergence from the strict one-way CR 611.2i duration is
 * that it would resume if a new Lightning re-entered your control (the accepted corpus approximation).
 */
val LightningSecuritySergeant = card("Lightning, Security Sergeant") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Legendary Creature — Human Soldier"
    power = 2
    toughness = 3
    oracleText = "Menace (This creature can't be blocked except by two or more creatures.)\n" +
        "Whenever Lightning deals combat damage to a player, exile the top card of your library. " +
        "You may play that card for as long as you control Lightning."

    keywords(Keyword.MENACE)

    triggeredAbility {
        trigger = Triggers.DealsCombatDamageToPlayer
        effect = Effects.Composite(
            listOf(
                GatherCardsEffect(
                    source = CardSource.TopOfLibrary(DynamicAmount.Fixed(1)),
                    storeAs = "exiledCard"
                ),
                MoveCollectionEffect(
                    from = "exiledCard",
                    destination = CardDestination.ToZone(Zone.EXILE)
                ),
                Effects.GrantMayPlayFromExile(
                    from = "exiledCard",
                    expiry = MayPlayExpiry.Permanent,
                    condition = Exists(
                        player = Player.You,
                        zone = Zone.BATTLEFIELD,
                        filter = GameObjectFilter.Creature.named("Lightning, Security Sergeant")
                    )
                )
            )
        )
        description = "Whenever Lightning deals combat damage to a player, exile the top card of " +
            "your library. You may play that card for as long as you control Lightning."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "560"
        artist = "Ramza Psyru"
        imageUri = "https://cards.scryfall.io/normal/front/4/a/4ad4dce3-6e43-4528-b570-85547d03164e.jpg?1782686124"
    }
}
