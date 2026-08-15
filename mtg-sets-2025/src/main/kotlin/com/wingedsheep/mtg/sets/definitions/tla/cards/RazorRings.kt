package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.scripting.values.EntityNumericProperty
import com.wingedsheep.sdk.scripting.values.EntityReference

/**
 * Razor Rings {1}{W}
 * Instant
 *
 * Razor Rings deals 4 damage to target attacking or blocking creature. You gain life equal to
 * the excess damage dealt this way.
 *
 * Composed from existing atoms (mirrors Hell to Pay's excess read): [Effects.DealDamage] deals a
 * fixed 4 to the target and marks the damage, then [Effects.GainLife] reads the post-damage excess
 * via `EntityProperty(EntityReference.Target(0), ExcessMarkedDamage)` — `max(0, marked − toughness)`
 * (CR 120.4a). CompositeEffect resolves its steps sequentially with no interleaved SBA pass, so the
 * marked damage in scope at the second step is exactly the 4 this spell just dealt.
 */
val RazorRings = card("Razor Rings") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Razor Rings deals 4 damage to target attacking or blocking creature. You gain " +
        "life equal to the excess damage dealt this way."

    spell {
        val creature = target("target attacking or blocking creature", TargetCreature(filter = TargetFilter.AttackingOrBlockingCreature))
        effect = Effects.Composite(
            Effects.DealDamage(4, creature),
            Effects.GainLife(
                DynamicAmount.EntityProperty(
                    EntityReference.Target(0),
                    EntityNumericProperty.ExcessMarkedDamage
                )
            )
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "33"
        artist = "Norikatsu Miyoshi"
        flavorText = "\"There's someone in there! He's bending the vines!\"\n—Sokka"
        imageUri = "https://cards.scryfall.io/normal/front/b/0/b05cfee5-ee59-4df6-a5f6-d9ef0fa7f98a.jpg?1764120111"
    }
}
