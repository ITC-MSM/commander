package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Aerid Konstrari — "enters or dies" is two triggers (as on Thawbringer). The activated ability
 * creates the Heartwood first, so X counts the artifacts you control after it enters; X is
 * locked in as the pump resolves.
 */
val AeridKonstrari = card("Aerid Konstrari") {
    manaCost = "{1}{R}{G}{G}"
    colorIdentity = "RG"
    typeLine = "Legendary Creature — Elder Sphinx"
    power = 5
    toughness = 4
    oracleText = "Flying\nWhen Aerid Konstrari enters or dies, create a Heartwood token. (It's a red and green artifact with \"{T}: Add {R} or {G}.\")\n{6}: Create a Heartwood token. Then Aerid Konstrari gets +X/+0 until end of turn, where X is the number of artifacts you control."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateHeartwood()
    }

    triggeredAbility {
        trigger = Triggers.Dies
        effect = Effects.CreateHeartwood()
    }

    activatedAbility {
        cost = Costs.Mana("{6}")
        effect = Effects.Composite(
            Effects.CreateHeartwood(),
            Effects.ModifyStats(
                DynamicAmounts.battlefield(Player.You, GameObjectFilter.Artifact).count(),
                DynamicAmount.Fixed(0),
                EffectTarget.Self,
            ),
        )
        description = "Create a Heartwood token. Then Aerid Konstrari gets +X/+0 until end of turn, where X is the number of artifacts you control."
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "121"
        artist = "Chris Rahn"
        imageUri = "https://cards.scryfall.io/normal/front/f/1/f17d2792-b075-4c47-ad38-e7a7eaee5f8c.jpg?1788878199"
        inBooster = false
    }
}
