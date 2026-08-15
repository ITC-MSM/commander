package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.effects.AddCountersEffect
import com.wingedsheep.sdk.scripting.effects.GrantKeywordEffect
import com.wingedsheep.sdk.scripting.effects.ModalEffect
import com.wingedsheep.sdk.scripting.effects.Mode
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import com.wingedsheep.sdk.scripting.targets.TargetPlayer

/**
 * Practiced Offense — Secrets of Strixhaven #25
 * {2}{W} · Sorcery
 *
 * Put a +1/+1 counter on each creature target player controls. Target creature gains your choice
 * of double strike or lifelink until end of turn.
 * Flashback {1}{W} (You may cast this card from your graveyard for its flashback cost. Then exile
 * it.)
 *
 * Two independent targets, declared as named bindings: a target player and a target creature.
 * The counter is placed on every creature the *target player* controls via [ForEachInGroup] over
 * a [GroupFilter] scoped with `targetPlayerControls(player)` (the preferred explicit-reference form
 * over implicit first-player resolution), applying [AddCountersEffect] with [EffectTarget.Self]
 * (the current group member). The "your choice of double strike or lifelink" is a within-resolution
 * choice (not a modal spell), so [ModalEffect.chooseOne] is built with `countsAsModalSpell = false`;
 * the chosen keyword is granted to the target creature until end of turn.
 *
 * Flashback {1}{W} via [KeywordAbility.flashback].
 */
val PracticedOffense = card("Practiced Offense") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Sorcery"
    oracleText = "Put a +1/+1 counter on each creature target player controls. Target creature " +
        "gains your choice of double strike or lifelink until end of turn.\n" +
        "Flashback {1}{W} (You may cast this card from your graveyard for its flashback cost. " +
        "Then exile it.)"

    spell {
        val player = target("player", TargetPlayer())
        val creature = target("creature", TargetCreature())
        effect = Effects.Composite(
            // Put a +1/+1 counter on each creature the target player controls.
            Effects.ForEachInGroup(
                filter = GroupFilter(GameObjectFilter.Creature.targetPlayerControls(player)),
                effect = AddCountersEffect(
                    counterType = Counters.PLUS_ONE_PLUS_ONE,
                    count = 1,
                    target = EffectTarget.Self,
                ),
            ),
            // Target creature gains your choice of double strike or lifelink until end of turn.
            ModalEffect.chooseOne(
                Mode.noTarget(
                    GrantKeywordEffect(Keyword.DOUBLE_STRIKE, creature, Duration.EndOfTurn),
                    "Double strike",
                ),
                Mode.noTarget(
                    GrantKeywordEffect(Keyword.LIFELINK, creature, Duration.EndOfTurn),
                    "Lifelink",
                ),
                countsAsModalSpell = false,
            ),
        )
    }

    keywordAbility(KeywordAbility.flashback("{1}{W}"))

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "25"
        artist = "Raluca Marinescu"
        imageUri = "https://cards.scryfall.io/normal/front/7/9/79c7cf94-c0a1-432d-90d7-7f0599c2e7a8.jpg?1775937087"
    }
}
