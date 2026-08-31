package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EventPattern.YouAttackEvent
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.TriggerSpec
import com.wingedsheep.sdk.scripting.conditions.ComparisonOperator
import com.wingedsheep.sdk.scripting.effects.TransformEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Alluring Suitor // Deadly Dancer — Innistrad: Crimson Vow #141
 * {2}{R} · Creature — Vampire 2/3 // Creature — Vampire 3/3
 *
 * Front — Alluring Suitor
 *   When you attack with exactly two creatures, transform this creature.
 *
 * Back — Deadly Dancer
 *   Trample
 *   When this creature transforms into Deadly Dancer, add {R}{R}. Until end of turn, you don't
 *   lose this mana as steps and phases end.
 *   {R}{R}: This creature and another target creature each get +1/+0 until end of turn.
 *
 * Modeling notes:
 *
 *  - **"Exactly two" is the attackers-declared event plus a CR 603.2 restriction, not a new
 *    event.** [YouAttackEvent] carries a `minAttackers` floor only — it is a "two or more" shape —
 *    so the upper bound rides `triggerRestriction`, which CR 603.2 checks *when the trigger would
 *    fire and never again*. That timing is what makes the printed ruling fall out for free: a
 *    creature **put onto the battlefield attacking** never "attacked", and it can only arrive
 *    *after* declare-attackers triggers have been put on the stack, so it is not on the board when
 *    the restriction counts. Using `interveningIf` here would be wrong twice over — it would
 *    re-check on resolution, by which point such a token *is* attacking.
 *  - The count itself is [DynamicAmounts.attackingCreaturesYouControl], the same battlefield tally
 *    Wingmate Roc reads, compared with [ComparisonOperator.EQ].
 *  - **The mana is added by a normal triggered ability, not a mana ability** — it uses the stack
 *    (CR 605.1b: an ability that triggers is never a mana ability), which is exactly what the
 *    printed "When this creature transforms into Deadly Dancer, add {R}{R}" means. The trigger is
 *    [Triggers.TransformsToBack], so it fires on the flip the front face just caused and *not* on
 *    a disturb-style cast onto the back face.
 *  - **"You don't lose this mana as steps and phases end" is its own effect**, not a mana expiry:
 *    [Effects.RetainUnspentMana] tags the controller's red mana as surviving every step/phase
 *    boundary until cleanup. Sequenced after the add so it covers the mana just produced.
 *  - **"This creature and another target creature"** is one target slot, excluding the source —
 *    [TargetFilter.OtherCreature] — plus an untargeted pump of the Dancer itself. Two
 *    [Effects.ModifyStats] rather than a group effect, because only one of the two is a target and
 *    the Dancer's own +1/+0 lands even if the target is removed in response.
 */
private val AlluringSuitorFront = card("Alluring Suitor") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Vampire"
    power = 2
    toughness = 3
    oracleText = "When you attack with exactly two creatures, transform this creature."

    triggeredAbility {
        trigger = TriggerSpec(
            event = YouAttackEvent(minAttackers = 2),
            binding = TriggerBinding.ANY,
        )
        triggerRestriction = Conditions.CompareAmounts(
            DynamicAmounts.attackingCreaturesYouControl(),
            ComparisonOperator.EQ,
            DynamicAmount.Fixed(2),
        )
        effect = TransformEffect(EffectTarget.Self)
        description = "When you attack with exactly two creatures, transform this creature."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "141"
        artist = "Justine Cruz"
        flavorText = "\"May I have this dance?\""
        imageUri = "https://cards.scryfall.io/normal/front/3/9/397ffd01-c090-4233-9f5a-5f765886d498.jpg?1783924853"

        ruling(
            "2021-11-19",
            "A creature put onto the battlefield attacking didn't \"attack\" and won't be counted " +
                "to determine whether Alluring Suitor's ability should trigger."
        )
    }
}

private val DeadlyDancer = card("Deadly Dancer") {
    manaCost = ""
    colorIdentity = "R"
    colorIndicator = "R" // Transformed back face, no mana cost (CR 204).
    typeLine = "Creature — Vampire"
    power = 3
    toughness = 3
    oracleText = "Trample\n" +
        "When this creature transforms into Deadly Dancer, add {R}{R}. Until end of turn, you " +
        "don't lose this mana as steps and phases end.\n" +
        "{R}{R}: This creature and another target creature each get +1/+0 until end of turn."

    keywords(Keyword.TRAMPLE)

    triggeredAbility {
        trigger = Triggers.TransformsToBack
        effect = Effects.Composite(
            Effects.AddMana(Color.RED, 2),
            Effects.RetainUnspentMana(Color.RED),
        )
        description = "When this creature transforms into Deadly Dancer, add {R}{R}. Until end of " +
            "turn, you don't lose this mana as steps and phases end."
    }

    activatedAbility {
        cost = Costs.Mana("{R}{R}")
        val partner = target(
            "another target creature",
            TargetCreature(filter = TargetFilter.OtherCreature)
        )
        effect = Effects.Composite(
            Effects.ModifyStats(1, 0, EffectTarget.Self),
            Effects.ModifyStats(1, 0, partner),
        )
        description = "This creature and another target creature each get +1/+0 until end of turn."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "141"
        artist = "Justine Cruz"
        imageUri = "https://cards.scryfall.io/normal/back/3/9/397ffd01-c090-4233-9f5a-5f765886d498.jpg?1783924853"
    }
}

val AlluringSuitor: CardDefinition = CardDefinition.doubleFacedCreature(
    frontFace = AlluringSuitorFront,
    backFace = DeadlyDancer,
)
