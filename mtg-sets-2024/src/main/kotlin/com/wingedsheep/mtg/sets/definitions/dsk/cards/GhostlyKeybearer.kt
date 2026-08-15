package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Ghostly Keybearer — Duskmourn: House of Horror #61
 * {3}{U}
 * Creature — Spirit
 * 3/3
 *
 * Flying
 * Whenever this creature deals combat damage to a player, unlock a locked door of up to one
 * target Room you control.
 *
 * The combat-damage trigger uses the resolution-time "unlock a door" effect (CR 709.5f): it gives
 * a locked half of the targeted Room the "unlocked" designation, firing that face's "When you
 * unlock this door" triggers (CR 709.5h) exactly as the unlock-cost special action would. "Up to
 * one target Room you control" is an optional `TargetObject` restricted to a Room with a locked
 * door — a fully-unlocked Room has nothing left to unlock, so it isn't a legal target, and the
 * controller may also choose no target.
 */
val GhostlyKeybearer = card("Ghostly Keybearer") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Spirit"
    power = 3
    toughness = 3
    oracleText = "Flying\nWhenever this creature deals combat damage to a player, unlock a locked " +
        "door of up to one target Room you control."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.DealsCombatDamageToPlayer
        val room = target(
            "room",
            TargetObject(
                optional = true,
                filter = TargetFilter(
                    GameObjectFilter.Any.withSubtype(Subtype.ROOM).youControl()
                ).hasLockedDoor()
            )
        )
        effect = Effects.UnlockDoor(room)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "61"
        artist = "Marco Gorlei"
        imageUri = "https://cards.scryfall.io/normal/front/1/1/11d04a98-6997-4653-9719-e6b215567599.jpg?1726286080"
    }
}
