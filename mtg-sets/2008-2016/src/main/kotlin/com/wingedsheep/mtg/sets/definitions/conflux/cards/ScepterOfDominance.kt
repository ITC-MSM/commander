package com.wingedsheep.mtg.sets.definitions.conflux.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Scepter of Dominance
 * {1}{W}{W}
 * Artifact
 * {W}, {T}: Tap target permanent.
 *
 * One activated ability: a mana-plus-tap [Costs.Composite] and [Effects.Tap] over the widest
 * target the SDK ships, [Targets.Permanent] — the printed noun is "permanent", not "creature",
 * so the requirement carries `GameObjectFilter.Permanent` and nothing narrower.
 */
val ScepterOfDominance = card("Scepter of Dominance") {
    manaCost = "{1}{W}{W}"
    colorIdentity = "W"
    typeLine = "Artifact"
    oracleText = "{W}, {T}: Tap target permanent."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{W}"), Costs.Tap)
        val victim = target("target", Targets.Permanent)
        effect = Effects.Tap(victim)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "17"
        artist = "Howard Lyon"
        flavorText = "\"Whether or not you will bow to me is not open to debate. The question is, will I ever let you rise?\" —Fridius, telemin master"
        imageUri = "https://cards.scryfall.io/normal/front/8/8/888bc7ca-f9fa-4da4-b466-b9dc273d5319.jpg"
    }
}
