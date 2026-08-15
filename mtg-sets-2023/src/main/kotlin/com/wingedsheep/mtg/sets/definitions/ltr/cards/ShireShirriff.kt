package com.wingedsheep.mtg.sets.definitions.ltr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ReflexiveTriggerEffect
import com.wingedsheep.sdk.scripting.effects.SacrificeEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Shire Shirriff
 * {1}{W}
 * Creature — Halfling Soldier
 * 2/2
 *
 * Vigilance
 * When this creature enters, you may sacrifice a token. When you do, exile target creature
 * an opponent controls until this creature leaves the battlefield.
 */
val ShireShirriff = card("Shire Shirriff") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Halfling Soldier"
    power = 2
    toughness = 2
    oracleText = "Vigilance\nWhen this creature enters, you may sacrifice a token. When you do, exile target creature an opponent controls until this creature leaves the battlefield."

    keywords(Keyword.VIGILANCE)

    // ETB: you may sacrifice a token. When you do, exile target creature an opponent controls
    // until this creature leaves the battlefield.
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = ReflexiveTriggerEffect(
            action = SacrificeEffect(filter = GameObjectFilter.Token),
            optional = true,
            reflexiveEffect = Effects.ExileUntilLeaves(EffectTarget.ContextTarget(0)),
            reflexiveTargetRequirements = listOf(Targets.CreatureOpponentControls)
        )
    }

    // When this creature leaves the battlefield, return the exiled card.
    triggeredAbility {
        trigger = Triggers.LeavesBattlefield
        effect = Effects.ReturnLinkedExileUnderOwnersControl()
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "30"
        artist = "Craig J Spearing"
        flavorText = "\"You're arrested for Gate-breaking, and Tearing up of Rules, and Trespassing, and Bribing Guards with Food.\""
        imageUri = "https://cards.scryfall.io/normal/front/4/4/444b687f-2571-4c55-a497-d24b9e18bc0f.jpg?1686967920"
    }
}
