package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Gustrider Exuberant
 * {2}{W}
 * Creature — Human Wizard
 * 1 / 2
 * Flying
 * Sacrifice this creature: Creatures you control with power 5 or greater gain flying until end of turn.
 *
 * The cost is the bare [Costs.SacrificeSelf] atom — no mana, no tap — so the ability is usable at
 * instant speed even the turn it lands. The grant is [Effects.ForEachInGroup] over a snapshot of
 * `GameObjectFilter.Creature.youControl().powerAtLeast(5)`, with [EffectTarget.Self] inside the body
 * bound to the current iteration entity; each iteration is a separate
 * [Effects.GrantKeyword] whose default `Duration.EndOfTurn` is the printed duration. Snapshotting
 * before iteration is what makes the grant a one-shot on the creatures present at resolution rather
 * than a continuous "creatures with power 5 or greater have flying".
 */
val GustriderExuberant = card("Gustrider Exuberant") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Wizard"
    power = 1
    toughness = 2
    oracleText = "Flying\n" +
        "Sacrifice this creature: Creatures you control with power 5 or greater gain flying until end of turn."

    keywords(Keyword.FLYING)

    activatedAbility {
        cost = Costs.SacrificeSelf
        effect = Effects.ForEachInGroup(
            filter = GroupFilter(GameObjectFilter.Creature.youControl().powerAtLeast(5)),
            effect = Effects.GrantKeyword(Keyword.FLYING, EffectTarget.Self)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "13"
        artist = "Wayne Reynolds"
        flavorText = "\"The elves claim the canopy. The nacatl claim the mountains. I suppose you think we ought to stay on the jungle floor?\""
        imageUri = "https://cards.scryfall.io/normal/front/5/3/5343e6f2-7db7-4731-8e1b-70bf74316a79.jpg"
    }
}
