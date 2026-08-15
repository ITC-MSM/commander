package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Sephiroth, Planet's Heir
 * {4}{U}{B}
 * Legendary Creature — Human Avatar Soldier
 * 4/4
 * Vigilance
 * When Sephiroth enters, creatures your opponents control get -2/-2 until end of turn.
 * Whenever a creature an opponent controls dies, put a +1/+1 counter on Sephiroth.
 *
 * The enters debuff applies a per-creature -2/-2 floating effect to every creature an
 * opponent controls at resolution ([Effects.ForEachInGroup] over [GroupFilter], with the
 * iterated creature as [EffectTarget.Self]); creatures that enter later are unaffected, as
 * the affected set is fixed when the ability resolves. The death trigger is a
 * [Triggers.leavesBattlefield] to the graveyard, filtered to opponent-controlled creatures
 * with [TriggerBinding.ANY].
 */
val SephirothPlanetsHeir = card("Sephiroth, Planet's Heir") {
    manaCost = "{4}{U}{B}"
    colorIdentity = "UB"
    typeLine = "Legendary Creature — Human Avatar Soldier"
    power = 4
    toughness = 4
    oracleText = "Vigilance (Attacking doesn't cause this creature to tap.)\n" +
        "When Sephiroth enters, creatures your opponents control get -2/-2 until end of turn.\n" +
        "Whenever a creature an opponent controls dies, put a +1/+1 counter on Sephiroth."

    keywords(Keyword.VIGILANCE)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.ForEachInGroup(
            GroupFilter(GameObjectFilter.Creature.opponentControls()),
            Effects.ModifyStats(power = -2, toughness = -2, target = EffectTarget.Self)
        )
    }

    triggeredAbility {
        trigger = Triggers.leavesBattlefield(
            filter = GameObjectFilter.Creature.opponentControls(),
            to = Zone.GRAVEYARD,
            binding = TriggerBinding.ANY
        )
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "553"
        artist = "Magali Villeneuve"
        flavorText = "\"This is the end... for all of you.\""
        imageUri = "https://cards.scryfall.io/normal/front/a/b/abd73e52-62f0-4e89-9dc6-90ff0bc2a9b7.jpg?1782686131"
    }
}
