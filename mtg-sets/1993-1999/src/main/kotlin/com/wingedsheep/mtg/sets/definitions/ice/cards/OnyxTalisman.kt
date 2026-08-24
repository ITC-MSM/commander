package com.wingedsheep.mtg.sets.definitions.ice.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.MayPayManaEffect

/**
 * Onyx Talisman
 * {2}
 * Artifact
 *
 * Whenever a player casts a black spell, you may pay {3}. If you do, untap target permanent.
 *
 * One of Ice Age's five Talismans — the cycle is a single shape with the spell filter recoloured.
 * "A player" is every player, this artifact's controller included, so the trigger is
 * [Triggers.anyPlayerCasts] (binding ANY, `Player.Each`); the ransom is [MayPayManaEffect], whose
 * yes/no and mana payment both happen on resolution. The permanent is a real target declared on the
 * ability, so it is locked in when the trigger goes on the stack, before anyone decides to pay.
 */
val OnyxTalisman = card("Onyx Talisman") {
    manaCost = "{2}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "Whenever a player casts a black spell, you may pay {3}. If you do, untap target permanent."

    triggeredAbility {
        trigger = Triggers.anyPlayerCasts(GameObjectFilter.Any.withColor(Color.BLACK))
        val permanent = target("target", Targets.Permanent)
        effect = MayPayManaEffect(ManaCost.parse("{3}"), Effects.Untap(permanent))
        description = "Whenever a player casts a black spell, you may pay {3}. " +
            "If you do, untap target permanent."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "331"
        artist = "Sandra Everingham"
        imageUri = "https://cards.scryfall.io/normal/front/a/8/a89b2368-1180-4821-bcb8-8161c18e5538.jpg"
    }
}
