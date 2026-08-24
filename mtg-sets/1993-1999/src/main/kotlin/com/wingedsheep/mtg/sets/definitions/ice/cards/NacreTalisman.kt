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
 * Nacre Talisman
 * {2}
 * Artifact
 *
 * Whenever a player casts a white spell, you may pay {3}. If you do, untap target permanent.
 *
 * One of Ice Age's five Talismans — the cycle is a single shape with the spell filter recoloured.
 * "A player" is every player, this artifact's controller included, so the trigger is
 * [Triggers.anyPlayerCasts] (binding ANY, `Player.Each`); the ransom is [MayPayManaEffect], whose
 * yes/no and mana payment both happen on resolution. The permanent is a real target declared on the
 * ability, so it is locked in when the trigger goes on the stack, before anyone decides to pay.
 */
val NacreTalisman = card("Nacre Talisman") {
    manaCost = "{2}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "Whenever a player casts a white spell, you may pay {3}. If you do, untap target permanent."

    triggeredAbility {
        trigger = Triggers.anyPlayerCasts(GameObjectFilter.Any.withColor(Color.WHITE))
        val permanent = target("target", Targets.Permanent)
        effect = MayPayManaEffect(ManaCost.parse("{3}"), Effects.Untap(permanent))
        description = "Whenever a player casts a white spell, you may pay {3}. " +
            "If you do, untap target permanent."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "329"
        artist = "Mark Tedin"
        imageUri = "https://cards.scryfall.io/normal/front/0/6/06912236-8225-4eb0-8086-c6a163c69892.jpg"
    }
}
