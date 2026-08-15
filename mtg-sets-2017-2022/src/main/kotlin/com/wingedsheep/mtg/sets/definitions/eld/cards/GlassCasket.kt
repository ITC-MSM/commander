package com.wingedsheep.mtg.sets.definitions.eld.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Glass Casket
 * {1}{W}
 * Artifact
 *
 * When this artifact enters, exile target creature an opponent controls with mana value 3 or less
 * until this artifact leaves the battlefield.
 *
 * Canonical printing is Throne of Eldraine; Wilds of Eldraine reprints it (see
 * `definitions/woe/cards/GlassCasketReprint.kt`).
 *
 * The exile is linked (CR 610.3): the leaves-the-battlefield trigger returns whatever this
 * artifact exiled, so a second Glass Casket never returns the first one's prisoner. If the
 * artifact leaves before its enter-trigger resolves, the return trigger fires with nothing linked
 * and the creature stays exiled forever — the printed behavior.
 */
val GlassCasket = card("Glass Casket") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Artifact"
    oracleText = "When this artifact enters, exile target creature an opponent controls with " +
        "mana value 3 or less until this artifact leaves the battlefield."

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

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "15"
        artist = "Anastasia Ovchinnikova"
        flavorText = "Fate will decide whether it's a bed or a tomb."
        imageUri = "https://cards.scryfall.io/normal/front/5/6/562f1c51-d245-4771-bf61-415297e4f9d5.jpg?1783932673"
    }
}
