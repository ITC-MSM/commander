package com.wingedsheep.mtg.sets.definitions.dtk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.RegenerateEffect

/**
 * Butcher's Glee
 * {2}{B}
 * Instant
 *
 * Target creature gets +3/+0 and gains lifelink until end of turn. Regenerate it. (Damage dealt by a creature with lifelink also causes its controller to gain that much life.)
 *
 * Two printed sentences, so two elements at the top level: the pump-and-lifelink sentence is one
 * nested [Effects.Composite] and "Regenerate it" is its sibling. Chaining with `then` would splice
 * the inner composite's members into a flat three-element list and lose that arity. There is no
 * `Effects.Regenerate` facade — [RegenerateEffect] is the shipped spelling (Death Ward, Reknit) —
 * and "it" is the creature just pumped, so every effect points at the same bound handle.
 */
val ButchersGlee = card("Butcher's Glee") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "Target creature gets +3/+0 and gains lifelink until end of turn. Regenerate it. (Damage dealt by a creature with lifelink also causes its controller to gain that much life.)"

    spell {
        val t = target("target", Targets.Creature)
        effect = Effects.Composite(
            Effects.Composite(
                Effects.ModifyStats(3, 0, t),
                Effects.GrantKeyword(Keyword.LIFELINK, t)
            ),
            RegenerateEffect(t)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "90"
        artist = "Jesper Ejsing"
        flavorText = "The Crave made Kneecleaver think she was bigger than the dragon."
        imageUri = "https://cards.scryfall.io/normal/front/b/4/b4e7919e-6190-4f3c-99f7-faa666d34f79.jpg?1783938601"
    }
}
