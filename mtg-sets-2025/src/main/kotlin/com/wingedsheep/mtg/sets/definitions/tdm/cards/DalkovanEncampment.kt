package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.conditions.Exists
import com.wingedsheep.sdk.scripting.effects.AddManaEffect
import com.wingedsheep.sdk.scripting.effects.CreateDelayedTriggerEffect
import com.wingedsheep.sdk.scripting.effects.CreateTokenEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Dalkovan Encampment — Tarkir: Dragonstorm #253
 * Land · Rare
 *
 * This land enters tapped unless you control a Swamp or a Mountain.
 * {T}: Add {W}.
 * {2}{W}, {T}: Whenever you attack this turn, create two 1/1 red Warrior creature tokens
 * that are tapped and attacking. Sacrifice them at the beginning of the next end step.
 *
 * Modeled as a check-land ([EntersTapped] with an Exists-Swamp-or-Mountain unless condition,
 * mirroring Isolated Chapel) plus a {T}: Add {W} mana ability. The {2}{W},{T} ability installs
 * a [CreateDelayedTriggerEffect] on [Triggers.YouAttack] that lasts the rest of the turn
 * (default EndOfTurn expiry, fireOnce = false) — so each time you declare attackers this turn it
 * creates two tapped-and-attacking 1/1 red Warrior tokens that are sacrificed at the next end
 * step. The token shape and end-step sacrifice match the Mobilize wiring (CardBuilder.mobilize).
 */
val DalkovanEncampment = card("Dalkovan Encampment") {
    typeLine = "Land"
    colorIdentity = "W"
    oracleText = "This land enters tapped unless you control a Swamp or a Mountain.\n" +
        "{T}: Add {W}.\n" +
        "{2}{W}, {T}: Whenever you attack this turn, create two 1/1 red Warrior creature tokens " +
        "that are tapped and attacking. Sacrifice them at the beginning of the next end step."

    replacementEffect(EntersTapped(
        unlessCondition = Conditions.Any(
            Exists(Player.You, Zone.BATTLEFIELD, GameObjectFilter.Land.withSubtype("Swamp")),
            Exists(Player.You, Zone.BATTLEFIELD, GameObjectFilter.Land.withSubtype("Mountain"))
        )
    ))

    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddManaEffect(Color.WHITE)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}{W}"), Costs.Tap)
        effect = CreateDelayedTriggerEffect(
            trigger = Triggers.YouAttack,
            effect = CreateTokenEffect(
                count = DynamicAmount.Fixed(2),
                power = 1,
                toughness = 1,
                colors = setOf(Color.RED),
                creatureTypes = setOf("Warrior"),
                tapped = true,
                attacking = true,
                sacrificeAtStep = Step.END
            )
        )
        description = "Whenever you attack this turn, create two 1/1 red Warrior creature tokens " +
            "that are tapped and attacking. Sacrifice them at the beginning of the next end step."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "253"
        artist = "Marina Ortega Lorente"
        imageUri = "https://cards.scryfall.io/normal/front/9/8/98ad5f0c-8775-4e89-8e92-84a6ade93e35.jpg?1743205000"
    }
}
