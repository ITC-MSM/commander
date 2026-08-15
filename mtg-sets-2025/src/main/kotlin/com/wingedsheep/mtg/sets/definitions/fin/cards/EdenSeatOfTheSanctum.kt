package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.ReflexiveTriggerEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Eden, Seat of the Sanctum
 * Land — Town
 * {T}: Add {C}.
 * {5}, {T}: Mill two cards. Then you may sacrifice this land. When you do, return another
 *   target permanent card from your graveyard to your hand.
 *
 * The second ability mills two, then composes a [ReflexiveTriggerEffect] for the optional
 * "sacrifice this land. When you do, …" follow-up: the action sacrifices Eden itself
 * ([Effects.SacrificeTarget] of [EffectTarget.Self]), and the reflexive "when you do" returns
 * a permanent card from your graveyard to your hand. Its target is chosen *after* the sacrifice
 * resolves (reflexive trigger targeting), so Eden is already in the graveyard by then;
 * `excludeSelf = true` on the target filter models the "another" restriction by excluding the
 * ability's source (Eden) from the legal returnable cards.
 */
val EdenSeatOfTheSanctum = card("Eden, Seat of the Sanctum") {
    manaCost = ""
    colorIdentity = ""
    typeLine = "Land — Town"
    oracleText = "{T}: Add {C}.\n" +
        "{5}, {T}: Mill two cards. Then you may sacrifice this land. When you do, return another " +
        "target permanent card from your graveyard to your hand."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddColorlessMana(1)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{5}"), Costs.Tap)
        effect = Effects.Composite(
            Patterns.Library.mill(2),
            ReflexiveTriggerEffect(
                action = Effects.SacrificeTarget(EffectTarget.Self),
                optional = true,
                reflexiveEffect = Effects.ReturnToHand(EffectTarget.ContextTarget(0)),
                reflexiveTargetRequirements = listOf(
                    TargetObject(
                        filter = TargetFilter(
                            baseFilter = GameObjectFilter.Permanent.ownedByYou(),
                            zone = Zone.GRAVEYARD,
                            excludeSelf = true,
                        )
                    )
                ),
            ),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "277"
        artist = "Leon Tukker"
        flavorText = "This city, Cocoon's capital, shares its name with its fal'Cie patron."
        imageUri = "https://cards.scryfall.io/normal/front/e/2/e28eac1e-adc7-4f8d-b206-bef09ba07d38.jpg?1748706821"
    }
}
