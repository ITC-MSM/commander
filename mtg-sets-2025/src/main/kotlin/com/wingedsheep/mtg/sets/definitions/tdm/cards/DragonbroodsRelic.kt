package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.TriggeredAbility
import com.wingedsheep.sdk.scripting.effects.CreateTokenEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Dragonbroods' Relic
 * {1}{G}
 * Artifact
 *
 * {T}, Tap an untapped creature you control: Add one mana of any color.
 * {3}{W}{U}{B}{R}{G}, Sacrifice this artifact: Create a 4/4 Dragon creature token named
 * Reliquary Dragon that's all colors. It has flying, lifelink, and "When this token enters, it
 * deals 3 damage to any target." Activate only as a sorcery.
 *
 * The mana ability taps both this artifact ({T}) and an untapped creature you control
 * ([Costs.TapPermanents] count = 1) as its cost, then adds one mana of any color. The Dragon
 * token is created all-colors (all five [Color] values) with flying + lifelink and an
 * any-target ETB-damage [TriggeredAbility].
 */
val DragonbroodsRelic = card("Dragonbroods' Relic") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Artifact"
    oracleText = "{T}, Tap an untapped creature you control: Add one mana of any color.\n" +
        "{3}{W}{U}{B}{R}{G}, Sacrifice this artifact: Create a 4/4 Dragon creature token named " +
        "Reliquary Dragon that's all colors. It has flying, lifelink, and \"When this token " +
        "enters, it deals 3 damage to any target.\" Activate only as a sorcery."

    // {T}, Tap an untapped creature you control: Add one mana of any color.
    activatedAbility {
        cost = Costs.Composite(
            Costs.Tap,
            Costs.TapPermanents(count = 1, filter = GameObjectFilter.Creature.youControl())
        )
        effect = Effects.AddAnyColorMana()
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    // {3}{W}{U}{B}{R}{G}, Sacrifice this artifact: Create the Reliquary Dragon token.
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{3}{W}{U}{B}{R}{G}"), Costs.SacrificeSelf)
        timing = TimingRule.SorcerySpeed
        effect = CreateTokenEffect(
            power = 4,
            toughness = 4,
            colors = setOf(Color.WHITE, Color.BLUE, Color.BLACK, Color.RED, Color.GREEN),
            creatureTypes = setOf("Dragon"),
            keywords = setOf(Keyword.FLYING, Keyword.LIFELINK),
            name = "Reliquary Dragon",
            triggeredAbilities = listOf(
                TriggeredAbility.create(
                    trigger = Triggers.EntersBattlefield.event,
                    binding = Triggers.EntersBattlefield.binding,
                    effect = Effects.DealDamage(3, EffectTarget.ContextTarget(0)),
                    targetRequirement = Targets.Any,
                    descriptionOverride = "When this token enters, it deals 3 damage to any target."
                )
            ),
            imageUri = "https://cards.scryfall.io/normal/front/4/4/44465924-8cc2-49a4-bc07-8dbae7570af6.jpg?1743176691"
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "140"
        artist = "Racrufi"
        imageUri = "https://cards.scryfall.io/normal/front/3/d/3d634087-77ba-4543-aa7a-8a3774d69cd7.jpg?1743204527"
    }
}
