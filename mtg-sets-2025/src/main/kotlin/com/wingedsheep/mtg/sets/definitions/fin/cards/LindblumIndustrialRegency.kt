package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.TriggeredAbility
import com.wingedsheep.sdk.scripting.effects.CreateTokenEffect
import com.wingedsheep.sdk.scripting.effects.DealDamageEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Lindblum, Industrial Regency // Mage Siege
 * Land — Town // Instant — Adventure
 *
 * Lindblum, Industrial Regency:
 *   This land enters tapped.
 *   {T}: Add {R}.
 *
 * Mage Siege — {2}{R}, Instant — Adventure:
 *   Create a 0/1 black Wizard creature token with "Whenever you cast a noncreature spell, this
 *   token deals 1 damage to each opponent."
 *   (Then exile this card. You may play the land later from exile.)
 *
 * Town land // spell Adventure — see [IshgardTheHolySee]. The Adventure half is an *instant*, so it
 * may be cast at instant speed; the token-with-embedded-trigger shape mirrors Cornered by Black Mages.
 */
val LindblumIndustrialRegency = card("Lindblum, Industrial Regency") {
    manaCost = ""
    colorIdentity = "R"
    typeLine = "Land — Town"
    oracleText = "This land enters tapped.\n{T}: Add {R}."

    replacementEffect(EntersTapped())

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.RED)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    adventure("Mage Siege") {
        manaCost = "{2}{R}"
        typeLine = "Instant — Adventure"
        oracleText = "Create a 0/1 black Wizard creature token with \"Whenever you cast a noncreature " +
            "spell, this token deals 1 damage to each opponent.\" " +
            "(Then exile this card. You may play the land later from exile.)"
        spell {
            effect = CreateTokenEffect(
                power = 0,
                toughness = 1,
                colors = setOf(Color.BLACK),
                creatureTypes = setOf("Wizard"),
                imageUri = "https://cards.scryfall.io/normal/front/1/8/187fe54c-7d0c-4225-9d46-3affbead897d.jpg?1782725378",
                triggeredAbilities = listOf(
                    TriggeredAbility.create(
                        trigger = Triggers.YouCastNoncreature.event,
                        binding = Triggers.YouCastNoncreature.binding,
                        effect = DealDamageEffect(1, EffectTarget.PlayerRef(Player.EachOpponent))
                    )
                )
            )
        }
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "285"
        artist = "Piotr Dura"
        imageUri = "https://cards.scryfall.io/normal/front/5/4/548dd152-f0b6-4e8f-9afc-a4ec1671b648.jpg?1748706846"
    }
}
