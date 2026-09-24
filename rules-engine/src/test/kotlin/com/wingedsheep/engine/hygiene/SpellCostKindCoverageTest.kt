package com.wingedsheep.engine.hygiene

import com.wingedsheep.engine.mechanics.cost.spell.SpellCosts
import com.wingedsheep.sdk.scripting.AdditionalCost
import com.wingedsheep.sdk.scripting.costs.CostAtom
import io.kotest.core.spec.style.FunSpec
import java.lang.reflect.Modifier
import kotlin.reflect.KClass

/**
 * Guards the spell-cost plug-in contract: every concrete [AdditionalCost] subtype — and every
 * [CostAtom] an [AdditionalCost.Atom] can carry — has a registered
 * [com.wingedsheep.engine.mechanics.cost.spell.SpellCostKind] in [SpellCosts].
 *
 * The bug class this kills: a new cost subtype compiles cleanly, and before the kinds existed it fell
 * through `else -> {}` branches in the cast handler and enumerator — offered, validated and paid as
 * nothing at all. Now dispatch throws on an unregistered leaf, and this test makes that a build
 * failure instead of a mid-game one.
 *
 * If this fails with a missing type, write its kind (see `AtomCostKinds.kt` / `CastingCostKinds.kt`)
 * and register it in [SpellCosts]. An atom that is never a spell's cost registers
 * `AbilityOnlyAtomCostKind`.
 */
class SpellCostKindCoverageTest : FunSpec({

    test("every AdditionalCost subtype and every CostAtom has a registered SpellCostKind") {
        val discovered = (findLeafSealedSubclasses(AdditionalCost::class) - AdditionalCost.Atom::class) +
            findLeafSealedSubclasses(CostAtom::class)
        val missing = (discovered - SpellCosts.registeredLeafTypes())
            .map { it.qualifiedName ?: it.java.name }
            .sorted()
        if (missing.isNotEmpty()) {
            error(
                "Spell cost subtypes with no registered SpellCostKind (write one and register it in " +
                    "SpellCosts):\n" + missing.joinToString("\n") { "  - $it" }
            )
        }
    }

    test("SpellCosts registers no stale types") {
        val discovered = findLeafSealedSubclasses(AdditionalCost::class) + findLeafSealedSubclasses(CostAtom::class)
        val stale = (SpellCosts.registeredLeafTypes() - discovered)
            .map { it.qualifiedName ?: it.java.name }
            .sorted()
        if (stale.isNotEmpty()) {
            error("SpellCosts registers types that are not cost leaves:\n" + stale.joinToString("\n") { "  - $it" })
        }
    }
}) {
    companion object {
        /** Same walker as [EffectExecutorCoverageTest]. */
        private fun findLeafSealedSubclasses(base: KClass<*>): Set<KClass<*>> {
            require(base.isSealed) { "${base.qualifiedName} must be sealed to be walked reflectively" }
            val leaves = mutableSetOf<KClass<*>>()
            val queue: ArrayDeque<KClass<*>> = ArrayDeque(base.sealedSubclasses)
            while (queue.isNotEmpty()) {
                val current = queue.removeFirst()
                val children = current.sealedSubclasses
                if (children.isNotEmpty()) {
                    queue.addAll(children)
                    continue
                }
                val javaClass = current.java
                if (javaClass.isInterface) continue
                if (Modifier.isAbstract(javaClass.modifiers)) continue
                leaves.add(current)
            }
            return leaves
        }
    }
}
