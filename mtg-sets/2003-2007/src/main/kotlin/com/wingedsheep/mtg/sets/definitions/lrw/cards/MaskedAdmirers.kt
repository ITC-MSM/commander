package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.Gate
import com.wingedsheep.sdk.scripting.effects.GatedEffect
import com.wingedsheep.sdk.scripting.effects.PayManaCostEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Masked Admirers
 * {2}{G}{G}
 * Creature — Elf Shaman
 * 3/2
 * When this creature enters, draw a card.
 * Whenever you cast a creature spell, you may pay {G}{G}. If you do, return this card from your
 * graveyard to your hand.
 *
 * The recursion ability functions only from the graveyard (CR 113.6b), so it is zone-scoped with
 * `triggerZone = Zone.GRAVEYARD` the way Squee, Goblin Nabob is — without it the trigger would be
 * indexed only while Masked Admirers is on the battlefield, where it can do nothing.
 *
 * "You may pay {G}{G}. If you do, …" is a [Gate.MayPay], not a plain `optional = true`: the return
 * happens iff the mana is actually paid. [Effects.ReturnToHandFromGraveyard] carries the
 * `fromZone` guard the *self*-return needs — nothing re-examines the card at resolution, so a
 * Masked Admirers exiled from the graveyard in response would otherwise come back from exile.
 */
val MaskedAdmirers = card("Masked Admirers") {
    manaCost = "{2}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf Shaman"
    power = 3
    toughness = 2
    oracleText = "When this creature enters, draw a card.\n" +
        "Whenever you cast a creature spell, you may pay {G}{G}. If you do, return this card " +
        "from your graveyard to your hand."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.DrawCards(1)
        description = "draw a card."
    }

    triggeredAbility {
        trigger = Triggers.youCastSpell(GameObjectFilter.Creature)
        triggerZone = Zone.GRAVEYARD
        effect = GatedEffect(
            gate = Gate.MayPay(PayManaCostEffect(ManaCost.parse("{G}{G}"))),
            then = Effects.ReturnToHandFromGraveyard(EffectTarget.Self),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "230"
        artist = "Eric Fortune"
        flavorText = "\"Beauty determines value, and we determine beauty.\""
        imageUri = "https://cards.scryfall.io/normal/front/c/9/c9cf01d6-7d5d-4638-9884-733797c9f502.jpg?1783942860"
    }
}
