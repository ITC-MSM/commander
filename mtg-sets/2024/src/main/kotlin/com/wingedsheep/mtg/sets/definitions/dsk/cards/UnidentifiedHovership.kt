package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Unidentified Hovership
 * {1}{W}{W}
 * Artifact — Vehicle
 * 2/2
 * Flying
 * When this Vehicle enters, exile up to one target creature with toughness 5 or less.
 * When this Vehicle leaves the battlefield, the exiled card's owner manifests dread.
 * Crew 1
 *
 * The ETB exiles the target via [Effects.ExileUntilLeaves], which links it to this source's
 * [com.wingedsheep.engine.state.components.battlefield.LinkedExileComponent]. Unlike a typical
 * "exile until ~ leaves" card, the exiled card is *not* returned — it stays exiled. The link
 * exists only so the leaves-the-battlefield trigger can find the exiled card's owner.
 *
 * On leave, [Player.OwnersOfLinkedExile] resolves to the distinct owners of the still-exiled
 * linked cards (CR ruling: "each player who owns one or more of the exiled cards manifests
 * dread" — once per owner, nothing when the pile is empty), and each manifests dread (CR 701.62)
 * via [Effects.ForEachPlayer] rebinding the shared [Patterns.Library.manifestDread] recipe to
 * that player (same construction as Fear of Impostors).
 */
val UnidentifiedHovership = card("Unidentified Hovership") {
    manaCost = "{1}{W}{W}"
    colorIdentity = "W"
    typeLine = "Artifact — Vehicle"
    power = 2
    toughness = 2
    oracleText = "Flying\n" +
        "When this Vehicle enters, exile up to one target creature with toughness 5 or less.\n" +
        "When this Vehicle leaves the battlefield, the exiled card's owner manifests dread.\n" +
        "Crew 1"

    keywords(Keyword.FLYING)

    // ETB: exile up to one target creature with toughness 5 or less, linked to this source.
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val creature = target(
            "up to one target creature with toughness 5 or less",
            TargetCreature(optional = true, filter = TargetFilter.Creature.toughnessAtMost(5))
        )
        effect = Effects.ExileUntilLeaves(creature)
    }

    // LTB: each owner of a card exiled with this Vehicle manifests dread.
    triggeredAbility {
        trigger = Triggers.LeavesBattlefield
        effect = Effects.ForEachPlayer(
            players = Player.OwnersOfLinkedExile,
            effects = Patterns.Library.manifestDread().effects,
        )
        description = "When this Vehicle leaves the battlefield, the exiled card's owner manifests dread."
    }

    keywordAbility(KeywordAbility.Numeric(Keyword.CREW, 1))

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "37"
        artist = "Jana Heidersdorf"
        imageUri = "https://cards.scryfall.io/normal/front/b/d/bd23f168-9ad0-4b4e-bfff-ae004c163727.jpg?1726285993"

        ruling("2024-09-20", "If Unidentified Hovership leaves the battlefield before its second ability resolves, the ability still exiles the target creature.")
        ruling("2024-09-20", "If there's no exiled card when Unidentified Hovership's third ability resolves (most likely because its second ability hasn't resolved yet), the ability won't do anything.")
        ruling("2024-09-20", "If Unidentified Hovership's second ability exiled more than one card (possibly because another effect caused it to trigger an additional time), each player who owns one or more of the exiled cards manifests dread.")
    }
}
