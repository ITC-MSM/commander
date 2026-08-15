package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.effects.CreateDelayedTriggerEffect
import com.wingedsheep.sdk.scripting.effects.DelayedTriggerExpiry
import com.wingedsheep.sdk.scripting.effects.ModifyStatsEffect
import com.wingedsheep.sdk.scripting.effects.TransformEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Zenos yae Galvus // Shinryu, Transcendent Rival (Final Fantasy #127)
 * {3}{B}{B} — Legendary Creature — Human Noble Warrior 4/4
 * //  — Legendary Creature — Dragon 8/8 (Flying)
 *
 * Front — Zenos yae Galvus:
 *   My First Friend — When Zenos yae Galvus enters, choose a creature an opponent controls. Until
 *   end of turn, creatures other than Zenos yae Galvus and the chosen creature get -2/-2.
 *   When the chosen creature leaves the battlefield, transform Zenos yae Galvus.
 *
 * Back — Shinryu, Transcendent Rival:
 *   Flying
 *   As this creature transforms into Shinryu, choose an opponent.
 *   Burning Chains — When the chosen player loses the game, you win the game.
 *
 * Modeling notes:
 *  - "choose a creature an opponent controls" is a non-targeting choice. Per the card's ruling,
 *    when you *can't* choose one (opponents control no creatures) the -2/-2 still resolves for
 *    every other creature. A mandatory target would remove the whole triggered ability from the
 *    stack in that case (CR 603.3c), so we model the choice as an **optional** target: the -2/-2
 *    `ForEachInGroup` runs regardless, and only the delayed transform-watch is created when a
 *    creature was actually chosen. (The one divergence from a true non-targeting choice — the
 *    engine has no "choose one if able" primitive — is that a player could decline while an
 *    opponent creature exists, and hexproof creatures can't be chosen.)
 *  - The -2/-2 is a one-shot [ModifyStatsEffect] applied to `AllCreatures.other().otherThanTarget()`
 *    — every creature except the source (Zenos) and the chosen target — mirroring Infest.
 *  - "When the chosen creature leaves the battlefield, transform Zenos" is a reflexive delayed
 *    trigger scoped to the chosen creature ([Triggers.LeavesBattlefield] + `watchedTarget`). It
 *    must survive across turns until that creature leaves, so it uses [DelayedTriggerExpiry.Never]
 *    with `fireOnce` (the end-of-turn cleanup only removes EndOfTurn triggers). `EffectTarget.Self`
 *    resolves to the delayed trigger's source — Zenos — when it fires.
 *  - The back's win condition is a broad [Triggers.AnyPlayerLosesGame] gated to the chosen player
 *    via `triggerCondition = TriggeringPlayerIs(Player.ChosenOpponent)`; [Effects.ChooseOpponent]
 *    (on [Triggers.TransformsToBack]) stores that opponent, and only an actual transform sets it.
 */

private val ShinryuTranscendentRival = card("Shinryu, Transcendent Rival") {
    manaCost = ""
    colorIdentity = "B"
    typeLine = "Legendary Creature — Dragon"
    oracleText = "Flying\n" +
        "As this creature transforms into Shinryu, choose an opponent.\n" +
        "Burning Chains — When the chosen player loses the game, you win the game."
    power = 8
    toughness = 8

    keywords(Keyword.FLYING)

    // As this creature transforms into Shinryu, choose an opponent.
    triggeredAbility {
        trigger = Triggers.TransformsToBack
        effect = Effects.ChooseOpponent("Choose an opponent")
        description = "As this creature transforms into Shinryu, choose an opponent."
    }

    // Burning Chains — When the chosen player loses the game, you win the game.
    triggeredAbility {
        trigger = Triggers.AnyPlayerLosesGame
        triggerCondition = Conditions.TriggeringPlayerIs(Player.ChosenOpponent)
        effect = Effects.WinGame(
            message = "Shinryu, Transcendent Rival — the chosen player lost the game"
        )
        description = "Burning Chains — When the chosen player loses the game, you win the game."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "127"
        artist = "Alexander Mokhov"
        imageUri = "https://cards.scryfall.io/normal/back/b/6/b65ffce4-bb58-418a-9bad-81533a5f2ba2.jpg?1782686503"
    }
}

private val ZenosYaeGalvusFront = card("Zenos yae Galvus") {
    manaCost = "{3}{B}{B}"
    colorIdentity = "B"
    typeLine = "Legendary Creature — Human Noble Warrior"
    oracleText = "My First Friend — When Zenos yae Galvus enters, choose a creature an opponent " +
        "controls. Until end of turn, creatures other than Zenos yae Galvus and the chosen " +
        "creature get -2/-2.\n" +
        "When the chosen creature leaves the battlefield, transform Zenos yae Galvus."
    power = 4
    toughness = 4

    // My First Friend — When Zenos enters, choose a creature an opponent controls (optional so the
    // rider still resolves when opponents control none). Give every other creature -2/-2 until end
    // of turn, then, if a creature was chosen, set up the transform-watch on it.
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val chosen = target(
            "creature an opponent controls",
            TargetPermanent(filter = TargetFilter.CreatureOpponentControls, optional = true)
        )
        effect = Effects.Composite(
            Effects.ForEachInGroup(
                filter = GroupFilter.AllCreatures.other().otherThanTarget(),
                effect = ModifyStatsEffect(-2, -2, EffectTarget.Self)
            ),
            ConditionalEffect(
                condition = Conditions.EntityMatches(
                    EffectTarget.ContextTarget(0),
                    GameObjectFilter.Creature
                ),
                effect = CreateDelayedTriggerEffect(
                    trigger = Triggers.LeavesBattlefield,
                    watchedTarget = chosen,
                    fireOnce = true,
                    expiry = DelayedTriggerExpiry.Never,
                    effect = TransformEffect(EffectTarget.Self)
                )
            )
        )
        description = "My First Friend — When Zenos yae Galvus enters, choose a creature an " +
            "opponent controls. Until end of turn, creatures other than Zenos yae Galvus and the " +
            "chosen creature get -2/-2. When the chosen creature leaves the battlefield, transform " +
            "Zenos yae Galvus."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "127"
        artist = "Alexander Mokhov"
        imageUri = "https://cards.scryfall.io/normal/front/b/6/b65ffce4-bb58-418a-9bad-81533a5f2ba2.jpg?1782686503"
        ruling(
            "2025-06-06",
            "In the case where you can't choose a creature an opponent controls for Zenos's first " +
                "ability (probably because your opponents control no creatures), other creatures " +
                "will still get -2/-2 until end of turn when Zenos's first ability resolves."
        )
        ruling(
            "2025-06-06",
            "If this card somehow enters the battlefield with its back face up, it didn't " +
                "transform, so you won't choose an opponent."
        )
    }
}

val ZenosYaeGalvus: CardDefinition = CardDefinition.doubleFacedCreature(
    frontFace = ZenosYaeGalvusFront,
    backFace = ShinryuTranscendentRival,
)
