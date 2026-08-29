package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.disturb
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.GrantTriggeredAbility
import com.wingedsheep.sdk.scripting.RedirectZoneChange
import com.wingedsheep.sdk.scripting.TriggeredAbility
import com.wingedsheep.sdk.scripting.effects.CREATED_TOKENS
import com.wingedsheep.sdk.scripting.effects.CreateDelayedTriggerEffect
import com.wingedsheep.sdk.scripting.effects.CreateTokenEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Dorothea, Vengeful Victim // Dorothea's Retribution (Innistrad: Crimson Vow #235 — the card's
 * earliest printing)
 * {W}{U} · Legendary Creature — Spirit 4/4 // Enchantment — Aura
 *
 * Front — Dorothea, Vengeful Victim ({W}{U}, Legendary Creature — Spirit, 4/4)
 *   Flying
 *   When Dorothea attacks or blocks, sacrifice it at end of combat.
 *   Disturb {1}{W}{U}
 *
 * Back — Dorothea's Retribution (Enchantment — Aura, white/blue color indicator)
 *   Enchant creature
 *   Enchanted creature has "Whenever this creature attacks, create a 4/4 white Spirit creature
 *   token with flying that's tapped and attacking. Sacrifice that token at end of combat."
 *   If Dorothea's Retribution would be put into a graveyard from anywhere, exile it instead.
 *
 * "Attacks or blocks" is one printed ability with two events, so it is one [Triggers.or] over the
 * two SELF-bound atoms rather than two triggered abilities — a creature can never do both in one
 * combat, so the disjunction fires at most once. The sacrifice is a delayed trigger set up on
 * resolution ([CreateDelayedTriggerEffect] at [Step.END_COMBAT], CR 603.7b), not an immediate one:
 * Dorothea deals her combat damage first, and if she leaves the battlefield before end of combat
 * the delayed trigger simply finds nothing to sacrifice.
 *
 * The back face quotes the *other* half of the card's design onto the enchanted creature, which is
 * [GrantTriggeredAbility] — the same shape [MischievousCatgeist]'s Aura face uses. Inside the
 * quotes, "this creature" is the creature that has the ability (the enchanted one), which is what
 * `Triggers.Attacks`'s SELF binding already means for a granted ability. The token half is Geist of
 * Saint Traft's exactly: [CreateTokenEffect] with `tapped`/`attacking` (CR 508.1 — put onto the
 * battlefield attacking, never "declared", so it triggers nothing that watches attack declaration)
 * followed by a delayed trigger over `CREATED_TOKENS`, which is how "that token" names the object
 * the same resolution just made. Geist exiles its token; this one sacrifices it, so the token's
 * controller loses it even if it has changed hands.
 *
 * Disturb is CR 702.146; the exile-instead clause is [RedirectZoneChange] with `selfOnly = true` so
 * it functions from every zone (CR 614.12).
 */
private val DorotheaVengefulVictimFront = card("Dorothea, Vengeful Victim") {
    manaCost = "{W}{U}"
    colorIdentity = "WU"
    typeLine = "Legendary Creature — Spirit"
    power = 4
    toughness = 4
    oracleText = "Flying\n" +
        "When Dorothea attacks or blocks, sacrifice it at end of combat.\n" +
        "Disturb {1}{W}{U} (You may cast this card from your graveyard transformed for its disturb cost.)"

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.or(Triggers.Attacks, Triggers.Blocks)
        effect = CreateDelayedTriggerEffect(
            step = Step.END_COMBAT,
            effect = Effects.SacrificeTarget(EffectTarget.Self),
        )
        description = "When Dorothea attacks or blocks, sacrifice it at end of combat."
    }

    disturb("{1}{W}{U}")

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "235"
        artist = "Marta Nael"
        flavorText = "\"Every day a Voldaren draws breath is a day I cannot rest.\""
        imageUri = "https://cards.scryfall.io/normal/front/8/c/8c927707-abb5-4a9d-ac53-25df182d6e9b.jpg?1783924799"
    }
}

private val DorotheasRetribution = card("Dorothea's Retribution") {
    manaCost = ""
    colorIdentity = "WU"
    colorIndicator = "WU" // Transformed back face, no mana cost (CR 204).
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\n" +
        "Enchanted creature has \"Whenever this creature attacks, create a 4/4 white Spirit " +
        "creature token with flying that's tapped and attacking. Sacrifice that token at end of " +
        "combat.\"\n" +
        "If Dorothea's Retribution would be put into a graveyard from anywhere, exile it instead."

    auraTarget = Targets.Creature

    staticAbility {
        ability = GrantTriggeredAbility(
            ability = TriggeredAbility.create(
                trigger = Triggers.Attacks.event,
                binding = Triggers.Attacks.binding,
                effect = CreateTokenEffect(
                    power = 4,
                    toughness = 4,
                    colors = setOf(Color.WHITE),
                    creatureTypes = setOf("Spirit"),
                    keywords = setOf(Keyword.FLYING),
                    tapped = true,
                    attacking = true,
                ).then(
                    CreateDelayedTriggerEffect(
                        step = Step.END_COMBAT,
                        effect = Effects.SacrificeTarget(EffectTarget.PipelineTarget(CREATED_TOKENS, 0)),
                    ),
                ),
            )
        )
    }

    replacementEffect(
        RedirectZoneChange(
            newDestination = Zone.EXILE,
            appliesTo = EventPattern.ZoneChangeEvent(to = Zone.GRAVEYARD),
            selfOnly = true,
        )
    )

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "235"
        artist = "Marta Nael"
        imageUri = "https://cards.scryfall.io/normal/back/8/c/8c927707-abb5-4a9d-ac53-25df182d6e9b.jpg?1783924799"
    }
}

val DorotheaVengefulVictim: CardDefinition = CardDefinition.doubleFacedPermanent(
    frontFace = DorotheaVengefulVictimFront,
    backFace = DorotheasRetribution,
)
