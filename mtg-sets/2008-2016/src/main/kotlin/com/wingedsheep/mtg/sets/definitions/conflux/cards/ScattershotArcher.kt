package com.wingedsheep.mtg.sets.definitions.conflux.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Scattershot Archer — Conflux #90
 * {G} · Creature — Elf Archer · 1/2
 *
 * {T}: This creature deals 1 damage to each creature with flying.
 *
 * "Each creature with flying" is untargeted, so it is [Effects.ForEachInGroup] over
 * `Filters.Group.allCreatures.withKeyword(Keyword.FLYING)` rather than a target requirement —
 * `IterationSpace.Group` binds each flier in turn as the body's [EffectTarget.Self], so a single
 * one-damage facade applies once per flier. The group is snapshotted before the first iteration,
 * and each flier takes damage from this creature independently (so lifelink, deathtouch, and
 * protection from green all read the archer as the source).
 */
val ScattershotArcher = card("Scattershot Archer") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf Archer"
    power = 1
    toughness = 2
    oracleText = "{T}: This creature deals 1 damage to each creature with flying."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.ForEachInGroup(
            filter = Filters.Group.allCreatures.withKeyword(Keyword.FLYING),
            effect = Effects.DealDamage(1, EffectTarget.Self)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "90"
        artist = "Steve Argyle"
        flavorText = "To train her elves for war, Mayael would drop a sackful of acorns from the tree canopy. Each archer tried to split as many as possible before the acorns hit the forest floor below."
        imageUri = "https://cards.scryfall.io/normal/front/d/7/d74d6cdb-a087-4b3d-bf25-509200ed6d93.jpg"
    }
}
