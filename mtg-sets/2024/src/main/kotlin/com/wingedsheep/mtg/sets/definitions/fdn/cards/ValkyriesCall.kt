package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Valkyrie's Call
 * {3}{W}{W}
 * Enchantment
 *
 * Whenever a nontoken, non-Angel creature you control dies, return that card to the
 * battlefield under its owner's control with a +1/+1 counter on it. It has flying and
 * is an Angel in addition to its other types.
 *
 * Modeled on the Vraska, the Silencer template: a filtered leaves-the-battlefield trigger
 * ([Triggers.leavesBattlefield] to [Zone.GRAVEYARD], ANY binding) whose filter restricts to
 * nontoken, non-Angel creatures the controller controls. [EffectTarget.TriggeringEntity] is
 * the dying card, now in the graveyard; [Effects.Move] returns it to the battlefield (under
 * its owner's control — the reanimation default), then a +1/+1 counter and two
 * [Duration.Permanent] continuous grants (Angel creature type + flying) apply to that
 * returned object for as long as it remains on the battlefield (CR 611.2b — the effect has
 * no duration and modifies the object created by the resolution).
 */
val ValkyriesCall = card("Valkyrie's Call") {
    manaCost = "{3}{W}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment"
    oracleText = "Whenever a nontoken, non-Angel creature you control dies, return that card to " +
        "the battlefield under its owner's control with a +1/+1 counter on it. It has flying and is " +
        "an Angel in addition to its other types."

    triggeredAbility {
        trigger = Triggers.leavesBattlefield(
            filter = GameObjectFilter.Creature.nontoken().youControl().notSubtype(Subtype.ANGEL),
            to = Zone.GRAVEYARD,
            binding = TriggerBinding.ANY
        )
        effect = Effects.Composite(
            // Return that card to the battlefield under its owner's control.
            Effects.Move(EffectTarget.TriggeringEntity, Zone.BATTLEFIELD),
            // ... with a +1/+1 counter on it.
            Effects.AddCounters("+1/+1", 1, EffectTarget.TriggeringEntity),
            // It has flying and is an Angel in addition to its other types.
            Effects.AddCreatureType("Angel", EffectTarget.TriggeringEntity, Duration.Permanent),
            Effects.GrantKeyword(Keyword.FLYING, EffectTarget.TriggeringEntity, Duration.Permanent)
        )
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "27"
        artist = "Scott Murphy"
        flavorText = "\"Welcome, worthy one.\""
        imageUri = "https://cards.scryfall.io/normal/front/0/e/0e1f1ff2-fa8f-4d38-b631-2d6e08e614c8.jpg?1782689241"
    }
}
