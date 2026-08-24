package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.effects.ForEachTargetEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Resounding Silence
 * {3}{W}
 * Instant
 * Exile target attacking creature.
 * Cycling {5}{G}{W}{U} ({5}{G}{W}{U}, Discard this card: Draw a card.)
 * When you cycle this card, exile up to two target attacking creatures.
 *
 * The white member of the Alara "Resounding" cycle, composed like the Onslaught cycling cycle: a
 * `spell { }` body, [KeywordAbility.cycling] for the wedge-coloured cycling cost, and a
 * [Triggers.YouCycleThis] triggered ability carrying the larger effect. The spell half is a single
 * [Effects.Exile]; the trigger declares a `count = 2`, `optional = true` [TargetCreature] over
 * [TargetFilter.AttackingCreature] — that pair is exactly "up to two target" — and fans the exile
 * out with [ForEachTargetEffect] so each chosen creature is moved independently.
 */
val ResoundingSilence = card("Resounding Silence") {
    manaCost = "{3}{W}"
    colorIdentity = "GUW"
    typeLine = "Instant"
    oracleText = "Exile target attacking creature.\n" +
        "Cycling {5}{G}{W}{U} ({5}{G}{W}{U}, Discard this card: Draw a card.)\n" +
        "When you cycle this card, exile up to two target attacking creatures."

    spell {
        val t = target("target", Targets.AttackingCreature)
        effect = Effects.Exile(t)
    }

    keywordAbility(KeywordAbility.cycling("{5}{G}{W}{U}"))

    triggeredAbility {
        trigger = Triggers.YouCycleThis
        target(
            "target",
            TargetCreature(count = 2, optional = true, filter = TargetFilter.AttackingCreature)
        )
        effect = ForEachTargetEffect(
            effects = listOf(Effects.Exile(EffectTarget.ContextTarget(0)))
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "22"
        artist = "Mark Zug"
        imageUri = "https://cards.scryfall.io/normal/front/5/b/5b3b0908-5408-41fd-8e69-2bc785e19304.jpg"
    }
}
