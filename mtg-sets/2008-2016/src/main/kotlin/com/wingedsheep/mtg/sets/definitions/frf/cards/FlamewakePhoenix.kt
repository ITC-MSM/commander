package com.wingedsheep.mtg.sets.definitions.frf.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.MustAttack
import com.wingedsheep.sdk.scripting.effects.MayPayManaEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Flamewake Phoenix
 * {1}{R}{R}
 * Creature — Phoenix
 * 2/2
 * Flying, haste
 * This creature attacks each combat if able.
 * Ferocious — At the beginning of combat on your turn, if you control a creature with power 4 or
 * greater, you may pay {R}. If you do, return this card from your graveyard to the battlefield.
 *
 * "Ferocious" is an ability word (no rules meaning); the gate is the intervening-if
 * [Conditions.YouControl] over a power-4+ creature (CR 603.4), checked when the trigger would fire
 * and again at resolution. The trigger fires while the card is in the graveyard
 * (`triggerZone = GRAVEYARD`); the optional {R} is modeled as [MayPayManaEffect] whose consequence
 * moves the card from the graveyard to the battlefield.
 */
val FlamewakePhoenix = card("Flamewake Phoenix") {
    manaCost = "{1}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Phoenix"
    oracleText = "Flying, haste\n" +
        "This creature attacks each combat if able.\n" +
        "Ferocious — At the beginning of combat on your turn, if you control a creature with power " +
        "4 or greater, you may pay {R}. If you do, return this card from your graveyard to the battlefield."
    power = 2
    toughness = 2

    keywords(Keyword.FLYING, Keyword.HASTE)

    staticAbility {
        ability = MustAttack()
    }

    triggeredAbility {
        trigger = Triggers.BeginCombat
        triggerZone = Zone.GRAVEYARD
        triggerCondition = Conditions.YouControl(GameObjectFilter.Creature.powerAtLeast(4))
        effect = MayPayManaEffect(
            cost = ManaCost.parse("{R}"),
            effect = Effects.Move(EffectTarget.Self, Zone.BATTLEFIELD)
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "100"
        artist = "Min Yum"
        imageUri = "https://cards.scryfall.io/normal/front/f/e/fefd5848-9fe1-4129-a5d7-e51606bf76ef.jpg?1783938688"
    }
}
