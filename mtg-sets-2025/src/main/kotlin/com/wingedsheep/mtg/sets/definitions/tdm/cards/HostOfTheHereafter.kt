package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersWithCounters
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Host of the Hereafter — Tarkir: Dragonstorm #193
 * {2}{B}{G} · Creature — Zombie Warlock · 2/2
 *
 * This creature enters with two +1/+1 counters on it.
 * Whenever this creature or another creature you control dies, if it had counters on it,
 * put its counters on up to one target creature you control.
 *
 * "This creature or another creature you control dies" is `Triggers.YourCreatureDies`
 * (ANY binding, creatures-you-control filter — which includes Host itself). The intervening
 * "if it had counters on it" (CR 603.4) is `Conditions.TriggeringEntityHadCounters`, reading
 * the dying creature's last-known total counter count. `Effects.MoveAllLastKnownCounters`
 * moves *every* counter kind from the dying creature (not just +1/+1) onto the chosen target,
 * which is optional ("up to one").
 */
val HostOfTheHereafter = card("Host of the Hereafter") {
    manaCost = "{2}{B}{G}"
    colorIdentity = "BG"
    typeLine = "Creature — Zombie Warlock"
    power = 2
    toughness = 2
    oracleText = "This creature enters with two +1/+1 counters on it.\n" +
        "Whenever this creature or another creature you control dies, if it had counters on it, " +
        "put its counters on up to one target creature you control."

    replacementEffect(EntersWithCounters(count = 2, selfOnly = true))

    triggeredAbility {
        trigger = Triggers.YourCreatureDies
        triggerCondition = Conditions.TriggeringEntityHadCounters
        target = TargetCreature(
            optional = true,
            filter = TargetFilter(GameObjectFilter.Creature.youControl())
        )
        effect = Effects.MoveAllLastKnownCounters(EffectTarget.ContextTarget(0))
        description = "Whenever this creature or another creature you control dies, if it had " +
            "counters on it, put its counters on up to one target creature you control."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "193"
        artist = "Annie Stegg"
        imageUri = "https://cards.scryfall.io/normal/front/0/f/0f182957-8133-45a7-80a3-1944bead4d43.jpg?1743204755"
    }
}
