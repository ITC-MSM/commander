package com.wingedsheep.mtg.sets.definitions.msh.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Baron Strucker, HYDRA Overlord — Marvel Super Heroes #88 (uncommon)
 * {2}{B} · Legendary Creature — Human Villain · 2/2
 *
 * Villain spells you cast cost {1} less to cast.
 * Whenever another Villain you control enters, you may have it connive. Do this only once each
 * turn.
 *
 * The discount is the Tombstone, Career Criminal / Undead Warchief shape: a [ModifySpellCost] static
 * scoped to [SpellCostTarget.YouCast] over the Villain subtype, reducing generic mana by one.
 *
 * The connive trigger is where the interesting rule lives. **"Do this only once each turn" is an
 * *effect* cap, not a trigger cap** — `effectOncePerTurn = true`, never `oncePerTurn = true`. Per CR
 * 603.2 the ability triggers once per matching event, so two Villains entering together each put an
 * instance on the stack and the controller decides *which* one connives by declining the other. The
 * trigger cap would fire only for the first Villain and take that choice away. Declining the "you
 * may" does not spend the budget: the engine lowers the flag into a
 * [com.wingedsheep.sdk.scripting.effects.Gate.OnceEachTurn] gate placed inside the [MayEffect]'s
 * consent gate, so only an application that actually happens counts.
 *
 * "Another **Villain** you control" is deliberately `GameObjectFilter.Any` — the set puts the Villain
 * subtype on non-creature permanents too (see Avengers Under Siege) — with [TriggerBinding.OTHER]
 * supplying the "another" (Baron Strucker is himself a Villain). "Have **it** connive" is
 * [Effects.Connive] on [EffectTarget.TriggeringEntity], the permanent that entered.
 */
val BaronStruckerHydraOverlord = card("Baron Strucker, HYDRA Overlord") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Legendary Creature — Human Villain"
    power = 2
    toughness = 2
    oracleText = "Villain spells you cast cost {1} less to cast.\n" +
        "Whenever another Villain you control enters, you may have it connive. Do this only once " +
        "each turn. (Draw a card, then discard a card. If you discarded a nonland card, put a " +
        "+1/+1 counter on that creature.)"

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.YouCast(GameObjectFilter.Any.withSubtype(Subtype.VILLAIN)),
            modification = CostModification.ReduceGeneric(1),
        )
    }

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Any.withSubtype(Subtype.VILLAIN).youControl(),
            binding = TriggerBinding.OTHER,
        )
        effect = MayEffect(Effects.Connive(EffectTarget.TriggeringEntity))
        effectOncePerTurn = true
        description = "Whenever another Villain you control enters, you may have it connive. " +
            "Do this only once each turn."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "88"
        artist = "InHyuk Lee"
        flavorText = "\"Minions of HYDRA! Behold! Our ultimate destiny is at hand!\""
        imageUri = "https://cards.scryfall.io/normal/front/e/a/eaf2251a-ffaf-4055-9474-7e3d08d89609.jpg?1783902947"
    }
}
