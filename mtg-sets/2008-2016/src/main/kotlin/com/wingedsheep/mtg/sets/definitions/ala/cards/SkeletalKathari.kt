package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.RegenerateEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Skeletal Kathari
 * {4}{B}
 * Creature — Bird Skeleton
 * 3 / 2
 * Flying
 * {B}, Sacrifice a creature: Regenerate this creature.
 *
 * Flying is a printed [Keyword]. The regeneration ability pairs a [Costs.Mana] atom with a
 * [Costs.Sacrifice] over [GameObjectFilter.Creature] — "a creature", not "another", so the
 * Kathari may eat itself — and the effect is [RegenerateEffect] on [EffectTarget.Self], which has
 * no `Effects` facade entry and so is imported directly.
 */
val SkeletalKathari = card("Skeletal Kathari") {
    manaCost = "{4}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Bird Skeleton"
    power = 3
    toughness = 2
    oracleText = "Flying\n" +
        "{B}, Sacrifice a creature: Regenerate this creature."

    keywords(Keyword.FLYING)

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{B}"),
            Costs.Sacrifice(GameObjectFilter.Creature)
        )
        effect = RegenerateEffect(EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "88"
        artist = "Carl Critchlow"
        flavorText = "Undeath doesn't end the kathari's search for carrion; it only removes one corpse from the Dregscape."
        imageUri = "https://cards.scryfall.io/normal/front/a/3/a3e7e593-e5b0-4348-b8e5-20173d41aa39.jpg"
    }
}
