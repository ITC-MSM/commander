package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import com.wingedsheep.sdk.scripting.values.EntityReference

/**
 * Circle of Confinement
 * {1}{W}
 * Enchantment
 *
 * When this enchantment enters, exile target creature an opponent controls with mana value 3 or
 * less until this enchantment leaves the battlefield.
 * Whenever an opponent casts a Vampire spell with the same name as a card exiled with this
 * enchantment, you gain 2 life.
 *
 * The exile is linked (CR 610.3) — the Glass Casket shape: the leaves trigger returns only what
 * this enchantment exiled. The life trigger reads that same linked pile through
 * [EntityReference.LinkedExiledCard], so it fires only while the prisoner is still exiled; once the
 * Circle leaves and the card returns, the reference resolves to nothing and no spell matches.
 */
val CircleOfConfinement = card("Circle of Confinement") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment"
    oracleText = "When this enchantment enters, exile target creature an opponent controls with " +
        "mana value 3 or less until this enchantment leaves the battlefield.\n" +
        "Whenever an opponent casts a Vampire spell with the same name as a card exiled with " +
        "this enchantment, you gain 2 life."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val t = target(
            "target",
            TargetCreature(filter = TargetFilter.Creature.manaValueAtMost(3).opponentControls())
        )
        effect = Effects.ExileUntilLeaves(t)
    }

    triggeredAbility {
        trigger = Triggers.LeavesBattlefield
        effect = Effects.ReturnLinkedExileUnderOwnersControl()
    }

    triggeredAbility {
        trigger = Triggers.opponentCasts(
            spellFilter = GameObjectFilter.Any
                .withSubtype("Vampire")
                .sharingNameWith(EntityReference.LinkedExiledCard()),
        )
        effect = Effects.GainLife(2)
        description = "Whenever an opponent casts a Vampire spell with the same name as a card " +
            "exiled with Circle of Confinement, you gain 2 life."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "7"
        artist = "Lorenzo Mastroianni"
        imageUri = "https://cards.scryfall.io/normal/front/1/3/13031fb6-9d5a-4add-9a86-28b2a9373fd2.jpg?1783924926"
        ruling(
            "2021-11-19",
            "If Circle of Confinement leaves the battlefield before its enters-the-battlefield " +
                "ability resolves, the target creature won't be exiled."
        )
        ruling(
            "2021-11-19",
            "If a token is exiled this way, it will cease to exist and won't return to the battlefield."
        )
    }
}
