package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.conditions.ComparisonOperator
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Nullstone Gargoyle — Ravnica: City of Guilds #266
 * {9} · Artifact Creature — Gargoyle · 4/5 · Rare
 *
 * Flying
 * Whenever the first noncreature spell of a turn is cast, counter that spell.
 *
 * "The first noncreature spell **of a turn**" is a per-turn count across every player, which is
 * why this is not `Triggers.NthSpellCast(1, …)`: that event counts the *caster's* own history, so
 * the second player's first noncreature spell of the turn would also trigger it. Instead the
 * trigger is the plain "a player casts a noncreature spell" event with a `triggerRestriction`
 * that reads the whole table's cast history — `SpellsCastThisTurn(Player.Each, Noncreature)`
 * sums every player's records, and the triggering spell is already recorded when the event is
 * examined, so "equals 1" is exactly "this is the turn's first".
 *
 * The count is a `triggerRestriction`, not an intervening-"if": it qualifies the *event* (CR
 * 603.2) and is never re-checked on resolution, so a second noncreature spell cast in response
 * neither rescues the first nor gets countered itself — both 2005 rulings. And because the
 * history counts casts rather than resolutions, a noncreature spell cast before the Gargoyle
 * arrived still closes the window for the turn.
 */
val NullstoneGargoyle = card("Nullstone Gargoyle") {
    manaCost = "{9}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Gargoyle"
    power = 4
    toughness = 5
    oracleText = "Flying\nWhenever the first noncreature spell of a turn is cast, counter that spell."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.anyPlayerCasts(GameObjectFilter.Noncreature)
        triggerRestriction = Conditions.CompareAmounts(
            DynamicAmount.SpellsCastThisTurn(player = Player.Each, filter = GameObjectFilter.Noncreature),
            ComparisonOperator.EQ,
            DynamicAmount.Fixed(1),
        )
        effect = Effects.CounterTriggeringSpell()
        description = "Whenever the first noncreature spell of a turn is cast, counter that spell."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "266"
        artist = "Glenn Fabry"
        flavorText = "Nullstone absorbs and dampens most forms of magical energy, as well as light, " +
            "sound, and heat."
        imageUri = "https://cards.scryfall.io/normal/front/1/d/1dc3aa88-2555-4862-a771-e9d5b9eab3ad.jpg?1783943597"
        ruling(
            "2005-10-01",
            "Nullstone Gargoyle cares about the first noncreature spell cast during a turn, not the " +
                "first one it \"sees.\" If a noncreature spell was cast before Nullstone Gargoyle " +
                "entered the battlefield, it won't counter the next one."
        )
        ruling(
            "2005-10-01",
            "If another spell is cast in response to the first spell, Nullstone Gargoyle doesn't " +
                "affect that second spell."
        )
    }
}
