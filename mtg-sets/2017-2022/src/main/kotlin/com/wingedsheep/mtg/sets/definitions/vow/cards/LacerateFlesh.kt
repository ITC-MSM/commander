package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.scripting.values.EntityNumericProperty
import com.wingedsheep.sdk.scripting.values.EntityReference

/**
 * Lacerate Flesh
 * {4}{R}
 * Sorcery
 *
 * Lacerate Flesh deals 4 damage to target creature. Create a number of Blood tokens equal to the
 * amount of excess damage dealt to that creature this way.
 *
 * Mirrors Hell to Pay's excess-damage shape, but with a fixed 4 damage instead of X and Blood
 * tokens instead of Treasure: [Effects.DealDamage]`(4)` marks the damage, then
 * [Effects.CreateBlood] reads the post-damage excess via `EntityProperty(Target(0),
 * ExcessMarkedDamage)` — `max(0, marked − toughness)` (CR 120.4a). CompositeEffect resolves its
 * steps sequentially with no interleaved SBA pass, so the marked damage in scope at the second
 * step is exactly the 4 this spell just dealt.
 */
val LacerateFlesh = card("Lacerate Flesh") {
    manaCost = "{4}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Lacerate Flesh deals 4 damage to target creature. Create a number of Blood " +
        "tokens equal to the amount of excess damage dealt to that creature this way."

    spell {
        val creature = target("creature", Targets.Creature)
        effect = Effects.Composite(
            Effects.DealDamage(4, creature),
            Effects.CreateBlood(
                count = DynamicAmount.EntityProperty(
                    EntityReference.Target(0),
                    EntityNumericProperty.ExcessMarkedDamage
                )
            )
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "166"
        artist = "Pauline Voss"
        imageUri = "https://cards.scryfall.io/normal/front/c/7/c7e0c0dc-2d35-4e5a-81da-dd5f35b8e579.jpg?1783924831"
    }
}
