package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.disturb
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.RedirectZoneChange
import com.wingedsheep.sdk.scripting.TriggerBinding

/**
 * Brine Comber // Brinebound Gift (Innistrad: Crimson Vow #233 — the card's earliest printing)
 * {1}{W}{U} · Creature — Spirit 1/1 // Enchantment — Aura
 *
 * Front — Brine Comber ({1}{W}{U}, Creature — Spirit, 1/1)
 *   Whenever this creature enters or becomes the target of an Aura spell, create a 1/1 white
 *   Spirit creature token with flying.
 *   Disturb {W}{U}
 *
 * Back — Brinebound Gift (Enchantment — Aura, white-blue color indicator)
 *   Enchant creature
 *   Whenever this Aura enters or enchanted creature becomes the target of an Aura spell, create a
 *   1/1 white Spirit creature token with flying.
 *   If this Aura would be put into a graveyard from anywhere, exile it instead.
 *
 * Implementation: each face's printed "enters **or** becomes the target" is two triggered
 * abilities, the house shape for an or-joined trigger (Crow of Dark Tidings' enters/dies) — and
 * here it is forced, because the back face's two halves don't even share a binding: the Aura's own
 * ETB is SELF-bound while "enchanted creature becomes the target" is
 * [TriggerBinding.ATTACHED]-bound. Both target halves use `Triggers.BecomesTargetOfAuraSpell`,
 * which reads the *targeting* spell through `BecomesTargetEvent.sourceFilter`.
 *
 * An Aura spell chooses its target as it is cast (CR 303.4a), so neither half fires for the Aura
 * that is already attached — only for a new Aura spell cast at the creature. Disturb (CR 702.146)
 * puts the card on the stack back face up (CR 712.8c), so the disturb cast is itself an Aura spell
 * that picks its host from `auraTarget`; that cast targets the creature, which means a *different*
 * Brine Comber already on the battlefield sees it. The exile-instead clause is
 * [RedirectZoneChange] with `selfOnly = true` so it functions from every zone (CR 614.12).
 */
private val SpiritTokenWithFlying = Effects.CreateToken(
    power = 1,
    toughness = 1,
    colors = setOf(Color.WHITE),
    creatureTypes = setOf("Spirit"),
    keywords = setOf(Keyword.FLYING),
    imageUri = "https://cards.scryfall.io/normal/front/6/b/6bee4081-5d74-4cc2-ba2f-887bc8799513.jpg?1783924700"
)

private val BrineComberFront = card("Brine Comber") {
    manaCost = "{1}{W}{U}"
    colorIdentity = "WU"
    typeLine = "Creature — Spirit"
    power = 1
    toughness = 1
    oracleText = "Whenever this creature enters or becomes the target of an Aura spell, create a " +
        "1/1 white Spirit creature token with flying.\n" +
        "Disturb {W}{U} (You may cast this card from your graveyard transformed for its disturb cost.)"

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = SpiritTokenWithFlying
    }

    triggeredAbility {
        trigger = Triggers.BecomesTargetOfAuraSpell()
        effect = SpiritTokenWithFlying
    }

    disturb("{W}{U}")

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "233"
        artist = "Olena Richards"
        imageUri = "https://cards.scryfall.io/normal/front/8/d/8d233bc5-8a08-4d38-abd4-21a112141afd.jpg?1783924801"
    }
}

private val BrineboundGift = card("Brinebound Gift") {
    manaCost = ""
    colorIdentity = "WU"
    colorIndicator = "WU"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\n" +
        "Whenever this Aura enters or enchanted creature becomes the target of an Aura spell, " +
        "create a 1/1 white Spirit creature token with flying.\n" +
        "If this Aura would be put into a graveyard from anywhere, exile it instead."

    auraTarget = Targets.Creature

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = SpiritTokenWithFlying
    }

    triggeredAbility {
        trigger = Triggers.BecomesTargetOfAuraSpell(binding = TriggerBinding.ATTACHED)
        effect = SpiritTokenWithFlying
    }

    replacementEffect(
        RedirectZoneChange(
            newDestination = Zone.EXILE,
            appliesTo = EventPattern.ZoneChangeEvent(to = Zone.GRAVEYARD),
            selfOnly = true,
        )
    )

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "233"
        artist = "Olena Richards"
        imageUri = "https://cards.scryfall.io/normal/back/8/d/8d233bc5-8a08-4d38-abd4-21a112141afd.jpg?1783924801"
    }
}

val BrineComber: CardDefinition = CardDefinition.doubleFacedPermanent(
    frontFace = BrineComberFront,
    backFace = BrineboundGift,
)
