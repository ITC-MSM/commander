package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.scripting.CantBeBlocked
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.ForEachTargetEffect
import com.wingedsheep.sdk.scripting.effects.GrantKeywordEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Secret Tunnel
 * Land — Cave
 *
 * This land can't be blocked.
 * {T}: Add {C}.
 * {4}, {T}: Two target creatures you control that share a creature type can't be blocked this turn.
 *
 * The static "can't be blocked" is a printed [CantBeBlocked] on the land itself — harmless while the
 * land isn't a creature, but honored if it's ever animated. The activated ability uses a single
 * two-target requirement with [TargetObject.sameCreatureType] (count = 2), so the two chosen
 * creatures you control must share at least one (projected) creature type with each other — the
 * cross-target legality is enforced by `TargetValidator`. [ForEachTargetEffect] then rebinds
 * `ContextTarget(0)` to each chosen creature in turn and grants it "can't be blocked" until end of
 * turn via [GrantKeywordEffect] (the IcyBlast idiom — see the inline note on why a single
 * `GrantStaticAbility` over the two targets does not iterate correctly here).
 */
val SecretTunnel = card("Secret Tunnel") {
    manaCost = ""
    typeLine = "Land — Cave"
    oracleText = "This land can't be blocked.\n" +
        "{T}: Add {C}.\n" +
        "{4}, {T}: Two target creatures you control that share a creature type can't be blocked this turn."

    staticAbility {
        ability = CantBeBlocked()
    }

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddColorlessMana(1)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{4}"), Costs.Tap)
        target(
            "two target creatures you control",
            TargetCreature(
                count = 2,
                filter = TargetFilter.CreatureYouControl,
                sameCreatureType = true
            )
        )
        // Grant "can't be blocked this turn" to each of the two chosen creatures. ForEachTargetEffect
        // rebinds ContextTarget(0) to each target in turn; GrantKeywordEffect snapshots that concrete
        // per-iteration target into its continuous grant (the IcyBlast idiom for granting an
        // AbilityFlag to multiple targets — GrantStaticAbility does not iterate correctly here).
        effect = ForEachTargetEffect(
            effects = listOf(
                GrantKeywordEffect(AbilityFlag.CANT_BE_BLOCKED.name, EffectTarget.ContextTarget(0))
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "278"
        artist = "Alexander Forssberg"
        flavorText = "\"I forget the next couple of lines, but then it goes...'Secret tunnel! Secret tunnel!\"\n—Chong, nomad musician"
        imageUri = "https://cards.scryfall.io/normal/front/2/d/2d39a0e1-6484-409c-ab05-5b276925a949.jpg?1764122027"
    }
}
