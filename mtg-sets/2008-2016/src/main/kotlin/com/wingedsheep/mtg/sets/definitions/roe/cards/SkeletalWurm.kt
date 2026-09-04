package com.wingedsheep.mtg.sets.definitions.roe.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.RegenerateEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Skeletal Wurm
 * {7}{B}
 * Creature — Skeleton Wurm
 * 7 / 6
 *
 * {B}: Regenerate this creature.
 *
 * Modeling notes:
 *  - A plain mana-only activated ability: [Costs.Mana] for `{B}`, no tap symbol, so it can be
 *    activated repeatedly and at instant speed.
 *  - Regeneration has no `Effects.*` facade — `docs/card-sdk-language-reference.md` documents
 *    `RegenerateEffect(target)` as the raw shape ("raw — no facade"), and every regenerating
 *    creature in the corpus (Unworthy Dead, Sanguine Guard, Spined Fluke, Child of Gaea) constructs
 *    it directly. `EffectTarget.Self` is Assay's `{"type": "Self"}` target: the shield lands on this
 *    creature, not on a chosen one.
 *  - `Effects.CantBeRegenerated` / `Destroy(noRegenerate = true)` are the *opposite* side of this
 *    mechanic and are not involved here.
 */
val SkeletalWurm = card("Skeletal Wurm") {
    manaCost = "{7}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Skeleton Wurm"
    power = 7
    toughness = 6
    oracleText = "{B}: Regenerate this creature."

    activatedAbility {
        cost = Costs.Mana("{B}")
        effect = RegenerateEffect(EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "127"
        artist = "Zoltan Boros & Gabor Szikszai"
        flavorText = "Necromancers are judged by the most powerful undead they've ever created. There are those who have animated just a single being, yet are considered the pinnacle of their dark craft."
        imageUri = "https://cards.scryfall.io/normal/front/2/f/2feedcf2-c443-4363-80cf-a90579a64342.jpg?1783941980"
    }
}
