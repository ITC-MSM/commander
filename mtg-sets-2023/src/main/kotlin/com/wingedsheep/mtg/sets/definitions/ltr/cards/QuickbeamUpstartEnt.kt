package com.wingedsheep.mtg.sets.definitions.ltr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.effects.ForEachTargetEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Quickbeam, Upstart Ent
 * {4}{G}{G}
 * Legendary Creature — Treefolk
 * 5/6
 *
 * Whenever Quickbeam or another Treefolk you control enters, up to two target creatures
 * each get +2/+2 and gain trample until end of turn.
 */
val QuickbeamUpstartEnt = card("Quickbeam, Upstart Ent") {
    manaCost = "{4}{G}{G}"
    colorIdentity = "G"
    typeLine = "Legendary Creature — Treefolk"
    power = 5
    toughness = 6
    oracleText = "Whenever Quickbeam or another Treefolk you control enters, up to two target creatures each get +2/+2 and gain trample until end of turn."

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Creature.youControl().withSubtype("Treefolk"),
            binding = TriggerBinding.ANY
        )
        target = TargetCreature(count = 2, optional = true)
        effect = ForEachTargetEffect(
            effects = listOf(
                Effects.ModifyStats(2, 2, EffectTarget.ContextTarget(0)),
                Effects.GrantKeyword(Keyword.TRAMPLE, EffectTarget.ContextTarget(0))
            )
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "183"
        artist = "Tomas Duchek"
        flavorText = "\"It is only a nickname, of course. They have called me that ever since I said yes to an elder Ent before he had finished his question.\""
        imageUri = "https://cards.scryfall.io/normal/front/b/8/b889037f-b95f-4756-80aa-04097d2818c3.jpg?1686969546"
    }
}
