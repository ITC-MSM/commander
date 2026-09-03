package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.TriggeredAbility
import com.wingedsheep.sdk.scripting.effects.AddCountersEffect
import com.wingedsheep.sdk.scripting.effects.GrantTriggeredAbilityEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Makeshift Mannequin — Lorwyn #124
 * {3}{B} · Instant
 *
 * Return target creature card from your graveyard to the battlefield with a mannequin counter on
 * it. For as long as that creature has a mannequin counter on it, it has "When this creature
 * becomes the target of a spell or ability, sacrifice it."
 *
 * Three effects over one target handle. The reanimation is Late to Dinner's
 * [Effects.PutOntoBattlefieldFromGraveyard] (the explicit `fromZone` makes it a graveyard
 * reanimation rather than a generic blink), and the counter and the grant both name the *same*
 * target — the card keeps its id across the zone change, so both land on the permanent that just
 * entered.
 *
 * The counter is load-bearing, not flavour: the printed "for as long as that creature has a
 * mannequin counter on it" is [Duration.WhileAffectedHasCounter], so removing the counter (Hex
 * Parasite, Vampire Hexmage) really does strip the drawback. Wiring it as
 * [Duration.Permanent] would read identically on the card and be wrong in exactly that case.
 *
 * The granted ability is a plain self-bound [EventPattern.BecomesTargetEvent] — *any* spell or
 * ability, either player's, and the creature's own controller sacrifices it. Note this is one of
 * the few sacrifice riders that fires on a **friendly** targeting too, which is the whole point
 * of the card's drawback.
 */
val MakeshiftMannequin = card("Makeshift Mannequin") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "Return target creature card from your graveyard to the battlefield with a " +
        "mannequin counter on it. For as long as that creature has a mannequin counter on it, it " +
        "has \"When this creature becomes the target of a spell or ability, sacrifice it.\""

    spell {
        val creature = target("target creature card from your graveyard", Targets.CreatureCardInYourGraveyard)
        effect = Effects.PutOntoBattlefieldFromGraveyard(creature)
            .then(AddCountersEffect(Counters.MANNEQUIN, 1, creature))
            .then(
                GrantTriggeredAbilityEffect(
                    ability = TriggeredAbility.create(
                        trigger = EventPattern.BecomesTargetEvent(),
                        binding = TriggerBinding.SELF,
                        effect = Effects.SacrificeTarget(EffectTarget.Self),
                        descriptionOverride = "When this creature becomes the target of a spell " +
                            "or ability, sacrifice it."
                    ),
                    target = creature,
                    duration = Duration.WhileAffectedHasCounter(Counters.MANNEQUIN)
                )
            )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "124"
        artist = "Darrell Riche"
        flavorText = "\"This vulgar mimicry will end now.\"\n—Desmera, perfect of Wren's Run"
        imageUri = "https://cards.scryfall.io/normal/front/7/9/79029161-a2c9-4cf6-94c0-31400daa0e8e.jpg?1783942888"
    }
}
