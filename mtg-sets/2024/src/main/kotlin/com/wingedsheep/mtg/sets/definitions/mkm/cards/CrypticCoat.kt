package com.wingedsheep.mtg.sets.definitions.mkm.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantBeBlocked
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.FaceDownMode
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Cryptic Coat — Murders at Karlov Manor #50
 * {2}{U} · Artifact — Equipment
 *
 * When this Equipment enters, cloak the top card of your library, then attach this Equipment to it.
 * Equipped creature gets +1/+0 and can't be blocked.
 * {1}{U}: Return this Equipment to its owner's hand.
 *
 * The enters trigger is one pipeline, not two effects: [GatherCardsEffect] lifts the top card,
 * [MoveCollectionEffect] puts it onto the battlefield face down as a cloak ([FaceDownMode.CLOAK] —
 * a 2/2 with ward {2}, CR 701.58a), and `storeMovedAs` hands the *battlefield* entity id to the
 * attach step through [EffectTarget.PipelineTarget]. Attaching by id is what makes the "then" in
 * the oracle text real: the Coat lands on the very permanent this ability just created, with no
 * targeting and therefore no ward tax on itself.
 *
 * Cryptic Coat deliberately has **no equip ability** (Scryfall ruling 2024-02-02: "Without
 * assistance from other cards, there's no way to attach it to a creature other than with its first
 * triggered ability"), so nothing here calls `equipAbility`. The {1}{U} bounce is the card's whole
 * engine: return it, recast it, cloak another card.
 *
 * "Can't be blocked" is the unconditional [CantBeBlocked], not a `CantBeBlockedBy` restriction —
 * both halves of the static are scoped to [Filters.EquippedCreature] so they follow the Coat when
 * it moves.
 */
val CrypticCoat = card("Cryptic Coat") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Artifact — Equipment"
    oracleText = "When this Equipment enters, cloak the top card of your library, then attach this " +
        "Equipment to it. (To cloak a card, put it onto the battlefield face down as a 2/2 creature " +
        "with ward {2}. Turn it face up any time for its mana cost if it's a creature card.)\n" +
        "Equipped creature gets +1/+0 and can't be blocked.\n" +
        "{1}{U}: Return this Equipment to its owner's hand."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.Composite(
            GatherCardsEffect(
                source = CardSource.TopOfLibrary(DynamicAmount.Fixed(1)),
                storeAs = "crypticCoatCloak",
            ),
            MoveCollectionEffect(
                from = "crypticCoatCloak",
                destination = CardDestination.ToZone(Zone.BATTLEFIELD),
                faceDown = FaceDownMode.CLOAK,
                storeMovedAs = "crypticCoatCloaked",
            ),
            Effects.AttachEquipment(EffectTarget.PipelineTarget("crypticCoatCloaked")),
        )
        description = "When this Equipment enters, cloak the top card of your library, then attach " +
            "this Equipment to it."
    }

    staticAbility {
        ability = ModifyStats(1, 0, Filters.EquippedCreature)
    }
    staticAbility {
        ability = CantBeBlocked(Filters.EquippedCreature)
    }

    activatedAbility {
        cost = Costs.Mana("{1}{U}")
        effect = Effects.ReturnToHand(EffectTarget.Self)
        description = "Return this Equipment to its owner's hand."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "50"
        artist = "Julia Metzger"
        imageUri = "https://cards.scryfall.io/normal/front/0/c/0c3d7e2c-a104-4757-9984-fb31088f92c4.jpg?1783912915"
    }
}
