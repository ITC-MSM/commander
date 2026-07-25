package com.wingedsheep.sdk.scripting.effects

import com.wingedsheep.sdk.scripting.GameObjectFilter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A side-effect that attaches to a spell when mana carrying this rider is spent on it.
 *
 * Riders are orthogonal to [ManaRestriction]:
 *  - A [ManaRestriction] controls *where* mana may be spent.
 *  - A [ManaSpellRider] controls *what happens to the spell* when the mana is spent.
 *
 * The set of riders consumed during payment is collected by the cast pipeline and
 * applied to the spell as it goes on the stack.
 */
@Serializable
sealed interface ManaSpellRider {
    val description: String

    /**
     * "That spell can't be countered." (Cavern of Souls)
     *
     * Translates to stamping `CantBeCounteredComponent` on the spell at cast time.
     */
    @SerialName("MakesSpellUncounterable")
    @Serializable
    data object MakesSpellUncounterable : ManaSpellRider {
        override val description: String = "That spell can't be countered"
    }

    /**
     * "When that mana is spent to cast a creature spell that shares a creature type
     * with your commander, scry [amount]." (Path of Ancestry)
     *
     * On consumption the cast pipeline checks the spell's projected creature subtypes
     * against the spell controller's commander(s). If the spell is a creature spell and
     * shares at least one creature subtype with any of the controller's commanders, a
     * triggered ability is placed on the stack above the spell that, on resolution,
     * scries [amount].
     *
     * If the rider is consumed but the spell doesn't satisfy the condition (non-creature
     * spell, no shared creature type, controller has no commander, etc.) the rider is
     * a no-op. Per CR / Scryfall ruling, creature types are checked at the moment of
     * casting, not at trigger-resolution.
     */
    @SerialName("ScryOnSharedTypeWithCommander")
    @Serializable
    data class ScryOnSharedTypeWithCommander(val amount: Int = 1) : ManaSpellRider {
        override val description: String =
            "When that mana is spent to cast a creature spell that shares a creature type with your commander, scry $amount"
    }

    /**
     * "When that mana is spent to cast a [spellFilter] spell, copy that spell and you may choose
     * new targets for the copy." (Pyromancer's Goggles, with
     * `spellFilter = GameObjectFilter.InstantOrSorcery.withColor(Color.RED)`.)
     *
     * On consumption the cast pipeline matches the spell against [spellFilter] using its cast
     * characteristics. On a match, a triggered ability is placed on the stack **above** the spell
     * that, on resolution, copies it (CR 707.10) — so the copy resolves before the original, which
     * is the printed behavior. The copy's controller may choose new targets.
     *
     * The rider is a no-op when the spell doesn't match (e.g. the {R} paid for a creature spell).
     * Consuming two riders copies the spell twice, one independent copy per rider.
     *
     * @property spellFilter Which cast spells the rider copies.
     */
    @SerialName("CopySpellWhenSpent")
    @Serializable
    data class CopySpellWhenSpent(
        val spellFilter: GameObjectFilter
    ) : ManaSpellRider {
        override val description: String =
            "When that mana is spent to cast a ${spellFilter.description} spell, copy that spell " +
                "and you may choose new targets for the copy"
    }
}
