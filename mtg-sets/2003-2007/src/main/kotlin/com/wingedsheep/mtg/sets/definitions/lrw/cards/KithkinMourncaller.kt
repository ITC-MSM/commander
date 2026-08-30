package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding

/**
 * Kithkin Mourncaller
 * {2}{G}
 * Creature — Kithkin Scout
 * 2/2
 * Whenever an attacking Kithkin or Elf is put into your graveyard from the battlefield, you may
 * draw a card.
 *
 * No "another": Mourncaller is itself a Kithkin, so dying while attacking triggers its own ability
 * ([TriggerBinding.ANY]). "Your graveyard" is ownership rather than control — a permanent always
 * goes to its owner's graveyard (CR 400.3). The attacking check is answered from last-known
 * information: the creature is removed from combat as it leaves the battlefield (CR 506.4), so the
 * trigger gate reads the zone-change snapshot's `wasAttacking`.
 */
val KithkinMourncaller = card("Kithkin Mourncaller") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Kithkin Scout"
    power = 2
    toughness = 2
    oracleText = "Whenever an attacking Kithkin or Elf is put into your graveyard from the battlefield, you may draw a card."

    triggeredAbility {
        trigger = Triggers.leavesBattlefield(
            filter = GameObjectFilter.Permanent.withAnySubtype("Kithkin", "Elf").attacking().ownedByYou(),
            to = Zone.GRAVEYARD,
            binding = TriggerBinding.ANY,
        )
        optional = true
        effect = Effects.DrawCards(1)
        description = "you may draw a card."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "224"
        artist = "Dominick Domingo"
        flavorText = "Eidren's hunts are dangerous affairs. All dread the inevitable recounting of those who died while flushing out his prey."
        imageUri = "https://cards.scryfall.io/normal/front/0/a/0a8b6f90-c609-4ffd-9136-9fd7a833ebb3.jpg?1783942861"
    }
}
