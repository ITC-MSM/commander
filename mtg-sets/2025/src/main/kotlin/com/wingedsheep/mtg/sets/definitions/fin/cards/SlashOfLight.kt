package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Slash of Light
 * {1}{W}
 * Instant
 * Slash of Light deals damage equal to the number of creatures you control plus the
 * number of Equipment you control to target creature.
 *
 * The damage amount sums two battlefield counts (creatures + Equipment, each scoped to
 * the controller) via [DynamicAmount.Add]. The spell itself is the damage source.
 */
val SlashOfLight = card("Slash of Light") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Slash of Light deals damage equal to the number of creatures you control plus the number of Equipment you control to target creature."

    spell {
        val t = target("target", Targets.Creature)
        effect = Effects.DealDamage(
            DynamicAmount.Add(
                DynamicAmount.Count(Player.You, Zone.BATTLEFIELD, GameObjectFilter.Creature),
                DynamicAmount.Count(
                    Player.You,
                    Zone.BATTLEFIELD,
                    GameObjectFilter.Artifact.withSubtype(Subtype("Equipment"))
                )
            ),
            t
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "32"
        artist = "Nathaniel Himawan"
        flavorText = "The endless struggle that raged over two thousand years had ended, and peace prevailed once more."
        imageUri = "https://cards.scryfall.io/normal/front/d/a/da6d9529-3cb0-4adc-8209-b9b02db3bf54.jpg?1748705874"
    }
}
