package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.effects.TransformEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Venat, Heart of Hydaelyn // Hydaelyn, the Mothercrystal
 * {1}{W}{W} — Legendary Creature — Elder Wizard 3/3 // Legendary Creature — God 4/4
 *
 * Front — Venat, Heart of Hydaelyn:
 *   Whenever you cast a legendary spell, draw a card. This ability triggers only once each turn.
 *   Hero's Sundering — {7}, {T}: Exile target nonland permanent. Transform Venat.
 *     Activate only as a sorcery.
 *
 * Back — Hydaelyn, the Mothercrystal:
 *   Indestructible
 *   Blessing of Light — At the beginning of combat on your turn, put a +1/+1 counter on another
 *     target creature you control. Until your next turn, it gains indestructible. If that creature
 *     is legendary, draw a card.
 *
 * Note: the task brief referred to the back face as "Venat, Sundered Heart"; Scryfall's authoritative
 * Oracle text names it "Hydaelyn, the Mothercrystal" (Legendary Creature — God, 4/4). Oracle text of
 * both faces is otherwise as briefed.
 */
private val HydaelynTheMothercrystal = card("Hydaelyn, the Mothercrystal") {
    manaCost = ""
    colorIdentity = "W"
    typeLine = "Legendary Creature — God"
    oracleText = "Indestructible\n" +
        "Blessing of Light — At the beginning of combat on your turn, put a +1/+1 counter on " +
        "another target creature you control. Until your next turn, it gains indestructible. " +
        "If that creature is legendary, draw a card."
    power = 4
    toughness = 4

    keywords(Keyword.INDESTRUCTIBLE)

    // Blessing of Light — At the beginning of combat on your turn, put a +1/+1 counter on another
    // target creature you control. Until your next turn, it gains indestructible. If that creature
    // is legendary, draw a card.
    triggeredAbility {
        trigger = Triggers.BeginCombat
        val creature = target("creature", TargetCreature(filter = TargetFilter.OtherCreatureYouControl))
        effect = Effects.Composite(
            Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, creature),
            Effects.GrantKeyword(Keyword.INDESTRUCTIBLE, creature, Duration.UntilYourNextTurn),
            ConditionalEffect(
                // "If that creature is legendary, draw a card." The +1/+1 target is the first (only)
                // chosen target, so test it via ContextTarget(0) like Blessing of Belzenlok.
                condition = Conditions.TargetMatchesFilter(GameObjectFilter.Any.legendary()),
                effect = Effects.DrawCards(1),
            ),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "39"
        artist = "Colin Boyer"
        flavorText = "\"For the sake of all, I beseech thee: deliver us from this fate!\""
        imageUri = "https://cards.scryfall.io/normal/back/2/6/2625c00d-0a51-4481-bf36-cf13a2546242.jpg?1782686568"
    }
}

private val VenatHeartOfHydaelynFront = card("Venat, Heart of Hydaelyn") {
    manaCost = "{1}{W}{W}"
    colorIdentity = "W"
    typeLine = "Legendary Creature — Elder Wizard"
    oracleText = "Whenever you cast a legendary spell, draw a card. This ability triggers only " +
        "once each turn.\n" +
        "Hero's Sundering — {7}, {T}: Exile target nonland permanent. Transform Venat. " +
        "Activate only as a sorcery."
    power = 3
    toughness = 3

    // Whenever you cast a legendary spell, draw a card. This ability triggers only once each turn.
    triggeredAbility {
        trigger = Triggers.youCastSpell(spellFilter = GameObjectFilter.Any.legendary())
        oncePerTurn = true
        effect = Effects.DrawCards(1)
    }

    // Hero's Sundering — {7}, {T}: Exile target nonland permanent. Transform Venat.
    // Activate only as a sorcery.
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{7}"), Costs.Tap)
        timing = TimingRule.SorcerySpeed
        val victim = target("nonland permanent", TargetPermanent(filter = TargetFilter.NonlandPermanent))
        effect = Effects.Composite(
            Effects.Exile(victim),
            TransformEffect(EffectTarget.Self),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "39"
        artist = "Colin Boyer"
        flavorText = "\"Show me your strength of will!\""
        imageUri = "https://cards.scryfall.io/normal/front/2/6/2625c00d-0a51-4481-bf36-cf13a2546242.jpg?1782686568"
    }
}

val VenatHeartOfHydaelyn: CardDefinition = CardDefinition.doubleFacedCreature(
    frontFace = VenatHeartOfHydaelynFront,
    backFace = HydaelynTheMothercrystal,
)
