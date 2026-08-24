package com.wingedsheep.mtg.sets.definitions.ice.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.PreventionScope

/**
 * Warning
 * {W}
 * Instant
 *
 * Prevent all combat damage that would be dealt by target attacking creature this turn.
 *
 * The shield is directional — it stops damage the target *deals*, not damage dealt to it — so it is
 * `PreventAllDamageDealtBy` narrowed to combat damage, which is Safeguard's line word for word. The
 * "attacking" restriction rides the target requirement, not the shield.
 */
val Warning = card("Warning") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Prevent all combat damage that would be dealt by target attacking creature this turn."

    spell {
        val t = target("target", Targets.AttackingCreature)
        effect = Effects.PreventAllDamageDealtBy(t, scope = PreventionScope.CombatOnly)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "55"
        artist = "Pat Lewis"
        flavorText = "\"The folk of the Karplusan Mountains are impossible to ambush.\"\n—Lovisa Coldeyes, Balduvian Chieftain"
        imageUri = "https://cards.scryfall.io/normal/front/c/c/cca5b4a7-df11-4635-a147-df12cd13a67c.jpg"
    }
}
