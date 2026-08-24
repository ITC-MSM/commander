package com.wingedsheep.mtg.sets.definitions.ice.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.RegenerateEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Kjeldoran Dead
 * {B}
 * Creature — Skeleton
 * 3/1
 *
 * When this creature enters, sacrifice a creature.
 * {B}: Regenerate this creature.
 *
 * The enters-trigger is the bare imperative "sacrifice a creature" — [Effects.SacrificeOwn], the
 * spelling with no player named, so the ability's controller chooses (CR 701.17a); it is an effect,
 * not an additional cost, and the printed line says "a creature", not "another", so this permanent
 * is itself a legal choice. The regeneration is the same [RegenerateEffect] shape as its functional
 * twin Spined Fluke.
 */
val KjeldoranDead = card("Kjeldoran Dead") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Creature — Skeleton"
    power = 3
    toughness = 1
    oracleText = "When this creature enters, sacrifice a creature.\n" +
        "{B}: Regenerate this creature."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.SacrificeOwn(GameObjectFilter.Creature)
    }

    activatedAbility {
        cost = Costs.Mana("{B}")
        effect = RegenerateEffect(EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "137"
        artist = "Melissa A. Benson"
        flavorText = "\"They shall kill those whom once they loved.\"\n—Lim-Dûl, the Necromancer"
        imageUri = "https://cards.scryfall.io/normal/front/d/3/d3f7b614-6075-4b7c-acc7-ab63185b570b.jpg"
    }
}
