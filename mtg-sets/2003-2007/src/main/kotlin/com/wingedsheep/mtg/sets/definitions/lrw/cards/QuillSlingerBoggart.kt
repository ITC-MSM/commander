package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Quill-Slinger Boggart
 * {3}{B}
 * Creature — Goblin Warrior
 * 3/2
 * Whenever a player casts a Kithkin spell, you may have target player lose 1 life.
 *
 * "A player" is every player, the Boggart's controller included, so this is
 * [Triggers.anyPlayerCasts] — the same shape as Bog-Strider Ash and Elvish Handservant. The spell
 * filter is [GameObjectFilter.Any] rather than `.Creature`: a Kithkin spell is any spell with the
 * subtype, and Lorwyn prints Kindred noncreature ones (Militia's Pride is a Kithkin enchantment
 * spell). The "you may" is `optional = true`, which the builder lowers to a MayEffect; the yes/no
 * is asked as the ability goes on the stack, before targets are chosen, so declining never forces
 * a pointless target.
 */
val QuillSlingerBoggart = card("Quill-Slinger Boggart") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Goblin Warrior"
    power = 3
    toughness = 2
    oracleText = "Whenever a player casts a Kithkin spell, you may have target player lose 1 life."

    triggeredAbility {
        trigger = Triggers.anyPlayerCasts(GameObjectFilter.Any.withSubtype(Subtype.KITHKIN))
        optional = true
        val p = target("target player", Targets.Player)
        effect = Effects.LoseLife(1, p)
        description = "Whenever a player casts a Kithkin spell, you may have target player lose 1 life."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "137"
        artist = "Warren Mahy"
        flavorText = "\"A good day in Goldmeadow is one in which I don't spend all evening picking " +
            "quills out of my backside.\"\n—Calydd, kithkin farmer"
        imageUri = "https://cards.scryfall.io/normal/front/c/3/c3a15a9d-46d7-4181-8131-50ba46a11c7b.jpg?1783942885"
    }
}
