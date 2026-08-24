package com.wingedsheep.mtg.sets.definitions.m15.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.effects.Gate
import com.wingedsheep.sdk.scripting.effects.GatedEffect
import com.wingedsheep.sdk.scripting.effects.PayManaCostEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Spirit Bonds
 * {1}{W}
 * Enchantment
 * Whenever a nontoken creature you control enters, you may pay {W}. If you do, create a 1/1 white
 * Spirit creature token with flying.
 * {1}{W}, Sacrifice a Spirit: Target non-Spirit creature gains indestructible until end of turn.
 *
 * "You may pay {W}. If you do" is a [Gate.MayPay] — the payment and the payoff are one resolution,
 * not a reflexive trigger. The Spirit sacrificed is any Spirit *permanent* (the tokens qualify);
 * the target must be a non-Spirit creature, so a Spirit token can't be its own shield.
 */
val SpiritBonds = card("Spirit Bonds") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment"
    oracleText =
        "Whenever a nontoken creature you control enters, you may pay {W}. If you do, create a 1/1 white Spirit creature token with flying.\n" +
        "{1}{W}, Sacrifice a Spirit: Target non-Spirit creature gains indestructible until end of turn. (Damage and effects that say \"destroy\" don't destroy it.)"

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Creature.nontoken().youControl(),
            binding = TriggerBinding.ANY
        )
        effect = GatedEffect(
            gate = Gate.MayPay(PayManaCostEffect(ManaCost.parse("{W}"))),
            then = Effects.CreateToken(
                power = 1,
                toughness = 1,
                colors = setOf(Color.WHITE),
                creatureTypes = setOf("Spirit"),
                keywords = setOf(Keyword.FLYING),
            )
        )
        description = "Whenever a nontoken creature you control enters, you may pay {W}. If you do, create a 1/1 white Spirit creature token with flying."
    }

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{1}{W}"),
            Costs.Sacrifice(GameObjectFilter.Permanent.withSubtype(Subtype.SPIRIT))
        )
        val t = target("target non-Spirit creature", TargetCreature(filter = TargetFilter(GameObjectFilter.Creature.notSubtype(Subtype.SPIRIT))))
        effect = Effects.GrantKeyword(Keyword.INDESTRUCTIBLE, t)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "37"
        artist = "Willian Murai"
        flavorText = "Designed by Justin Gary"
        imageUri = "https://cards.scryfall.io/normal/front/a/a/aa2a3aaa-e78a-48cc-b7d3-7f65e467054c.jpg?1783939197"
    }
}
