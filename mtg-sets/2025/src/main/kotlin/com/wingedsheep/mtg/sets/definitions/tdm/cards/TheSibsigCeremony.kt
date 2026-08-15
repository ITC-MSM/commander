package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * The Sibsig Ceremony
 * {B}{B}{B}
 * Legendary Enchantment
 *
 * Creature spells you cast cost {2} less to cast.
 * Whenever a creature you control enters, if you cast it, destroy that creature, then create a
 * 2/2 black Zombie Druid creature token.
 *
 * The cost reduction is a [ModifySpellCost] static targeting creature spells you cast. The ETB
 * trigger uses an ANY binding over creatures you control (the enchantment itself isn't a
 * creature, so it never self-triggers). The "if you cast it" intervening-if refers to the
 * entering creature, so it is gated with [Conditions.TriggeringEntityWasCast] — the cast-subject
 * version of "if you cast it" (a plain `WasCast` would test the enchantment, not the creature).
 * On resolution the entering creature is destroyed ([EffectTarget.TriggeringEntity]) and a 2/2
 * black Zombie Druid token is created.
 */
val TheSibsigCeremony = card("The Sibsig Ceremony") {
    manaCost = "{B}{B}{B}"
    colorIdentity = "B"
    typeLine = "Legendary Enchantment"
    oracleText = "Creature spells you cast cost {2} less to cast.\n" +
        "Whenever a creature you control enters, if you cast it, destroy that creature, then " +
        "create a 2/2 black Zombie Druid creature token."

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.YouCast(GameObjectFilter.Creature),
            modification = CostModification.ReduceGeneric(2)
        )
    }

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Creature.youControl(),
            binding = TriggerBinding.ANY
        )
        triggerCondition = Conditions.TriggeringEntityWasCast
        effect = Effects.Destroy(EffectTarget.TriggeringEntity)
            .then(
                Effects.CreateToken(
                    power = 2,
                    toughness = 2,
                    colors = setOf(Color.BLACK),
                    creatureTypes = setOf("Zombie", "Druid"),
                    imageUri = "https://cards.scryfall.io/normal/front/f/1/f10d5813-7818-43e8-b08d-4ed8c54d0366.jpg?1748452772"
                )
            )
        description = "Whenever a creature you control enters, if you cast it, destroy that " +
            "creature, then create a 2/2 black Zombie Druid creature token."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "91"
        artist = "Eli Minaya"
        flavorText = "\"Once flesh, now gold and jade. Once dead, now alive again. Once " +
            "respected, now honored beyond imagination.\""
        imageUri = "https://cards.scryfall.io/normal/front/5/a/5a9f2a62-1c61-4d2e-86d9-18cd84c31748.jpg?1743204325"
    }
}
