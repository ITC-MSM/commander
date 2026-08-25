package com.wingedsheep.mtg.sets.definitions.rna.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Territorial Boar — Ravnica Allegiance #145
 * {1}{G} · Creature — Boar · 2 / 2
 *
 * [TriggerBinding.ANY] rather than `OTHER`: the printed line has no "another", so a 4-power
 * Boar-pumping creature that *is* the Boar would still count — it just never is, since the Boar
 * is a 2/2. The power test reads projected state, so a creature that enters as a 2/2 and is
 * immediately made 4/4 by a replacement does trigger it.
 */
val TerritorialBoar = card("Territorial Boar") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Boar"
    power = 2
    toughness = 2
    oracleText = "Whenever a creature you control with power 4 or greater enters, this creature gets +1/+1 and gains vigilance until end of turn."

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Creature.powerAtLeast(4).youControl(),
            binding = TriggerBinding.ANY
        )
        effect = Effects.Composite(listOf(
            Effects.ModifyStats(1, 1, EffectTarget.Self),
            Effects.GrantKeyword(Keyword.VIGILANCE, EffectTarget.Self)
        ))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "145"
        artist = "Dan Murayama Scott"
        flavorText = "\"The presence of the strong will make you stronger.\"\n" +
        "—Yeva, Nature's Herald"
        imageUri = "https://cards.scryfall.io/normal/front/5/e/5e9ddae7-7e7c-46c7-ad7d-9a686c256b9d.jpg"
    }
}
