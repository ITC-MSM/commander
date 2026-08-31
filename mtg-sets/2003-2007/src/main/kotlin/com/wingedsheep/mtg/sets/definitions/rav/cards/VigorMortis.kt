package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect

/**
 * Vigor Mortis — Ravnica: City of Guilds #111
 * {2}{B}{B} · Sorcery
 *
 * Return target creature card from your graveyard to the battlefield. If {G} was spent to cast this
 * spell, that creature enters with an additional +1/+1 counter on it.
 *
 * The rider is spelled "enters **with**", not "then put a counter on it", so the counter has to ride
 * along with the move rather than follow it — that is `Effects.Move`'s `addCounterType`, the same
 * spelling Thunderbolts Conspiracy uses for "return it to the battlefield with a finality counter on
 * it". The counter lands inside the move executor, before the enters-the-battlefield triggers it
 * caused are put on the stack, so a trigger that reads the creature's counters or its power sees the
 * counter — the distinction Miraculous Recovery's rulings draw for the *other* wording.
 *
 * The gate therefore has to sit around the whole move (two branches of the same reanimation), not
 * after it: there is no point in the resolution at which the creature is on the battlefield and the
 * counter is still pending.
 */
val VigorMortis = card("Vigor Mortis") {
    manaCost = "{2}{B}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "Return target creature card from your graveyard to the battlefield. " +
        "If {G} was spent to cast this spell, that creature enters with an additional +1/+1 counter on it."

    spell {
        val creatureCard = target(
            "target creature card from your graveyard",
            Targets.CreatureCardInYourGraveyard
        )
        effect = ConditionalEffect(
            condition = Conditions.ManaSpentToCastIncludes(requiredGreen = 1),
            effect = Effects.Move(
                creatureCard,
                Zone.BATTLEFIELD,
                fromZone = Zone.GRAVEYARD,
                addCounterType = CounterType.PLUS_ONE_PLUS_ONE,
            ),
            elseEffect = Effects.Move(creatureCard, Zone.BATTLEFIELD, fromZone = Zone.GRAVEYARD),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "111"
        artist = "Dan Murayama Scott"
        flavorText = "After fear and weakness rot away, that which remains is stronger than its living form."
        imageUri = "https://cards.scryfall.io/normal/front/8/f/8fe06560-bb3f-4f0b-ad3c-7257419eb8ef.jpg?1783943660"
        ruling(
            "2005-10-01",
            "The +1/+1 counter from this ability is in addition to any other counters the creature enters with."
        )
    }
}
