package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.IfYouDoEffect
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.effects.ReflexiveTriggerEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Rubble Rouser — Secrets of Strixhaven #128
 * {2}{R} · Creature — Dwarf Sorcerer · 1/4
 *
 * When this creature enters, you may discard a card. If you do, draw a card.
 * {T}, Exile a card from your graveyard: Add {R}. When you do, this creature deals 1 damage to
 * each opponent.
 *
 * The ETB is the standard optional loot: [MayEffect] wrapping an [IfYouDoEffect] whose action is
 * a single discard and whose payoff is a draw.
 *
 * The activated ability is a mana ability ({T} + exile a card from your graveyard) that adds {R}.
 * "When you do" is a reflexive trigger that deals 1 damage to each opponent, modeled with
 * [ReflexiveTriggerEffect] (non-optional action = the mana addition, reflexive effect = the
 * damage). `damageSource = EffectTarget.Self` so the damage is dealt by Rubble Rouser itself, as
 * the text says.
 */
val RubbleRouser = card("Rubble Rouser") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Dwarf Sorcerer"
    power = 1
    toughness = 4
    oracleText = "When this creature enters, you may discard a card. If you do, draw a card.\n" +
        "{T}, Exile a card from your graveyard: Add {R}. When you do, this creature deals 1 " +
        "damage to each opponent."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = MayEffect(
            effect = IfYouDoEffect(
                action = Patterns.Hand.discardCards(1),
                ifYouDo = Effects.DrawCards(1),
            ),
        )
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Tap, Costs.ExileFromGraveyard(1))
        effect = ReflexiveTriggerEffect(
            action = Effects.AddMana(Color.RED),
            optional = false,
            reflexiveEffect = Effects.DealDamage(
                1,
                EffectTarget.PlayerRef(Player.EachOpponent),
                damageSource = EffectTarget.Self,
            ),
            descriptionOverride = "Add {R}. When you do, this creature deals 1 damage to each opponent.",
        )
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "128"
        artist = "Craig J Spearing"
        flavorText = "\"History is patient. I am not.\""
        imageUri = "https://cards.scryfall.io/normal/front/a/f/afe61957-a9bb-42b0-98e8-b5fa418cbaff.jpg?1775937860"
    }
}
