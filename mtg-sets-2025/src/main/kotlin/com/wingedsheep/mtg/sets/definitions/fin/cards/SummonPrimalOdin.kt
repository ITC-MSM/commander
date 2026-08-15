package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.TriggeredAbility
import com.wingedsheep.sdk.scripting.effects.GrantTriggeredAbilityEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Summon: Primal Odin
 * {4}{B}{B}
 * Enchantment Creature — Saga Knight
 * 5/3
 * (As this Saga enters and after your draw step, add a lore counter. Sacrifice after III.)
 * I — Gungnir — Destroy target creature an opponent controls.
 * II — Zantetsuken — This creature gains "Whenever this creature deals combat damage to a player,
 *   that player loses the game."
 * III — Hall of Sorrow — Draw two cards. Each player loses 2 life.
 *
 * Three-chapter Saga (sacrifice after III; derived from the highest declared chapter). Chapter II
 * permanently grants the Saga creature itself the Phage-style "deals combat damage to a player →
 * that player loses the game" trigger via [GrantTriggeredAbilityEffect] (SELF, [Duration.Permanent]),
 * reusing [Triggers.DealsCombatDamageToPlayer] + [Effects.LoseGame] on the damaged player. Chapter
 * III draws two and drains every player for 2 ([Player.Each]).
 */
val SummonPrimalOdin = card("Summon: Primal Odin") {
    manaCost = "{4}{B}{B}"
    colorIdentity = "B"
    typeLine = "Enchantment Creature — Saga Knight"
    oracleText = "(As this Saga enters and after your draw step, add a lore counter. Sacrifice after III.)\n" +
        "I — Gungnir — Destroy target creature an opponent controls.\n" +
        "II — Zantetsuken — This creature gains \"Whenever this creature deals combat damage to a " +
        "player, that player loses the game.\"\n" +
        "III — Hall of Sorrow — Draw two cards. Each player loses 2 life."
    power = 5
    toughness = 3

    // I — Gungnir — Destroy target creature an opponent controls.
    sagaChapter(1) {
        val victim = target("creature", TargetObject(filter = TargetFilter.CreatureOpponentControls))
        effect = Effects.Destroy(victim)
    }

    // II — Zantetsuken — grant this creature the "combat damage to a player → that player loses" trigger.
    sagaChapter(2) {
        effect = GrantTriggeredAbilityEffect(
            ability = TriggeredAbility.create(
                trigger = Triggers.DealsCombatDamageToPlayer.event,
                binding = Triggers.DealsCombatDamageToPlayer.binding,
                effect = Effects.LoseGame(
                    target = EffectTarget.PlayerRef(Player.TriggeringPlayer),
                    message = "Summon: Primal Odin's Zantetsuken dealt combat damage"
                )
            ),
            target = EffectTarget.Self,
            duration = Duration.Permanent
        )
    }

    // III — Hall of Sorrow — Draw two cards. Each player loses 2 life.
    sagaChapter(3) {
        effect = Effects.DrawCards(2) then
            Effects.LoseLife(2, EffectTarget.PlayerRef(Player.Each))
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "121"
        artist = "Nino Is"
        flavorText = "The elder primal Odin has returned to Eorzea."
        imageUri = "https://cards.scryfall.io/normal/front/8/b/8b1b5f06-e34d-44a3-976e-5157c4b7a0f4.jpg?1748706216"
    }
}
