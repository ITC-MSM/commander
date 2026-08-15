package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.effects.GiveControlToTargetPlayerEffect
import com.wingedsheep.sdk.scripting.effects.IfYouDoEffect
import com.wingedsheep.sdk.scripting.effects.SuccessCriterion
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.ContextPropertyKey
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Kain, Traitorous Dragoon
 * {2}{B}
 * Legendary Creature — Human Knight
 * 2/4
 *
 * Jump — During your turn, Kain has flying.
 * Whenever Kain deals combat damage to a player, that player gains control of Kain. If they do,
 * you draw that many cards, create that many tapped Treasure tokens, then lose that much life.
 *
 * "Jump" is modelled as a conditional static ability (Rule 604): while it's your turn, Kain grants
 * itself flying via [GroupFilter.source]. The rest of the time it has no evasion, so it can be blocked.
 *
 * The combat-damage trigger hands Kain to the damaged player. "That player" is
 * [Player.TriggeringPlayer], resolved through the existing [GiveControlToTargetPlayerEffect]
 * (newController = the triggering player, permanent = Kain itself). "If they do" is gated on the
 * control actually moving via [SuccessCriterion.ControlChanged] — so if a "can't gain control"
 * effect or Kain leaving the battlefield stops the hand-off, the rider is withheld, per the printed
 * text. The three riders all scale off the combat damage dealt ("that many/much"), read from
 * [ContextPropertyKey.TRIGGER_DAMAGE_AMOUNT]. They resolve for *you* — the ability's controller,
 * fixed when it triggered — not the new controller, matching "you draw / you lose".
 */
val KainTraitorousDragoon = card("Kain, Traitorous Dragoon") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Legendary Creature — Human Knight"
    oracleText = "Jump — During your turn, Kain has flying.\n" +
        "Whenever Kain deals combat damage to a player, that player gains control of Kain. " +
        "If they do, you draw that many cards, create that many tapped Treasure tokens, then lose that much life."
    power = 2
    toughness = 4

    // Jump — During your turn, Kain has flying.
    staticAbility {
        condition = Conditions.IsYourTurn
        ability = GrantKeyword(keyword = Keyword.FLYING, filter = GroupFilter.source())
    }

    triggeredAbility {
        trigger = Triggers.DealsCombatDamageToPlayer
        val damageDealt = DynamicAmount.ContextProperty(ContextPropertyKey.TRIGGER_DAMAGE_AMOUNT)
        effect = IfYouDoEffect(
            action = GiveControlToTargetPlayerEffect(
                permanent = EffectTarget.Self,
                newController = EffectTarget.PlayerRef(Player.TriggeringPlayer),
            ),
            ifYouDo = Effects.Composite(
                listOf(
                    Effects.DrawCards(damageDealt),
                    Effects.CreateTreasure(count = damageDealt, tapped = true),
                    Effects.LoseLife(amount = damageDealt, target = EffectTarget.PlayerRef(Player.You)),
                ),
            ),
            successCriterion = SuccessCriterion.ControlChanged,
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "105"
        artist = "Russell Dongjun Lu"
        imageUri = "https://cards.scryfall.io/normal/front/f/8/f8c86be0-e1b3-4a78-9254-238dd936914b.jpg?1782686519"
    }
}
