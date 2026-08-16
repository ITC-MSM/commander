package com.wingedsheep.mtg.sets.definitions.zen.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Baloth Woodcrasher
 * {4}{G}{G}
 * Creature — Beast
 * 4/4
 * Landfall — Whenever a land you control enters, this creature gets +4/+4 and gains trample until end of turn. (It can deal excess combat damage to the player or planeswalker it's attacking.)
 *
 * Landfall is the standard [Triggers.entersBattlefield] over `GameObjectFilter.Land.youControl()`
 * with [TriggerBinding.ANY]; the effect is a [Effects.Composite] of an until-end-of-turn
 * [Effects.ModifyStats] and [Effects.GrantKeyword](TRAMPLE), both on [EffectTarget.Self].
 */
val BalothWoodcrasher = card("Baloth Woodcrasher") {
    manaCost = "{4}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Beast"
    power = 4
    toughness = 4
    oracleText = "Landfall — Whenever a land you control enters, this creature gets +4/+4 and gains trample until end of turn. (It can deal excess combat damage to the player or planeswalker it's attacking.)"

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Land.youControl(),
            binding = TriggerBinding.ANY
        )
        effect = Effects.Composite(
            Effects.ModifyStats(4, 4, EffectTarget.Self),
            Effects.GrantKeyword(Keyword.TRAMPLE, EffectTarget.Self)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "157"
        artist = "Zoltan Boros & Gabor Szikszai"
        flavorText = "Its insatiable hunger quickly depletes a region of prey. It must migrate from place to place to feed its massive bulk."
        imageUri = "https://cards.scryfall.io/normal/front/8/2/8223dc6a-2bee-4be9-86d5-f0a17a24c33e.jpg?1783942137"
        ruling("2024-11-08", "A landfall ability triggers whenever a land you control enters for any reason. It triggers whenever you play a land, as well as whenever a spell or ability puts a land onto the battlefield under your control.")
        ruling("2024-11-08", "A landfall ability doesn't trigger if a permanent already on the battlefield becomes a land.")
        ruling("2024-11-08", "Whenever a land you control enters, each landfall ability of the permanents you control will trigger. You can put them on the stack in any order. The last ability you put on the stack will be the first one to resolve (As a result, you can have those abilities resolve in the order of your choosing.).")
    }
}
