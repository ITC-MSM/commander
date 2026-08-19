package com.wingedsheep.mtg.sets.definitions.mkm.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Tolsimir, Midnight's Light — Murders at Karlov Manor #236
 * {2}{G}{W}{W} · Legendary Creature — Elf Scout · 3/2 · Rare
 *
 * The attack trigger is ANY-bound over Wolves the controller controls, so it fires once for each
 * attacking Wolf — including Voja Fenstalker, the token the enters trigger makes. Tolsimir itself
 * is an Elf Scout and never triggers it.
 *
 * "If Tolsimir attacked this combat" is a CR 603.4 intervening-if: it is checked both when the
 * Wolves are declared and again on resolution, and it reads the per-entity "attacked this combat"
 * marker rather than the live attacking state — so Tolsimir dying after attackers are declared
 * still leaves the requirement in place, while a Wolf attacking in a *second* combat Tolsimir sat
 * out does nothing.
 *
 * The blocker is pinned to the triggering Wolf, not to Tolsimir, which is what
 * `Effects.ForceBlock`'s `attacker` parameter names.
 */
val TolsimirMidnightsLight = card("Tolsimir, Midnight's Light") {
    manaCost = "{2}{G}{W}{W}"
    colorIdentity = "GW"
    typeLine = "Legendary Creature — Elf Scout"
    oracleText = "Lifelink\n" +
        "When Tolsimir enters, create Voja Fenstalker, a legendary 5/5 green and white Wolf " +
        "creature token with trample.\n" +
        "Whenever a Wolf you control attacks, if Tolsimir attacked this combat, target creature " +
        "an opponent controls blocks that Wolf this combat if able."
    power = 3
    toughness = 2

    keywords(Keyword.LIFELINK)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateToken(
            power = 5,
            toughness = 5,
            colors = setOf(Color.GREEN, Color.WHITE),
            creatureTypes = setOf(Subtype.WOLF.value),
            keywords = setOf(Keyword.TRAMPLE),
            name = "Voja Fenstalker",
            legendary = true,
            imageUri = "https://cards.scryfall.io/normal/front/2/1/2175200f-3ed4-4b39-ac76-a7d25967aa8a.jpg?1783912604",
        )
    }

    triggeredAbility {
        trigger = Triggers.attacks(
            filter = GameObjectFilter.Creature.withSubtype(Subtype.WOLF).youControl(),
            binding = TriggerBinding.ANY,
        )
        interveningIf = Conditions.SourceAttackedThisCombat
        val blocker = target("creature an opponent controls", Targets.CreatureOpponentControls)
        effect = Effects.ForceBlock(blocker, attacker = EffectTarget.TriggeringEntity)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "236"
        artist = "Uriah Voth"
        imageUri = "https://cards.scryfall.io/normal/front/0/8/08d22402-c41d-43d7-be1f-42be1e300726.jpg?1783912835"
    }
}
