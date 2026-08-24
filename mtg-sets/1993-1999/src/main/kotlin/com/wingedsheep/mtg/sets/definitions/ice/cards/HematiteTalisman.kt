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
 * Hematite Talisman
 * {2}
 * Artifact
 *
 * Whenever a player casts a red spell, you may pay {3}. If you do, untap target permanent.
 *
 * One of Ice Age's five Talismans — the cycle is a single shape with the spell filter recoloured.
 * "A player" is every player, this artifact's controller included, so the trigger is
 * [Triggers.anyPlayerCasts] (binding ANY, `Player.Each`); the ransom is [MayPayManaEffect], whose
 * yes/no and mana payment both happen on resolution. The permanent is a real target declared on the
 * ability, so it is locked in when the trigger goes on the stack, before anyone decides to pay.
 */
val HematiteTalisman = card("Hematite Talisman") {
    manaCost = "{2}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "Whenever a player casts a red spell, you may pay {3}. If you do, untap target permanent."

    triggeredAbility {
        trigger = Triggers.anyPlayerCasts(GameObjectFilter.Any.withColor(Color.RED))
        val permanent = target("target", Targets.Permanent)
        effect = MayPayManaEffect(ManaCost.parse("{3}"), Effects.Untap(permanent))
        description = "Whenever a player casts a red spell, you may pay {3}. " +
            "If you do, untap target permanent."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "320"
        artist = "Allen Williams"
        imageUri = "https://cards.scryfall.io/normal/front/8/3/83585337-56a9-44d2-9ed1-8a959bcfb010.jpg"
    }
}
