package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.CostReductionSource
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.PreventDamage
import com.wingedsheep.sdk.scripting.SpellCostTarget
import com.wingedsheep.sdk.scripting.events.DamageType
import com.wingedsheep.sdk.scripting.events.RecipientFilter

/**
 * Diamond Weapon
 * {7}{G}{G}
 * Legendary Artifact Creature — Elemental
 * 8/8
 * This spell costs {1} less to cast for each permanent card in your graveyard.
 * Reach
 * Immune — Prevent all combat damage that would be dealt to Diamond Weapon.
 *
 * Three existing primitives, no new mechanic:
 *   - A self-cast [ModifySpellCost] reducing generic by [CostReductionSource]
 *     `CardsInGraveyardMatchingFilter(Permanent)` — "{1} less for each permanent card in your
 *     graveyard" (CR 601.2f cost reduction).
 *   - [Keyword.REACH].
 *   - A continuous [PreventDamage] replacement (CR 615) scoped to combat damage
 *     ([DamageType.Combat]) dealt to itself ([RecipientFilter.Self]). Per Scryfall ruling this
 *     prevention isn't considered while a trampling attacker it blocks assigns lethal damage
 *     (CR 510.1c handles that at assignment time, before the replacement applies).
 */
val DiamondWeapon = card("Diamond Weapon") {
    manaCost = "{7}{G}{G}"
    colorIdentity = "G"
    typeLine = "Legendary Artifact Creature — Elemental"
    power = 8
    toughness = 8
    oracleText = "This spell costs {1} less to cast for each permanent card in your graveyard.\n" +
        "Reach\n" +
        "Immune — Prevent all combat damage that would be dealt to Diamond Weapon."

    keywords(Keyword.REACH)

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.SelfCast,
            modification = CostModification.ReduceGenericBy(
                CostReductionSource.CardsInGraveyardMatchingFilter(GameObjectFilter.Permanent)
            )
        )
    }

    replacementEffect(
        PreventDamage(
            appliesTo = EventPattern.DamageEvent(
                recipient = RecipientFilter.Self,
                damageType = DamageType.Combat
            )
        )
    )

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "183"
        artist = "Esuthio"
        flavorText = "A tutelary creature said only to appear when the planet is in grave danger."
        imageUri = "https://cards.scryfall.io/normal/front/6/c/6ce7f494-2a19-4b11-94d4-fc5e5a7068bd.jpg?1748706442"
    }
}
