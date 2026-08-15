package com.wingedsheep.mtg.sets.definitions.blb.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.GrantTriggeredAbility
import com.wingedsheep.sdk.scripting.TriggeredAbility
import com.wingedsheep.sdk.scripting.effects.GrantKeywordEffect
import com.wingedsheep.sdk.scripting.effects.ModifyStatsEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Bria, Riptide Rogue
 * {2}{U}{R}
 * Legendary Creature — Otter Rogue
 * 3/3
 * Prowess (Whenever you cast a noncreature spell, this creature gets +1/+1 until end of turn.)
 * Other creatures you control have prowess.
 * Whenever you cast a noncreature spell, target creature you control can't be blocked this turn.
 *
 * Prowess is a keyword that bundles an intrinsic triggered ability (+1/+1 on noncreature cast).
 * The engine derives that behavior from the explicit triggered ability, NOT from the keyword tag
 * alone, so "Other creatures you control have prowess" is modeled by granting both the prowess
 * keyword (display) and the prowess triggered ability (behavior) to the group via
 * [GrantTriggeredAbility]. Each granted copy is a separate instance, so a creature that already
 * has prowess and gets Bria's grant triggers twice — matching the reminder text.
 */
val BriaRiptideRogue = card("Bria, Riptide Rogue") {
    manaCost = "{2}{U}{R}"
    colorIdentity = "UR"
    typeLine = "Legendary Creature — Otter Rogue"
    power = 3
    toughness = 3
    oracleText = "Prowess (Whenever you cast a noncreature spell, this creature gets +1/+1 until end of turn.)\n" +
        "Other creatures you control have prowess. (If a creature has multiple instances of prowess, each triggers separately.)\n" +
        "Whenever you cast a noncreature spell, target creature you control can't be blocked this turn."

    // Bria's own prowess: keyword + intrinsic +1/+1 triggered ability.
    prowess()

    // "Other creatures you control have prowess." — grant the keyword (display) and the
    // intrinsic prowess triggered ability (behavior) to the rest of your board.
    val otherCreatures = GroupFilter(GameObjectFilter.Creature.youControl(), excludeSelf = true)
    staticAbility {
        ability = GrantKeyword(Keyword.PROWESS, otherCreatures)
    }
    staticAbility {
        ability = GrantTriggeredAbility(
            ability = TriggeredAbility.create(
                trigger = Triggers.YouCastNoncreature.event,
                binding = Triggers.YouCastNoncreature.binding,
                effect = ModifyStatsEffect(
                    powerModifier = 1,
                    toughnessModifier = 1,
                    target = EffectTarget.Self
                )
            ),
            filter = otherCreatures
        )
    }

    // "Whenever you cast a noncreature spell, target creature you control can't be blocked this turn."
    triggeredAbility {
        trigger = Triggers.YouCastNoncreature
        val t = target("target", Targets.CreatureYouControl)
        effect = GrantKeywordEffect(AbilityFlag.CANT_BE_BLOCKED.name, t)
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "379"
        artist = "Borja Pindado"
        imageUri = "https://cards.scryfall.io/normal/front/3/9/390c96b3-68da-4a42-89ab-d9ccc79ce0dd.jpg?1724104634"
    }
}
